package com.vunke.electricity.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.vunke.electricity.device.DeviceUtil;
import com.vunke.electricity.service.ConfigService;
import com.vunke.electricity.service.WebService;
import com.vunke.electricity.util.LogUtil;
import com.vunke.electricity.util.MACUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by zhuxi on 2019/10/16.
 */

public class initUtil {
    private static final String TAG = "initUtil";
    public static void UpgradeApp(final Context context) {
        Observable.interval(0,1, TimeUnit.HOURS).subscribe(new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                LogUtil.i(TAG, "UpgradeApp onNext: ");
                DeviceUtil.INSTANCE.upgradeApp(context);
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG, "UpgradeApp onError: ");
            }

            @Override
            public void onComplete() {
                LogUtil.i(TAG, "UpgradeApp onComplete: ");
            }
        });
    }
    public static void initRestart(Context context) {
        try {
            LogUtil.i(TAG,"initRestart");
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName("com.vunke.restart","com.vunke.restart.service.RestartService");
            intent.setComponent(componentName);
            intent.setAction("com.vunke.electricity.restart");
            context.startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void initConfigService(Context context) {
        Intent intent2 =new  Intent(context, ConfigService.class);
        context.startService(intent2);
    }

    public static void initWebService(Context context) {
        Intent intent = new Intent(context, WebService.class);
        context.startService(intent);
    }

    public static void initDeviceInfo(final Context context) {
//        String mac = MACUtil.getMac();
        Observable.interval(0,2, TimeUnit.HOURS).subscribe(new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                LogUtil.i(TAG, "initDeviceInfo onNext: start init");
                String mac = MACUtil.getSERIAL();
                DeviceUtil.INSTANCE.getTheMeter(mac,context);
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG, "initDeviceInfo onError: ");
                dispose();
                initDeviceInfo(context);
            }

            @Override
            public void onComplete() {
                LogUtil.i(TAG, "initDeviceInfo onComplete: ");
            }
        });

    }
}
