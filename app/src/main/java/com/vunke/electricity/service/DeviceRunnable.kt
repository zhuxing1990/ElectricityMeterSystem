package com.vunke.electricity.service

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.vunke.electricity.dao.MeterDao
import com.vunke.electricity.db.Meter
import com.vunke.electricity.device.DeviceUtil
import com.vunke.electricity.device.ElectrictyMeterUtil
import com.vunke.electricity.device.WeiShenElectricityUtil
import com.vunke.electricity.modle.MetersBean
import com.vunke.electricity.util.LogUtil
import com.vunke.electricity.util.Utils
import java.io.IOException



/**
 * Created by zhuxi on 2019/9/21.
 */
class DeviceRunnable(context: Context):Runnable{


    companion object {
        var TAG = "DeviceRunnable"
        var instance: DeviceRunnable? = null
        private var mServerThread: Thread? = null
        var context: Context?=null
        @Synchronized
        fun getInstance(context: Context): DeviceRunnable {
            if (instance==null){
                instance = DeviceRunnable(context)
                start0(instance!!)
                this.context = context
            }
            return instance!!
        }
        @Throws(IOException::class)
        private fun start0(run :Runnable) {
            LogUtil.i(TAG,"start thread")
            if (mServerThread == null) {
                mServerThread = Thread(run)
                mServerThread!!.start()
            }
        }
    }
    var isRun = true;
     fun pause0(){
        LogUtil.i(TAG,"pause thread")
        if (isRun){
            isRun =!isRun
        }
    }
      fun resume0(){
        LogUtil.i(TAG,"resume thread")
        if (!isRun){
            isRun = !isRun
        }
    }

    @Throws(IOException::class)
    private fun stop0() {
        LogUtil.i(TAG,"stop thread")
        if (mServerThread == null) {
            return
        }
        mServerThread!!.interrupt()
    }

    var meterList:ArrayList<MetersBean>?= null
    fun setList(list:ArrayList<MetersBean>?){
        LogUtil.i(TAG,"setList :${list.toString()}")
        meterList = list
    }
    fun getList():ArrayList<MetersBean>{
        return meterList!!
    }

    override fun run() {
        while (isRun&&mServerThread!=null&&!mServerThread!!.isInterrupted){
            Thread.sleep(2000)
            for ((index,bean) in meterList!!.withIndex()){
                if(bean.serialPort!=null&&bean.serialPort.inputStream!=null){
//                                         LogUtil.i(TAG,"  posion$index  meter:${meters.toString()}")
                    var buffer = ByteArray(1024)
                    var size = bean.serialPort.inputStream.read(buffer)
                    if (size>0){
                        val bufData = ByteArray(size)
                        System.arraycopy(buffer, 0, bufData, 0, size)
                        LogUtil.d(TAG, "size: " + size + ",接收：" + Utils.bytesToHex(bufData).toUpperCase())
                        if (size<22){
                            LogUtil.i(TAG,"get size <22 ")
                            if(OldBuffer!=null){
                                LogUtil.d(TAG, "OldBuffer size:${OldBuffer!!.size},接收拼接前:${Utils.bytesToHex(OldBuffer).toUpperCase()}" )
                                var newBuff = concat2(OldBuffer!!,bufData)
                                LogUtil.d(TAG, "newBuff size:${newBuff.size},接收拼接后数据:${Utils.bytesToHex(newBuff).toUpperCase()}")
                                CopyBuff(newBuff, bean)
                            }
                            if (OldBuffer2!=null){
                                LogUtil.d(TAG, "OldBuffer2 size:${OldBuffer2!!.size},接收拼接前:${Utils.bytesToHex(OldBuffer2).toUpperCase()}" )
                                var newBuff = concat2(OldBuffer2!!,bufData)
                                LogUtil.d(TAG, "OldBuffer2 size:${newBuff.size},接收拼接后数据:${Utils.bytesToHex(newBuff).toUpperCase()}")
                                CopyBuff(newBuff, bean)
                            }
                        }
                        onDataReceived(bufData,bufData.size,bean.meter)

                    }
                }
            }
        }
    }

