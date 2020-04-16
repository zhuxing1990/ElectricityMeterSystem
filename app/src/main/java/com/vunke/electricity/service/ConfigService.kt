package com.vunke.electricity.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.example.x6.serialportlib.SerialPort
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.vunke.electricity.base.BaseConfig
import com.vunke.electricity.base.BaseUrl
import com.vunke.electricity.dao.MeterDao
import com.vunke.electricity.device.ComPort
import com.vunke.electricity.device.DeviceUtil
import com.vunke.electricity.device.ElectrictyMeterUtil
import com.vunke.electricity.modle.ConfigInfoBean
import com.vunke.electricity.modle.MetersBean
import com.vunke.electricity.util.LogUtil
import com.vunke.electricity.util.MACUtil
import com.vunke.electricity.util.SPUtils
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * Created by zhuxi on 2019/9/19.
 */
class ConfigService : Service() {

    var TAG = "ConfigService"

    companion object {
        val STOP_QUERY_METER = "com.vunke.electricity.stop_query_meter"
        /**心跳频率 */
        var HEART_BEAT_RATE: Long = 360
        //间隙时间键名
        val IntervalTime = "interval_time"
        //初始化阀值
        var DefaultWaringValue: Long = 3
        //阀值键名
        val WaringValue = "waring_value"

        //是否查询
        val isQuery = true
    }
    var serialPort1: SerialPort? = null;
    var serialPort2: SerialPort? = null;
    var serialPort3: SerialPort? = null;
    var serialPort4: SerialPort? = null;
    var serialPort5: SerialPort? = null;

    override fun onCreate() {
        super.onCreate()
        LogUtil.i(TAG, "onCreate ")
         serialPort1 = ComPort.getInstance(applicationContext).initComPort(BaseConfig.COM1)
         serialPort2 = ComPort.getInstance(applicationContext).initComPort(BaseConfig.COM2)
         serialPort3 = ComPort.getInstance(applicationContext).initComPort(BaseConfig.COM3)
         serialPort4 = ComPort.getInstance(applicationContext).initComPort(BaseConfig.COM4)
         serialPort5 = ComPort.getInstance(applicationContext).initComPort(BaseConfig.COM5)
        initConfigInfo()
        initHeartBeat()
    }

