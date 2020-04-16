package com.vunke.electricity.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.vunke.electricity.server.WebServer;

/**
 * Created by zhuxi on 2019/9/16.
 */

public class WebService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            WebServer.startServer(this);
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
//        WebServer.stopServer();
    }
}