    private fun CopyBuff(newBuff: ByteArray, bean: MetersBean) {
        if (newBuff.size == 22) {
            onDataReceived(newBuff, newBuff.size, bean.meter)
            LogUtil.i(TAG, "接收拼接结束，清除OldBuffer")
            OldBuffer = null
            OldBuffer2 = null
        } else if (newBuff.size > 22) {
            var newBuff2 = ByteArray(22)
            var newBuff3 = ByteArray(22)
            LogUtil.i(TAG, "get size > 22")
            System.arraycopy(newBuff, 0, newBuff2, 0, 22)
            LogUtil.d(TAG, "newBuff2 size: " + newBuff2.size + ",接收拼接后截取前22位：" + Utils.bytesToHex(newBuff2).toUpperCase())
            onDataReceived(newBuff2, newBuff2.size, bean.meter)
            System.arraycopy(newBuff, newBuff.size - 22, newBuff3, 0, 22)
            LogUtil.d(TAG, "newBuff3 size: " + newBuff3.size + ",接收拼接后截取后22位：" + Utils.bytesToHex(newBuff3).toUpperCase())
            onDataReceived(newBuff3, newBuff3.size, bean.meter)
            OldBuffer = null
            OldBuffer2 = null
            LogUtil.i(TAG, "接收拼接结束，清除OldBuffer")
        } else if (newBuff.size < 22) {
            LogUtil.i(TAG,"接收拼接后的长度不够，继续拼接")
            OldBuffer2 = newBuff
        }
    }

