package com.vunke.electricity.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vunke.electricity.device.DeviceUtil
import com.vunke.electricity.service.ConfigService
import com.vunke.electricity.service.WebService
import com.vunke.electricity.util.MACUtil

/**
 * Created by zhuxi on 2019/9/16.
 */
class MyReceiver :BroadcastReceiver(){
    companion object {
        var AppUpgrade = "com.vunke.electricity.appupgrade"
    }
    override fun onReceive(context: Context, intent: Intent)  {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            init(context)
        }else if (intent.action.equals(AppUpgrade)){
            init(context)
        }
    }

    private fun init(context: Context) {
        DeviceUtil.upgradeApp(context)
        val i = Intent()
        i.setClass(context, WebService::class.java)
        context.startService(i)
        val mac = MACUtil.getSERIAL()
        DeviceUtil.getTheMeter(mac, context)
        val i2 = Intent()
        i2.setClass(context, ConfigService::class.java)
        context.startService(i2)
    }

}