package com.vunke.electricity.util

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log



/**
 * Created by zhuxi on 2019/10/6.
 */
object RebootUtil {
    var TAG = "RebootUtil"
    fun reboot( context: Context){
        reBoot2()
    }

    private fun reboot1(context: Context) {
        try {
            val intent2 = Intent()
            intent2.action = Intent.ACTION_REBOOT
            intent2.putExtra("nowait", 1)
            intent2.putExtra("interval", 1)
            intent2.putExtra("window", 0)
            LogUtil.i(TAG, "this is huawei send Broadcast :" + Intent.ACTION_REBOOT)
            context.sendBroadcast(intent2)
        } catch (e: Exception) {
            LogUtil.i(TAG, "this is reboot Broadcast ,start reboot failed")
            e.printStackTrace()
        }
    }

    /**
     * 通过Runtime，发送指令，重启系统，测试结果，不起作用，可能需要root
     */
    private fun reBoot2() {

        try {
            Log.v(TAG, "root Runtime->reboot")
            val proc = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot ")) //关机
            proc.waitFor()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private fun reBoot3(context: Context) {
        /*弹出重启设备菜单 */
        Log.v(TAG, "reBoot3")
        val pManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager //重启到fastboot模式
        pManager.reboot("重启")

    }

    /**
     * 关机
     */
    private fun shutDowm() {
        Log.v(TAG, "shutDowm")
        try {
            //获得ServiceManager类
            val ServiceManager = Class
                    .forName("android.os.ServiceManager")
            //获得ServiceManager的getService方法
            val getService = ServiceManager.getMethod("getService", java.lang.String::class.java)
            //调用getService获取RemoteService
            val oRemoteService = getService.invoke(null, Context.POWER_SERVICE)
            //获得IPowerManager.Stub类
            val cStub = Class.forName("android.os.IPowerManager\$Stub")
            //获得asInterface方法
            val asInterface = cStub.getMethod("asInterface", android.os.IBinder::class.java)
            //调用asInterface方法获取IPowerManager对象
            val oIPowerManager = asInterface.invoke(null, oRemoteService)
            //获得shutdown()方法
            val shutdown = oIPowerManager.javaClass.getMethod("shutdown", Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
            //调用shutdown()方法
            shutdown.invoke(oIPowerManager, false, true)
        } catch (e: Exception) {
            Log.e(TAG, e.toString(), e)
        }

    }
}