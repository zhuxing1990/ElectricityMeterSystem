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
 * Created by zhuxi on 2020/1/10.
 */

public class QueryStatusPage implements WebPage {
    private static final String TAG = "QueryStatusPage";
    @Override
    public void page(Context context, WebRequest request, PrintWriter out, OutputStream rawOut) throws Throwable {
        LogUtil.i(TAG, "page: get request");
        String meterNo = request.queryParameter(WebConfig.ACCOUNT_KEY);
        List<Meter> meters = MeterDao.Companion.getInstance(context).queryMeter(EncryptToolNew.DESDecrypt(meterNo, WebConfig.ENCRYPT_KEY));
        final ResponseData responseData = new ResponseData();
        if (meters==null&&meters.size()==0){
            responseData.setCode(400);
            responseData.setMessage("查询设备信息失败,无法获取设备信息");
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
                byte[] bytes = ElectrictyMeterUtil.FrmatStatusCMD(meter.getMeterNo());
                serialPort.sendData(bytes);
                responseData.setCode(200);
                responseData.setMessage("已经发送查询状态命令");
                sleep(2000);
                byte [] buff = new byte[1024];
                int read = serialPort.getInputStream().read(buff);
                if (read>0) {
                    byte[] byteArray = new byte[read];
                    System.arraycopy(buff, 0, byteArray, 0, read);
                    if (byteArray == null || byteArray.length == 0) {
                        LogUtil.i(TAG, "get Read data:  参数为空，无法解析数据");
                    }
                    LogUtil.i(TAG, "get Read data:" + Utils.bytesToHex(byteArray).toUpperCase());
                    LogUtil.i(TAG, "get Read size:" + byteArray.length);
                    if (byteArray.length == 19){
                        String meterNoData = ElectrictyMeterUtil.getDeviceMeterNo(byteArray);
                        LogUtil.i(TAG, "onDataReceived: getDeviceMeterNo：" + meterNoData);
                        if (!TextUtils.isEmpty(meterNoData)){
                            String meterStatus = ElectrictyMeterUtil.getMeterStatus(byteArray);
                            LogUtil.i(TAG,"onDataReceived getMeterStatus:"+meterStatus);
                            if (meterStatus.equals("off")){
                                DeviceUtil.INSTANCE.upLoadGATELog(context, meter, byteArray, 0, 1);
                            }else if(meterStatus.equals("on")){
                                DeviceUtil.INSTANCE.upLoadGATELog(context, meter, byteArray, 1, 1);
                            }
                            responseData.setData(meterNoData);
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
