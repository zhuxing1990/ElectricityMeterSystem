package com.vunke.electricity.device

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.text.format.Formatter
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.vunke.electricity.base.initUtil
import com.vunke.electricity.util.LogUtil
import com.vunke.electricity.util.VersionUtil
import java.io.File
import java.text.NumberFormat


/**
 * Created by zhuxi on 2019/9/23.
 */
object UpgradeUtil{
    var TAG = "UpgradeUtil"
    fun startUpgrade(context: Context,download_url:String){
        if (TextUtils.isEmpty(download_url)||!download_url.contains(".apk")){
            LogUtil.i(TAG,"get download url is null")
            return
        }
        var numberFormat = NumberFormat.getPercentInstance()
        //设置百分数精确度2即保留两位小数
        numberFormat.setMinimumFractionDigits(2)

        OkGo.get<File>(download_url).tag("startUpgrade")
                .execute(object :FileCallback(){
                    override fun downloadProgress(progress: Progress?) {
                        super.downloadProgress(progress)
                        val totalSize = Formatter.formatFileSize(context, progress!!.totalSize)
                        val currentSize = Formatter.formatFileSize(context, progress.currentSize)
                        val speed = String.format("%s/s", Formatter.formatFileSize(context, progress.speed))
                        LogUtil.i(TAG, "onProgress: 已经下载:"+currentSize)
                        LogUtil.i(TAG, "onProgress: 当前文件大小:"+totalSize)
                        LogUtil.i(TAG, "onProgress: 下载速度:"+speed)
                        val fraction = numberFormat.format(progress.fraction.toDouble())
                        LogUtil.i(TAG, "onProgress: fraction:"+fraction)
                    }
                    override fun onSuccess(response: Response<File>?) {
                      try {
                          var file = response!!.body()
                          if (file.exists()) {
                              val versionCode = VersionUtil.getVersionCode("com.android.silenceinstaller", context)
                              val intents = Intent()
                              if (versionCode != -1) {
                                  LogUtil.i(TAG,"发送重启应用的命令")
                                  initUtil.initRestart(context);
                                  // 执行静默安装
                                  intents.action = "com.android.SilenceInstall.Start"
                                  intents.`package` = "com.android.silenceinstaller"
                                  intents.setDataAndType(Uri.fromFile(response.body()),
                                          "application/vnd.android.package-archive")
                                  context.startService(intents)
                                  LogUtil.i(TAG, "onSuccess: start install:" + response.body().name)
                              } else {
                                  LogUtil.i(TAG,"发送重启应用的命令")
                                  initUtil.initRestart(context);
                                  intents.action = Intent.ACTION_VIEW
                                  intents.setDataAndType(Uri.parse("file://" + response.body().toString()),
                                          "application/vnd.android.package-archive")
                                  intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                  context.startActivity(intents)
                                  LogUtil.i(TAG, "onSuccess: start install:" + response.body().name)

                              }

                          }
                      }catch (e:Exception){
                          e.printStackTrace()
                      }
                    }



                    override fun onError(response: Response<File>?) {
                        super.onError(response)
                        LogUtil.i(TAG,"download error, download_url:$download_url")

                    }


                })
    }

//    fun UpLoadDownloadLogUtil(context: Context){
//
//    }
}