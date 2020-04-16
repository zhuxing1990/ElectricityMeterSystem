package com.vunke.electricity.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vunke.electricity.base.initUtil

/**
 * Created by zhuxi on 2019/10/16.
 */
class RestartActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUtil.UpgradeApp(this)
        initUtil.initWebService(this)
//        initUtil.initDeviceInfo(this)
        initUtil.initConfigService(this)
        finish()
    }
}