package com.vunke.electricity.server.workpage;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.x6.serialportlib.SerialPort;
import com.google.gson.Gson;
import com.vunke.electricity.dao.MeterDao;
import com.vunke.electricity.db.Meter;
import com.vunke.electricity.device.ComPort;
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
 * Created by zhuxi on 2020/7/1.
 */

public class ChangetBaudRatePage implements WebPage {
    private static final String TAG = "ChangetBaudRatePage";
    private final String COMPORT = "comPort";
    @Override
    public void page(Context context, WebRequest request, PrintWriter out, OutputStream rawOut) throws Throwable {
        String meterNo = request.queryParameter(WebConfig.ACCOUNT_KEY);
        String comPort1 = request.queryParameter(COMPORT);
        boolean hasComPort = false;
        SerialPort serialPort = null;
        if (!TextUtils.isEmpty(comPort1)){
            hasComPort = true;
        }
        List<Meter> meters = MeterDao.Companion.getInstance(context).queryMeter(EncryptToolNew.DESDecrypt(meterNo, WebConfig.ENCRYPT_KEY));
        final ResponseData responseData = new ResponseData();
        if (meters==null&&meters.size()==0){
            responseData.setCode(400);
            responseData.setMessage("查询设备信息失败,无法修改波特率");
            String outData = new Gson().toJson(responseData);
            out.write(outData);
            out.close();
            return;
        }
        for (final Meter meter:meters) {
            try {
                String comPort = meter.getComPort();
                if (hasComPort){
                    serialPort = ComPort.Companion.getInstance(context).initComPort2400(comPort1);
                }else{
                    serialPort = ComPort.Companion.getInstance(context).initComPort2400(comPort);
                }
                Intent intent = new Intent(context, ConfigService.class);
                intent.setAction(ConfigService.Companion.getSTOP_QUERY_METER());
                context.startService(intent);
                DeviceRunnable.Companion.getInstance().pause0();
                byte[] bytes = WeiShenElectricityUtil.changetBaudRate(meter.getMeterNo());
                serialPort.sendData(bytes);
                responseData.setCode(200);
                responseData.setMessage("已经发送修改波特率设备命令");
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
                    responseData.setMessage(Utils.bytesToHex(byteArray).toUpperCase());
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
