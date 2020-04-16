package com.vunke.electricity.device

import android.content.Context
import com.example.x6.serialportlib.SerialPort
import com.vunke.electricity.base.BaseConfig


/**
 * Created by zhuxi on 2019/9/19.
 */
class ComPort(context:Context) {

    companion object {
        private  var instance: ComPort? = null
        @Synchronized
        fun getInstance(mcontext: Context):ComPort{
            if (instance==null){
                return ComPort(mcontext)
            }
            return instance!!
        }
    }

    fun initComPort(comPort:String):SerialPort{
        var serialPort: SerialPort? =null
        try {
         serialPort = SerialPort(comPort,BaseConfig.BAUDRATE_1200,8,1, 'e'.toInt(),true)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return serialPort!!
    }

}