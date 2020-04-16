package com.vunke.electricity.device

import com.vunke.electricity.service.DeviceRunnable
import com.vunke.electricity.util.LogUtil
import com.vunke.electricity.util.Utils

/**
 * Created by zhuxi on 2019/10/18.
 */
object ReadDataUtil{
    val TAG = "ReadDataUtil"
    fun Read(byteArray: ByteArray):Double{
        var hextodl = -0.1
        if (byteArray == null || byteArray.size == 0) {
            LogUtil.i(TAG, "Read:  参数为空，无法解析数据")
            return hextodl
        }
        try{
            LogUtil.i(TAG,"get Read data:${Utils.bytesToHex(byteArray).toUpperCase()}")
            LogUtil.i(TAG, "get Read size:${byteArray.size}")
            if (byteArray.size > 10 && byteArray.size == 22) {
                LogUtil.i(TAG, "Read: 开始解析电量")
                val num = byteArray.size - 10
                val b1 = byteArray[num]
                val b2 = byteArray[num + 1]
                var b3 = byteArray[byteArray.size-1]
                if (ElectrictyMeterUtil.authCode(b1, b2,b3)) {
                    LogUtil.i(TAG, "Read: 电量应答成功，数据长度正常")
                    hextodl = ElectrictyMeterUtil.getElectric(byteArray)
                    var hextodl2 = ElectrictyMeterUtil.getElectric(byteArray)
                    LogUtil.i(TAG, "Read: hextodl:$hextodl" )
                    LogUtil.i(TAG, "Read: hextodl2:$hextodl2" )
                    var meterNo = ElectrictyMeterUtil.getMeterNo(byteArray)
                    LogUtil.i(TAG,"Read:$meterNo  ")
                    DeviceUtil.uploadMeterReading(DeviceRunnable.context!!, meterNo,hextodl,hextodl2,Utils.bytesToHex(byteArray).toUpperCase())
                    DeviceUtil.getCostInfo(DeviceRunnable.context!!,meterNo)
//                    return hextodl
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
            return hextodl
        }
        return hextodl
    }
}