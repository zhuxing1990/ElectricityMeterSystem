package com.vunke.electricity.activity

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.vunke.electricity.R
import com.vunke.electricity.dao.MeterDao
import com.vunke.electricity.db.Meter
import com.vunke.electricity.device.DeviceUtil
import com.vunke.electricity.device.DeviceUtil.uploadMeterReading
import com.vunke.electricity.device.ElectrictyMeterUtil
import com.vunke.electricity.util.LogUtil
import com.vunke.electricity.util.MACUtil
import com.vunke.electricity.util.Utils
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_home.*
import java.util.concurrent.TimeUnit

class HomeActivity :SerialPortActivity() {
    var TAG = "HomeActivity"
    override fun onDataReceived(buffer: ByteArray?, size: Int) {
        if (buffer == null || buffer.size == 0) {
            LogUtil.i(TAG, "onDataReceived:  参数为空，无法解析数据")
            return
        }
        LogUtil.i(TAG,"get Read data:${Utils.bytesToHex(buffer).toUpperCase()}")
        Toast.makeText(this, Utils.bytesToHex(buffer).toUpperCase(), Toast.LENGTH_SHORT).show()
        LogUtil.i(TAG, "onDataReceived: 获取参数长度:" + buffer.size)

        home_message.append("\n接收到设备的返回信息:${Utils.bytesToHex(buffer).toUpperCase()}")
        val buff = Utils.byteTo16String(buffer)
        LogUtil.i(TAG, "onDataReceived: buff:" + buff)
        if (buffer.size > 10 && buffer.size == 22) {
            LogUtil.i(TAG, "onDataReceived: 开始解析电量")
            home_message.append("\n开始解析电量")
            val num = buffer.size - 10
            val b1 = buffer[num]
            val b2 = buffer[num + 1]
            var b3 = buffer[buffer.size-1]
            if (ElectrictyMeterUtil.authCode(b1, b2,b3)) {
                LogUtil.i(TAG, "onDataReceived: 电量应答成功，数据长度正常")
                val hextodl = ElectrictyMeterUtil.getElectric(buffer)
                val hextodl2 = ElectrictyMeterUtil.getElectric2(buffer)
                LogUtil.i(TAG, "onDataReceived: hextodl：" + hextodl)
                LogUtil.i(TAG, "onDataReceived: hextodl2：" + hextodl2)
                home_message.append("\n当前已用电量：$hextodl，设备编号:${mymeter.meterNo}")
                mymeter.beginCheckNum = hextodl
                Toast.makeText(this, "当前已用电量" + hextodl, Toast.LENGTH_SHORT).show()
               var meterNo  =  ElectrictyMeterUtil.getMeterNo(buffer)
                mymeter.meterNo = meterNo
                uploadMeterReading(mcontext, meterNo,hextodl, hextodl2 , Utils.bytesToHex(buffer).toUpperCase());
            } else {
                LogUtil.i(TAG, "onDataReceived: b1:" + Integer.toHexString(b1.toInt()) + "\t  b2:" + Integer.toHexString(b2.toInt()))
                LogUtil.i(TAG, "onDataReceived: 获取正常应答 失败或者 数据域长度 不够")
            }
        } else {

        }
    }
    lateinit var mymeter:Meter;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        finish()
//        initSerial2()
//        initSerial()
//        initView()
//        QueryMeters();
    }

    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0x1120){
                mymeter= msg.obj as Meter
                home_message.append("\n开始查询设备用电量，设备编号${mymeter.meterNo}")
                var byte = ElectrictyMeterUtil.FrmatQueryCMD(mymeter.meterNo);
                WriteSerial2(byte)
            }
        }
    }
    private fun QueryMeters() {
        home_message.setText("正在查询本地设备信息")
       var meterDao = MeterDao.getInstance(mcontext)
        var meters = meterDao.queryMeters()
        if (meters != null && meters.size != 0) {
            val listObservable = Flowable.fromIterable(meters)
            val timerObservable = Flowable.interval(0,5000, TimeUnit.MILLISECONDS)
            Flowable.zip(listObservable, timerObservable, object : BiFunction<Meter, Long, String> {

                override fun apply(meter: Meter, t2: Long): String {
                    LogUtil.i(TAG, "apply: :" + meter.meterNo)
                    var message = Message.obtain()
                    message.what = 0x1120
                    message.obj = meter
                    handler.sendMessage(message)
                    return meter.meterNo
                }
            }).subscribe()
        }else{
            home_message.append("\n查询数据失败，正在在线同步数据")
            val mac = MACUtil.getSERIAL()
            DeviceUtil.getTheMeter(mac, this)
        }
    }
    fun initView(){

    }
}
