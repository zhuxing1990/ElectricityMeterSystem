package com.vunke.electricity.server;


import android.content.Context;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2018-03-22.
 */
public interface WebPage {

    void page(Context context, WebRequest request, PrintWriter out, OutputStream rawOut) throws Throwable;
}
