package com.vunke.electricity.server.workpage;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.x6.serialportlib.SerialPort;
import com.google.gson.Gson;
import com.vunke.electricity.dao.MeterDao;
import com.vunke.electricity.db.Meter;
import com.vunke.electricity.device.ComPort;
import com.vunke.electricity.device.DeviceUtil;
import com.vunke.electricity.device.ElectrictyMeterUtil;
import com.vunke.electricity.device.WeiShenElectricityUtil;
import com.vunke.electricity.server.WebPage;
import com.vunke.electricity.server.WebRequest;
import com.vunke.electricity.server.config.EncryptToolNew;
import com.vunke.electricity.server.config.ResponseData;
import com.vunke.electricity.server.config.WebConfig;
import com.vunke.electricity.service.ConfigService;
import com.vunke.electricity.service.DeviceRunnable;
import com.vunke.electricity.util.LogUtil;
import com.vunke.electricity.util.Utils;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by zhuxi on 2019/9/20.
 */

public class OpenDevicePage implements WebPage {
    private static final String TAG = "OpenDevicePage";
    @Override
    public void page(Context context, WebRequest request, final PrintWriter out, OutputStream rawOut) throws Throwable {
            LogUtil.i(TAG, "page: get request");
            String meterNo = request.queryParameter(WebConfig.ACCOUNT_KEY);
            List<Meter> meters = MeterDao.Companion.getInstance(context).queryMeter(EncryptToolNew.DESDecrypt(meterNo, WebConfig.ENCRYPT_KEY));
            final ResponseData responseData = new ResponseData();
            if (meters==null&&meters.size()==0){
                responseData.setCode(400);
                responseData.setMessage("查询设备信息失败,无法合闸");
                String outData = new Gson().toJson(responseData);
                out.write(outData);
                out.close();
                return;
            }
            for (final Meter meter:meters) {
                try {
                    String comPort = meter.getComPort();
                    final SerialPort serialPort = ComPort.Companion.getInstance(context).initComPort(comPort);
                    Intent intent = new Intent(context, ConfigService.class);
                    intent.setAction(ConfigService.Companion.getSTOP_QUERY_METER());
                    context.startService(intent);
                    DeviceRunnable.Companion.getInstance().pause0();
                    String brand = meter.getBrand();
                    byte[] bytes;
                    if (!TextUtils.isEmpty(brand)&& "1".equals(brand)){
                        bytes = WeiShenElectricityUtil.FrmatOpenCMD(meter.getMeterNo());
                        LogUtil.i(TAG,"WeiShen FrmatOpenCMD:"+ Utils.bytesToHex(bytes).toUpperCase());
                    }else{
                        bytes = ElectrictyMeterUtil.FrmatOpenCMD(meter.getMeterNo());
                        LogUtil.i(TAG,"FrmatOpenCMD:"+ Utils.bytesToHex(bytes).toUpperCase());
                    }
//                    byte[] bytes = ElectrictyMeterUtil.FrmatOpenCMD(meter.getMeterNo());
                    serialPort.sendData(bytes);
                    DeviceUtil.INSTANCE.upLoadGATELog(context,meter,bytes,1,0);
                    responseData.setCode(200);
                    responseData.setMessage("已经发送打开设备命令");
                    sleep(2000);
                    byte [] buff = new byte[1024];
                    int read = serialPort.getInputStream().read(buff);
                    if (read>0){
                        byte[] byteArray = new byte[read];
                        System.arraycopy(buff, 0, byteArray , 0, read);
                        if (byteArray == null || byteArray.length == 0) {
                            LogUtil.i(TAG, "get Read data:  参数为空，无法解析数据");
                        }
                        LogUtil.i(TAG,"get Read data:"+ Utils.bytesToHex(byteArray).toUpperCase());
                        LogUtil.i(TAG,"get Read size:"+byteArray.length);
                        if (byteArray.length == 16) {
                            String meterNoData = ElectrictyMeterUtil.getDeviceMeterNo(byteArray);
                            LogUtil.i(TAG, "onDataReceived: getDeviceMeterNo：" + meterNoData);
                            if (!TextUtils.isEmpty(meterNoData)){
                                boolean authMeter = ElectrictyMeterUtil.AuthMeter(byteArray);
                                if (authMeter){
                                    LogUtil.i(TAG,"执行命令成功");
                                    responseData.setMessage("合闸命令执行成功");
                                }else {
                                    LogUtil.i(TAG,"合闸命令执行失败");
                                }
                            }
                        }
                    }
                    String outData = new Gson().toJson(responseData);
                    out.write(outData);
                }catch (Exception e){
                    e.printStackTrace();
                    responseData.setCode(500);
                    responseData.setMessage("无法发送命令到设备，请检查设备配置信息或者当前设备是否存在故障");
                    String outData = new Gson().toJson(responseData);
                    out.write(outData);
                }finally {
                    out.close();
                }
            }
    }
}