    fun initConfigInfo() {
        LogUtil.i(TAG, "initConfigInfo")
        OkGo.get<String>(BaseUrl.INIT_URL + BaseUrl.GET_CONFIG_INFO).tag("ConfigInfo").execute(object : StringCallback() {
            override fun onSuccess(response: Response<String>?) {
                var s = response!!.body().toString()
                LogUtil.i(DeviceUtil.TAG, "initConfigInfo onSuccess:$s")
                if (!TextUtils.isEmpty(s)) {
                    try {
                        val bean = Gson().fromJson(response.body(), ConfigInfoBean::class.java)
                        if (bean != null) {
                            val code = bean.respCode
                            if (code == 2000) {
                                val bizBody = bean.bizBody
                                if (bizBody != null && bizBody.size != 0) {
                                    bizBody.forEach { it ->
                                        val key = it.configKey
                                        val value = it.configValue
                                        when (key) {
                                            "loopTime" -> {
                                                HEART_BEAT_RATE = value.toLong()
                                                SPUtils.putLong(application, IntervalTime, HEART_BEAT_RATE)
                                                LogUtil.i(TAG, "onSuccess: save intervalTime:" + HEART_BEAT_RATE)
                                            }
                                            "smsNotityRate" -> {
                                                DefaultWaringValue = value.toLong()
                                                SPUtils.putLong(application, WaringValue, DefaultWaringValue)
                                                LogUtil.i(TAG, "onSuccess: save DefaultWaringValue:" + DefaultWaringValue)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e:Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onError(response: Response<String>?) {
                super.onError(response)
                LogUtil.i(DeviceUtil.TAG, "initConfigInfo onError")
            }

        })
    }

    fun initHeartBeat() {
        LogUtil.i(TAG, "initHeartBeat")
        HEART_BEAT_RATE = SPUtils.getLong(application, IntervalTime, HEART_BEAT_RATE)
        LogUtil.i(TAG, "initHeartBeat: init HEART_BEAT_RATE:" + HEART_BEAT_RATE)
        DefaultWaringValue = SPUtils.getLong(application, WaringValue, DefaultWaringValue)
        LogUtil.i(TAG, "initHeartBeat: init DefaultWaringValue:" + DefaultWaringValue)

        Observable.interval(0, HEART_BEAT_RATE, TimeUnit.MINUTES)
//                .onBackpressureDrop()//效果与Drop类型一样
                .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object:DisposableObserver<Long>(){
                    override fun onNext(t: Long) {
                        LogUtil.i(TAG,"initHeartBeat onNext:"+t)
                        val mac = MACUtil.getSERIAL()
                        DeviceUtil.getTheMeter(mac, applicationContext)
                        startQuert()
                    }
                    override fun onComplete() {
                        LogUtil.i(TAG,"initHeartBeat onComplete")
                        dispose()
                        initHeartBeat()
                    }
                    override fun onError(e: Throwable) {
                        LogUtil.i(TAG,"initHeartBeat onError")
                        this.dispose()
                        initHeartBeat()
                    }
                })
    }

    var meterList: ArrayList<MetersBean>? = null
    private fun startQuert() {
        LogUtil.i(TAG, "startQuert")
        try {
            var meterDao = MeterDao.getInstance(applicationContext)
            var meters = meterDao.queryMeters()
            if (meters != null && meters.size != 0) {
                LogUtil.i(TAG, "startQuert meters:" + meters.toString())

                meterList = ArrayList()
                meters.forEach {
                    var metersBean = MetersBean()
                    var comPort = it.comPort
                    when (comPort) {
                        BaseConfig.COM1 -> {
                            metersBean.serialPort = serialPort1
                        }
                        BaseConfig.COM2 -> {
                            metersBean.serialPort = serialPort2
                        }
                        BaseConfig.COM3 -> {
                            metersBean.serialPort = serialPort3
                        }
                        BaseConfig.COM4 -> {
                            metersBean.serialPort = serialPort4
                        }
                        BaseConfig.COM5 -> {
                            metersBean.serialPort = serialPort5
                        }
                    }
                    metersBean.meter = it
                    meterList!!.add(metersBean)
                }
                if (meterList != null && meterList!!.size != 0) {
                    QueryData()
                } else {
                    LogUtil.i(TAG, "get meterList is null,restart query meter")
                    val mac = MACUtil.getSERIAL()
                    DeviceUtil.getTheMeter(mac, applicationContext)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    var observer :DisposableObserver<MetersBean>? = null
    private fun QueryData() {
        LogUtil.i(TAG, "startQuert meterList:${meterList.toString()}")
        DeviceRunnable.getInstance(applicationContext).setList(meterList!!)
        //fromIterable接收一个Iterable，每次发射一个元素（与for循环效果相同）
        val listObservable = Observable.fromIterable(meterList)
        //interval定时器，间隔1秒发射一次
        val timerObservable = Observable.interval(4, TimeUnit.SECONDS)
        //使用zip操作符合并两个Observable
        var zipObservable = Observable.zip(listObservable, timerObservable, object : BiFunction<MetersBean, Long, MetersBean> {
            override fun apply(t1: MetersBean, t2: Long): MetersBean {
                return t1;
            }
        })
        observer = object : DisposableObserver<MetersBean>() {
            override fun onError(e: Throwable) {
                LogUtil.i(TAG, "startQuert onError")
                dispose()
                observer = null
            }

            override fun onComplete() {
                LogUtil.i(TAG, "startQuert onComplete")
                dispose()
                observer = null
            }

            override fun onNext(t: MetersBean) {
                LogUtil.i(TAG, "startQuert onNext:${t.toString()}")
                if (t.serialPort != null && t.serialPort.inputStream != null) {
                    LogUtil.i(TAG, "startQuert start query ")
                    t.serialPort.sendData(ElectrictyMeterUtil.FrmatQueryCMD(t.meter.meterNo))
                }
            }
        }
       zipObservable.subscribeOn(Schedulers.io())
               .subscribe(observer!!)

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent!=null){
            var action = intent.action
            if (!TextUtils.isEmpty(action)){
                if (action.equals(STOP_QUERY_METER)){
                    if (observer!=null){
                        if (!observer!!.isDisposed()){
                            observer!!.dispose()
                            observer=null
                        }

                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented")
    }

}