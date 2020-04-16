package com.vunke.electricity.server.workpage;

import android.content.Context;
import com.vunke.electricity.util.LogUtil;

import com.google.gson.Gson;
import com.vunke.electricity.dao.MeterDao;
import com.vunke.electricity.db.Meter;
import com.vunke.electricity.device.DeviceUtil;
import com.vunke.electricity.server.WebRequest;
import com.vunke.electricity.server.config.BaseWebPage;
import com.vunke.electricity.server.config.EncryptToolNew;
import com.vunke.electricity.server.config.ResponseData;
import com.vunke.electricity.server.config.WebConfig;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by zhuxi on 2019/9/21.
 */

public class RenewalBalancePage extends BaseWebPage {
    private static final String TAG = "RenewalBalancePage";
    @Override
    public void page(Context context, WebRequest request, PrintWriter out, OutputStream rawOut) throws Throwable {
        super.page(context, request, out, rawOut);
        LogUtil.i(TAG, "page: get request");
        String meterNo = request.queryParameter(WebConfig.ACCOUNT_KEY);
        List<Meter> meters = MeterDao.Companion.getInstance(context).queryMeter(EncryptToolNew.DESDecrypt(meterNo, WebConfig.ENCRYPT_KEY));
        final ResponseData responseData = new ResponseData();
        if (meters==null&&meters.size()==0){
            responseData.setCode(400);
            responseData.setMessage("查询设备信息失败,无法开闸");
            String outData = new Gson().toJson(responseData);
            out.write(outData);
            out.close();
            return;
        }else{
            responseData.setCode(200);
            responseData.setMessage("正在获取设备余额");
            String outData = new Gson().toJson(responseData);
            out.write(outData);
            out.close();
            for ( Meter meter:meters) {
                DeviceUtil.INSTANCE.getCostInfo(context,meter.getMeterNo());
                sleep(1000);
            }
        }

    }
}
