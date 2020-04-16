package com.vunke.electricity.server.workpage;

import android.content.Context;
import android.content.Intent;

import com.example.x6.serialportlib.SerialPort;
import com.google.gson.Gson;
import com.vunke.electricity.dao.MeterDao;
import com.vunke.electricity.db.Meter;
import com.vunke.electricity.device.ComPort;
import com.vunke.electricity.device.DeviceUtil;
import com.vunke.electricity.device.ElectrictyMeterUtil;
import com.vunke.electricity.modle.MeterQueryBean;
import com.vunke.electricity.server.WebRequest;
import com.vunke.electricity.server.config.BaseWebPage;
import com.vunke.electricity.server.config.EncryptToolNew;
import com.vunke.electricity.server.config.ResponseData;
import com.vunke.electricity.server.config.WebConfig;
import com.vunke.electricity.service.ConfigService;
import com.vunke.electricity.service.DeviceRunnable;
import com.vunke.electricity.util.LogUtil;
import com.vunke.electricity.util.MACUtil;
import com.vunke.electricity.util.Utils;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by zhuxi on 2019/10/12.
 */

public class QueryMeterPage extends BaseWebPage {
    private static final String TAG = "QueryMeterPage";
    @Override
    public void page(Context context, WebRequest request, PrintWriter out, OutputStream rawOut) throws Throwable {
        super.page(context, request, out, rawOut);
        String meterNo = request.queryParameter(WebConfig.ACCOUNT_KEY);
        List<Meter> meters = MeterDao.Companion.getInstance(context).queryMeter(EncryptToolNew.DESDecrypt(meterNo, WebConfig.ENCRYPT_KEY));
        final ResponseData responseData = new ResponseData();
        if (meters==null&&meters.size()==0){
            responseData.setCode(400);
            responseData.setMessage("查询设备信息失败");
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
                byte[] bytes = ElectrictyMeterUtil.FrmatQueryCMD(meter.getMeterNo());
                serialPort.sendData(bytes);
                responseData.setCode(200);
                responseData.setMessage("已经发送查询命令");

                sleep(2000);
                byte [] buff = new byte[1024];
                int read = serialPort.getInputStream().read(buff);
                if (read>0){
                    byte[] byteArray = new byte[read];
                    System.arraycopy(buff, 0, byteArray , 0, read);
                    if (byteArray == null || byteArray.length == 0) {
                        LogUtil.i(TAG, "get Read data:  参数为空，无法解析数据");
                    }
//                    double read1 = ReadDataUtil.INSTANCE.Read(byteArray);
//                    responseData.setData(read1);
                    LogUtil.i(TAG,"get Read data:"+ Utils.bytesToHex(byteArray).toUpperCase());
                    LogUtil.i(TAG,"get Read size:"+byteArray.length);
                    if (byteArray.length > 10 && byteArray.length == 22) {
                        LogUtil.i(TAG, "get Read : 开始解析电量");
                        int num = byteArray.length - 10;
                        byte b1 = byteArray[num];
                        byte b2 = byteArray[num + 1];
                        byte b3 = byteArray[byteArray.length-1];
                        if (ElectrictyMeterUtil.authCode(b1, b2,b3)) {
                            responseData.setMessage("获取电量信息成功");
                            LogUtil.i(TAG, "get Read data: 电量应答成功，数据长度正常");
                            double hextodl = ElectrictyMeterUtil.getElectric(byteArray);
                            double hextodl2 = ElectrictyMeterUtil.getElectric2(byteArray);
                            LogUtil.i(TAG, "get Read data: hextodl:" +hextodl);
                            LogUtil.i(TAG, "get Read data: hextodl2:" +hextodl2);
                            String getmeterNo = ElectrictyMeterUtil.getMeterNo(byteArray);
                            LogUtil.i(TAG, "get Read data: meterNo:"+getmeterNo);
                            MeterQueryBean meterQueryBean = new MeterQueryBean();
                            meterQueryBean.setBeginCheckNum(hextodl);
                            meterQueryBean.setBeginCheckNum2(hextodl2);
                            meterQueryBean.setMeterNo(getmeterNo);
                            meterQueryBean.setCollectorId(MACUtil.getSERIAL());
                            meterQueryBean.setBeginCheckCode(Utils.bytesToHex(byteArray).toUpperCase());
                            responseData.setData(meterQueryBean);
                            DeviceUtil.INSTANCE.uploadMeterReading(context,getmeterNo,hextodl,hextodl2,Utils.bytesToHex(byteArray).toUpperCase());
                        }
                    }
                }
                String outData = new Gson().toJson(responseData);
                out.write(outData);
                DeviceRunnable.Companion.getInstance().resume0();
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
