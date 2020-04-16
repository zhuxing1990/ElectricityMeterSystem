package com.vunke.electricity.base;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.vunke.electricity.util.LogUtil;
import com.vunke.electricity.util.LogcatHelper;
import com.vunke.electricity.util.RebootUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by zhuxi on 2019/9/9.
 */

    public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";
    private static BaseApplication application;
    @Override
    public void onCreate() {
        super.onCreate();
        LogcatHelper.getInstance(this).start();
        Log.i(TAG, "onCreate: ");
        this.application = this;
        initUtil.initWebService(this);
        initUtil.UpgradeApp(application);
        Observable.interval(6, TimeUnit.HOURS)
        .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                RebootUtil.INSTANCE.reboot(application);
            }

        });
        initDevices();
    }

    private void initDevices() {
//        initUtil.initDeviceInfo(this);
        initUtil.initConfigService(this);
        try {
            LogUtil.i(TAG,"initRestart");
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName("com.vunke.restart","com.vunke.restart.service.RestartService");
            intent.setComponent(componentName);
            startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
