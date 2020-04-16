package com.vunke.electricity.dao

import android.content.Context
import com.vunke.electricity.db.SMS
import com.vunke.electricity.db.SMS_Title
import com.vunke.electricity.db.SendSMS_SQLite
import com.vunke.electricity.util.LogUtil
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update

/**
 * Created by zhuxi on 2019/10/9.
 */
class SendSMS_Dao(context: Context){

    private  var  TAG = "SendSMS_Dao"
    var sendSms_SQLite: SendSMS_SQLite
    companion object {
        private var instance : SendSMS_Dao? = null
        @Synchronized
        fun getInstance(context: Context) : SendSMS_Dao{
            if(instance == null){
                instance = SendSMS_Dao(context)
            }
            return instance!!
        }
    }
    init {
        sendSms_SQLite =  SendSMS_SQLite.getInstance(context);
    }


    private fun converDomain2Map(sms: SMS):MutableMap<String, String>{
        var result = mutableMapOf<String, String>()
        try {
            //难点2
            with(sms){
                result[SMS_Title.SEND_TIME] = sendTime
//                result[SMS_Title.USER_ID] = "$userId"
                result[SMS_Title.METER_NO] = meterNo
                result[SMS_Title.SMS_TYPE] = "$smsType"
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return result
    }
    fun saveData(sms: SMS):Long{
        var result: Long = -1
        try {
           var smsList =  queryData(sms.meterNo,sms.smsType)
            sendSms_SQLite.use {
                if (smsList==null|| 0==smsList.size){
                    LogUtil.i(TAG,"saveData :get sms data is null,insert data")
                    val varargs = converDomain2Map(sms).map {
                        Pair(it.key, it.value)
                    }.toTypedArray()
                    result = insert(SMS_Title.TABLE_NAME, *varargs)
                }else{
                    LogUtil.i(TAG,"saveData :get sms data is exists,update data")
                    updateData(sms)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return result
    }

    fun queryData(meterNo:String,smsType:Long):List<SMS>?{
        var SMS_List:List<SMS>? = null
        try {
            sendSms_SQLite.use {
                var value = "${SMS_Title.METER_NO}={meterNo}"+ " AND  ${SMS_Title.SMS_TYPE} = {smsType}"
                var selectQueryBuilder = select(SMS_Title.TABLE_NAME)
                        .where(value,"meterNo" to meterNo,"smsType" to smsType)
                SMS_List = selectQueryBuilder.parseList(object :MapRowParser<SMS>{
                    override fun parseRow(columns: Map<String, Any?>): SMS {
                        var meterNo = columns[SMS_Title.METER_NO] as String
//                        var user_id = columns[SMS_Title.USER_ID] as Long
                        var sms_type = columns[SMS_Title.SMS_TYPE] as Long
                        var send_time = columns[SMS_Title.SEND_TIME] as String
                        return SMS(meterNo,sms_type,send_time)
                    }
                })
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return SMS_List
    }

    fun updateData(sms:SMS){
      try {
          var value = "${SMS_Title.METER_NO}={meterNo}" +" AND  ${SMS_Title.SMS_TYPE} = {smsType}"
          sendSms_SQLite.use {
              val varargs = converDomain2Map(sms).map {
                  Pair(it.key, it.value)
              }.toTypedArray()
              update(SMS_Title.TABLE_NAME,*varargs).where(value,"meterNo" to sms.meterNo,"smsType" to sms.smsType).exec()
          }
      }catch (e:Exception){
          e.printStackTrace()
      }
    }

}