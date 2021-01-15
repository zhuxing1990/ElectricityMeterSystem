package com.vunke.electricity.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.vunke.electricity.base.initUtil
import com.vunke.electricity.util.LogUtil

/**
 * Created by zhuxi on 2019/10/7.
 */
class AppInstallReceiver : BroadcastReceiver() {
    var TAG = "AppInstallReceiver"
    override fun onReceive(context: Context, intent: Intent?) {
       if (intent!=null){
           val action = intent.action
           if (!TextUtils.isEmpty(action)){
               LogUtil.i(TAG, "get app package replaced:" + action!!)
               if (Intent.ACTION_PACKAGE_REPLACED.equals(action)){
                   val schemeSpecificPart = intent.data!!.schemeSpecificPart
                   LogUtil.i(TAG,"get package:$schemeSpecificPart")
                   if (schemeSpecificPart .equals(context.packageName)){
                       LogUtil.i(TAG,"upgrade success ,start app")
                       try {
                           LogUtil.i(TAG,"发送重启应用的命令")
                           initUtil.initRestart(context);
                       }catch (e:Exception){
                           e.printStackTrace()
                       }
                   }
               }else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)){
                   LogUtil.i(TAG,"应用被卸载")
                   val packageName = intent.data.schemeSpecificPart
                   LogUtil.i(TAG,"get package:$packageName")
                   if (packageName.equals(context.packageName)){
                       try {
                           LogUtil.i(TAG,"发送重启应用的命令")
                           initUtil.initRestart(context);
                       }catch (e:Exception){
                           e.printStackTrace()
                       }
                   }
               }
           }
       }
    }
}