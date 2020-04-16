package com.vunke.electricity.server.workpage;

import android.content.Context;

import com.google.gson.Gson;
import com.vunke.electricity.server.WebRequest;
import com.vunke.electricity.server.config.BaseWebPage;
import com.vunke.electricity.server.config.ResponseData;
import com.vunke.electricity.util.LogUtil;
import com.vunke.electricity.util.LogcatHelper;
import com.vunke.electricity.util.RebootUtil;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by zhuxi on 2019/9/20.
 */

public class RebootDevicePage extends BaseWebPage {
    private static final String TAG = "RebootDevicePage";
    @Override
    public void page(Context context, WebRequest request, PrintWriter out, OutputStream rawOut) throws Throwable {
        super.page(context, request, out, rawOut);
        LogUtil.i(TAG, "page: get request");
        RebootUtil.INSTANCE.reboot(context);
        ResponseData responseData = new ResponseData();
        responseData.setCode(200);
        responseData.setMessage("请求成功");
        String outData = new Gson().toJson(responseData);
        LogcatHelper.getInstance(context).stop();
        out.write(outData);
        out.write(outData);
        out.close();

    }
}