    private fun getMeter1(b :ByteArray):ByteArray{
        if (b.size<22){
            return b
        }
        var newB = ByteArray(22);
        try {
            System.arraycopy(b,0,newB,0,22)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return newB
    }

    private fun concat2(a: ByteArray, b: ByteArray): ByteArray {
        val alen = a.size
        val blen = b.size
        if (alen == 0) {
            return b
        }
        if (blen == 0) {
            return a
        }
        val result = java.lang.reflect.Array.newInstance(a.javaClass.componentType, alen + blen) as ByteArray
        System.arraycopy(a, 0, result, 0, alen)
        System.arraycopy(b, 0, result, alen, blen)
        return result
    }
    var OldBuffer:ByteArray?=null
    var OldBuffer2:ByteArray?=null
    fun onDataReceived(buffer: ByteArray?,size:Int, meter: Meter?) {
        LogUtil.i(TAG,"onDataReceived")
        if (buffer == null || buffer.size == 0) {
            LogUtil.i(TAG, "onDataReceived:  参数为空，无法解析数据")
            return
        }
        try{
            LogUtil.i(TAG,"onDataReceived size${buffer.size} get Read data:${Utils.bytesToHex(buffer).toUpperCase()}")
//        val buff = Utils.byteTo16String(buffer)
//        LogUtil.i(TAG, "onDataReceived: buff:" + buff)
            if (buffer.size > 10 && buffer.size == 22) {
                LogUtil.i(TAG, "onDataReceived: 开始解析电量")
                var a = buffer[0]
                var b = buffer[1]
                var c = buffer[2]
                var d = buffer[3]
                if (ElectrictyMeterUtil.getFE_Code(a,b,c,d)){
                    LogUtil.i(TAG, "onDataReceived: 验证 前4位 FE 正常")
                    val num = buffer.size - 10
                    val b1 = buffer[num]
                    val b2 = buffer[num + 1]
                    var b3 = buffer[buffer.size-1]
                    if (ElectrictyMeterUtil.authCode(b1, b2,b3)) {
                        LogUtil.i(TAG, "onDataReceived: 电量应答成功，数据长度正常")
                        val hextodl = ElectrictyMeterUtil.getElectric(buffer)
                        val hextodl2 = ElectrictyMeterUtil.getElectric2(buffer)
                        LogUtil.i(TAG, "onDataReceived: hextodl:$hextodl" )
                        LogUtil.i(TAG, "onDataReceived: hextodl2:$hextodl2" )
                        var meterNo = ElectrictyMeterUtil.getMeterNo(buffer)
                        meter!!.meterNo = meterNo
                        LogUtil.i(TAG,"onDataReceived:$meterNo  ")
                        meter.beginCheckNum = hextodl
                        DeviceUtil.uploadMeterReading(context!!, meterNo,hextodl,hextodl2,Utils.bytesToHex(buffer).toUpperCase())
                        DeviceUtil.getCostInfo(context!!,meterNo)
                    } else {
                        LogUtil.i(TAG, "onDataReceived: b1:${Integer.toHexString(b1.toInt())}" +   "\t  b2:${Integer.toHexString(b2.toInt())}" )
                        LogUtil.i(TAG, "onDataReceived: 获取正常应答 失败或者 数据长度 不够")
                    }
                }else{
                    LogUtil.i(TAG, "onDataReceived: 验证 前4位 FE ,获取正常应答 失败")
                }
            }else if (buffer.size>10 && buffer.size ==20){
                OldBuffer = buffer
                LogUtil.i(TAG,"onDataReceived size20")
                val meterNo1 = WeiShenElectricityUtil.getMeterNo(buffer)
                Log.i(TAG, "onDataReceived size20 weishen meterNo:$meterNo1")
                val hextodl = ElectrictyMeterUtil.getElectric(buffer)
                val hextodl2 = ElectrictyMeterUtil.getElectric2(buffer)
                LogUtil.i(TAG, "onDataReceived size20 getElectric:weishen hextodl:$hextodl")
                LogUtil.i(TAG, "onDataReceived size20 getElectric:weishen hextodl2$hextodl2")
                meter!!.meterNo = meterNo1
                LogUtil.i(TAG,"onDataReceived:$meterNo1")
                meter.beginCheckNum = hextodl
                DeviceUtil.uploadMeterReading(context!!, meterNo1,hextodl,hextodl2,Utils.bytesToHex(buffer).toUpperCase())
                DeviceUtil.getCostInfo(context!!,meterNo1)
            }else if (buffer.size == 16) {
                LogUtil.i(TAG,"onDataReceived size16")
                val meterNo = ElectrictyMeterUtil.getDeviceMeterNo(buffer)
                LogUtil.i(TAG, "onDataReceived size16 getDeviceMeterNo1：" + meterNo)
                if (!TextUtils.isEmpty(meterNo)) {
                    val authMeter = ElectrictyMeterUtil.AuthMeter(buffer)
                    if (authMeter) {
                        LogUtil.i(TAG, "执行命令成功")
                    } else {
                        LogUtil.i(TAG, "执行命令失败")
                    }
                }else{
                    LogUtil.i(TAG,"onDataReceived  长度不够")
                    OldBuffer = buffer
                }
            } else if (buffer.size == 19) {
                LogUtil.i(TAG,"onDataReceived size19")
                val meterNo = ElectrictyMeterUtil.getDeviceMeterNo(buffer)
                LogUtil.i(TAG, "onDataReceived size19 getDeviceMeterNo2：" + meterNo)
                if (!TextUtils.isEmpty(meterNo)) {
                    val meterStatus = ElectrictyMeterUtil.getMeterStatus(buffer)
                    LogUtil.i(TAG, "onDataReceived size19 getMeterStatus:" + meterStatus)
                    var meters = MeterDao.getInstance(context!!).queryMeter(meterNo)
                    if (meters!=null && meters.size!=0)
                    meters.forEach {
                        var meter = it
                        when(meterStatus){
                            "off"->{
//                                DeviceUtil.upLoadGATELog(context!!, meter, buffer, 0, 1)
                            }
                            "on"->{
//                                DeviceUtil.upLoadGATELog(context!!, meter, buffer, 1, 1)
                            }
                            "error"->{

                            }
                        }
                    }
                }else{
                    LogUtil.i(TAG,"onDataReceived  长度不够")
                    OldBuffer = buffer
                }
            }else{
                LogUtil.i(TAG,"onDataReceived  长度不够")
                OldBuffer = buffer
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }




}
