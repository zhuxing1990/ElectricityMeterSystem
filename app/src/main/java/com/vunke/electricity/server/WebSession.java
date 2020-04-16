package com.vunke.electricity.server;

import android.content.Context;

import com.vunke.electricity.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Administrator on 2018-03-22.
 */
public class WebSession implements Runnable {
    private static final String TAG = "WebSession";
    private long mCreateTime;
    private Socket mSocket;
    private Thread mThread;
    private Context mContext;
    public WebSession(Context context, Socket socket) {
        if (socket == null) {
            throw new NullPointerException("socket == null");
        }
        mContext = context;
        mCreateTime = System.currentTimeMillis();
        mSocket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream in = new BufferedInputStream(mSocket.getInputStream());
            OutputStream out = mSocket.getOutputStream();
            WebRequest webRequest = WebRequest.parse(in);
            mSocket.shutdownInput();
            webResponse(webRequest, out);
//            mSocket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                SessionManager.getInstance().remove(this);
        }
    }

    private void webResponse(WebRequest request, OutputStream out) {
        Class<? extends WebPage> webPageClass = WebPageMap.getInstance().getWebPage(request.getPageName());
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        // 重点:    writer 设置请求头，不设置的话，默认是没有请求头的，部分请求会直接判断请求CODE  为 -1
        writer.print("HTTP/1.1 200 OK \r\n");
        //设置内容类型   格式为UTF-8
        writer.print("Content-Type: application/json;charset=UTF-8 \r\n");
        // 返回连接关闭
        writer.print("Connection: close \r\n");
        writer.print("Access-Control-Allow-Origin: * \r\n");
        writer.print("Access-Control-Allow-Methods: * \r\n");
        writer.print("Access-Control-Allow-Headers: DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,key,x-biz,x-info,platinfo,encr,enginever,gzipped,poiid \n\r\n");
        try {
            if (webPageClass != null) {
                WebPage webPage = webPageClass.newInstance();
                LogUtil.i(TAG, "webResponse: start page: " + webPageClass.getSimpleName());
                webPage.page(mContext, request, writer, out);
                LogUtil.i(TAG, "webResponse: end page: " + webPageClass.getSimpleName());
            } else {
                writer.write("Error:404");//page not found
            }
        } catch (Throwable tr) {
            try {
                writer.write(tr.toString());
            } catch (Exception ignore) {
            }
        }
    }

    public synchronized void start() {
        if (mThread != null || mSocket == null) {
            return;
        }

        mThread = new Thread(this);
        mThread.setDaemon(true);
        mThread.start();

        SessionManager.getInstance().add(this);
    }

    public synchronized void close() {
        if (mThread == null) {
            return;
        }

        if (mThread.isAlive() || !mThread.isInterrupted()) {
            mThread.interrupt();
        }
        mThread = null;

        try {
            mSocket.close();
        } catch (IOException ignore) {
        }
        mSocket = null;
        SessionManager.getInstance().remove(this);
    }

    public long getCreateTime() {
        return mCreateTime;
    }

}
