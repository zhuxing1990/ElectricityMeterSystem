package com.vunke.electricity.util

import android.content.Context

/**
 * Created by zhuxi on 2019/9/18.
 */
object VersionUtil {

    fun getVersionCode(packageName:String,context: Context):Int{
        var versionCode = -1
        var packageManage = context.packageManager
        try {
            var packageInfo = packageManage.getPackageInfo(packageName,0)
            if (packageInfo != null){
                versionCode = packageInfo.versionCode
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        return versionCode
    }
    fun getVersionName(packageName:String,context: Context):String{
        var versionName = ""
        var packageManage = context.packageManager
        try {
            var packageInfo = packageManage.getPackageInfo(packageName,0)
            if (packageInfo != null){
                versionName = packageInfo.versionName
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        return versionName
    }
}