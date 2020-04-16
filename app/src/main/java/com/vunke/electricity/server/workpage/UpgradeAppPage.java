package com.vunke.electricity.server.workpage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Formatter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.vunke.electricity.device.DeviceUtil;
import com.vunke.electricity.receiver.MyReceiver;
import com.vunke.electricity.server.WebRequest;
import com.vunke.electricity.server.config.BaseWebPage;
import com.vunke.electricity.server.config.ResponseData;
import com.vunke.electricity.util.LogUtil;
import com.vunke.electricity.util.VersionUtil;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhuxi on 2019/9/23.
 */

public class UpgradeAppPage extends BaseWebPage {
    private static final String TAG = "UpgradeAppPage";
    private static final String PARAM_APP_URL = "appUrl";
    boolean isInstall = false;
    @Override
    public void page(final Context context, WebRequest request,final PrintWriter out, OutputStream rawOut) throws Throwable {
        super.page(context, request, out, rawOut);
        LogUtil.i(TAG, "page: get request");
        DeviceUtil.INSTANCE.upgradeApp(context);
        final ResponseData responseData = new ResponseData();
        String appUrl = request.queryParameter(PARAM_APP_URL);
        if (TextUtils.isEmpty(appUrl)){
            DeviceUtil.INSTANCE.upgradeApp(context);
            responseData.setCode(200);
            responseData.setMessage("已经接收到升级命令，正在获取升级信息");
            String outData = new Gson().toJson(responseData);
            out.write(outData);
            out.close();
        }else if (appUrl.contains(".apk")){
            OkGo.<File>get(appUrl).tag(this).execute(new FileCallback() {
                @Override
                public void onSuccess( Response<File> response) {
                    try{
                        BlockingQueue queue = new LinkedBlockingQueue();
                        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 1, TimeUnit.SECONDS, queue);
                        int versionCode = VersionUtil.INSTANCE.getVersionCode("com.android.silenceinstaller",context);
                        Intent intents = new Intent();
                        if (versionCode!=-1) {
                            // 执行静默安装
                            intents.setAction("com.android.SilenceInstall.Start");
                            intents.setPackage( "com.android.silenceinstaller");
                            intents.setDataAndType(Uri.fromFile(response.body()),
                                    "application/vnd.android.package-archive");
                            context.startService(intents);
                            LogUtil.i(TAG, "onSuccess: start install:"+response.body().getName());
                            executor.execute(new Runnable(){

                                @Override
                                public void run() {
                                    responseData.setCode(200);
                                    responseData.setMessage("正在静默安装应用中");
                                    String outData = new Gson().toJson(responseData);
                                    out.write(outData);
                                    out.close();
                                }
                            });
                            executor.shutdown();
                            isInstall = true;
                        } else {
                            intents.setAction(Intent.ACTION_VIEW);
                            intents.setDataAndType(Uri.parse("file://" + response.body().toString()),
                                    "application/vnd.android.package-archive");
                            context.startActivity(intents);
                            LogUtil.i(TAG, "onSuccess: start install:"+response.body().getName());
                            executor.execute(new Runnable(){
                                @Override
                                public void run() {
                                    responseData.setCode(200);
                                    responseData.setMessage("警告:未安装解耦插件，需要手动安装应用");
                                    String outData = new Gson().toJson(responseData);
                                    out.write(outData);
                                    out.close();
                                }
                            });
                            executor.shutdown();
                            isInstall = true;
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void downloadProgress(Progress progress) {
                    super.downloadProgress(progress);
                    String current_size = Formatter.formatFileSize(context, progress.currentSize);
                    String total_size = Formatter.formatFileSize(context, progress.totalSize);
                    String speed = String.format("%s/s",Formatter.formatFileSize(context,progress.speed));
                    LogUtil.i(TAG, "onProgress: 当前文件大小:" + total_size);
                    LogUtil.i(TAG, "onProgress: 已经下载:" + current_size);
                    LogUtil.i(TAG, "onProgress: 下载速度:" + speed);
                    NumberFormat numberFormat = NumberFormat.getPercentInstance();
                    //设置百分数精确度2即保留两位小数
                    numberFormat.setMinimumFractionDigits(2);
                    String fraction = numberFormat.format(progress.fraction);
                    LogUtil.i(TAG, "onProgress: 下载进度:" + fraction);
                }

                @Override
                public void onError(com.lzy.okgo.model.Response<File> response) {
                    super.onError(response);
                    LogUtil.i(TAG, "onError: ");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (isInstall){
                        Intent intent = new Intent(context, MyReceiver.class);
                        intent.setAction(MyReceiver.Companion.getAppUpgrade());
                        context.sendBroadcast(intent);
                    }
                }
            });
        }else{
            responseData.setCode(400);
            responseData.setMessage("解析app_url中的APK失败,无法升级指定APK");
            String outData = new Gson().toJson(responseData);
            out.write(outData);
            out.close();
        }

    }
}
