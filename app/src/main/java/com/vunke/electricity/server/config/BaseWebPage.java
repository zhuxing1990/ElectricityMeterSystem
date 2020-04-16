package com.vunke.electricity.server.config;

import android.content.Context;
import android.support.annotation.CallSuper;

import com.vunke.electricity.server.WebPage;
import com.vunke.electricity.server.WebRequest;

import java.io.OutputStream;
import java.io.PrintWriter;


/**
 * Created by Administrator on 2018-04-10.
 */

public class BaseWebPage implements WebPage {
    @CallSuper
    @Override
    public void page(Context context, WebRequest request, PrintWriter out, OutputStream rawOut) throws Throwable {
        WebConfig.checkAccount(request,out);
    }
}
