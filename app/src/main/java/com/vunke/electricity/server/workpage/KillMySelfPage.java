package com.vunke.electricity.server.workpage;

import android.content.Context;

import com.google.gson.Gson;
import com.vunke.electricity.server.WebPage;
import com.vunke.electricity.server.WebRequest;
import com.vunke.electricity.server.config.ResponseData;
import com.vunke.electricity.util.LogcatHelper;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by zhuxi on 2019/12/28.
 */

public class KillMySelfPage implements WebPage {

    @Override
    public void page(Context context, WebRequest request, PrintWriter out, OutputStream rawOut) throws Throwable {
        ResponseData responseData = new ResponseData();
        responseData.setCode(200);
        responseData.setMessage("正在退出重启，请稍候……");
        String outData = new Gson().toJson(responseData);
        out.write(outData);
        out.close();
        LogcatHelper.getInstance(context).stop();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
