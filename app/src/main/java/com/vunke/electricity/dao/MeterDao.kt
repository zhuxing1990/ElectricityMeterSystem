package com.vunke.electricity.dao

import android.content.ContentValues
import android.content.Context
import com.vunke.electricity.db.Meter
import com.vunke.electricity.db.MeterSQLite
import com.vunke.electricity.db.MeterTitle
import com.vunke.electricity.util.LogUtil
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update

/**
 * Created by zhuxi on 2019/9/17.
 */
class  MeterDao(mContext:Context){
    private  var  TAG = "MeterDao"
    var meterSQLite:MeterSQLite
    companion object {
        private var instance : MeterDao? = null
        @Synchronized
        fun getInstance(context: Context) : MeterDao{
            if(instance == null){
                instance = MeterDao(context)
            }
            return instance!!
        }
    }
    init {
//        meterSQLite = MeterSQLite(mContext)
        meterSQLite =  MeterSQLite.getInstance(mContext);
    }
    private fun converDomain2Map(data:Meter):MutableMap<String, String>{
        var result = mutableMapOf<String, String>()
        try {
            //难点2
            with(data){
//                result[MeterTitle._ID] = "$_id"
                result[MeterTitle.METER_ID] = "$meterId"
                result[MeterTitle.METER_NO] = meterNo
                result[MeterTitle.BIGIN_CHECK_NUM] = "$beginCheckNum"
                result[MeterTitle.END_CHECK_NUM] = "$endCheckNum"
                result[MeterTitle.END_CHECK_NUM_TWO] = "$endCheckNumTwo"
                result[MeterTitle.CHECK_DATE] = checkDate
                result[MeterTitle.COLLECTOR_ID] = collectorId
                result[MeterTitle.COM_PORT] = comPort
                result[MeterTitle.USER_ID] = "$userId"
                result[MeterTitle.ROOM_LEVEL] = roomLevel
                result[MeterTitle.MAGNIFICATION] = "$magnification"
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return result
    }

    /**
     * 保存数据到数据库中
     * */
    fun saveMeters(datas: List<Meter>):Long {
        LogUtil.i(TAG,"saveMeters ")
        //难点1
        var result: Long = -1
        try {
            meterSQLite.use {
                //难点3
                datas.forEach({
                    var meterNo = it.meterNo
                    var meters = queryMeter(meterNo);
                    if (meters==null|| 0==meters.size){
                        LogUtil.i(TAG,"saveMeters :get meters is null,insert data")
                        val varargs = converDomain2Map(it).map {
                            Pair(it.key, it.value)
                        }.toTypedArray()
                        result = insert(MeterTitle.TABLE_NAME, *varargs)
                    }else{
                        LogUtil.i(TAG,"saveMeters : get meters is exists,update data")
                        var value = "${MeterTitle.METER_NO}={meterNo}"
                        val varargs = converDomain2Map(it).map {
                            Pair(it.key, it.value)
                        }.toTypedArray()
                       update(MeterTitle.TABLE_NAME,*varargs).where(value,"meterNo" to meterNo).exec()
//                        val delete = delete(MeterTitle.TABLE_NAME, null, null);
//                        LogUtil.i(TAG,"delete:$delete")
//                        val varargs = converDomain2Map(it).map {
//                            Pair(it.key, it.value)
//                        }.toTypedArray()
//                        result = insert(MeterTitle.TABLE_NAME, *varargs)
                    }
                })
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return result
    }


    fun updateMeter(data:Meter):Int{
        var count = -1;
        try {
            meterSQLite.use {
                var varargs = ContentValues()
                varargs.put(MeterTitle.METER_ID,data.meterId)
//                varargs.put(MeterTitle.METER_NO,data.meterNo)
                varargs.put(MeterTitle.BIGIN_CHECK_NUM,data.beginCheckNum)
                varargs.put(MeterTitle.END_CHECK_NUM,data.endCheckNum)
                varargs.put(MeterTitle.END_CHECK_NUM_TWO,data.endCheckNumTwo)
                varargs.put(MeterTitle.CHECK_DATE,data.checkDate)
                varargs.put(MeterTitle.COLLECTOR_ID,data.collectorId)
                varargs.put(MeterTitle.COM_PORT,data.comPort)
                varargs.put(MeterTitle.USER_ID,data.userId)
                varargs.put(MeterTitle.ROOM_LEVEL,data.roomLevel)
                varargs.put(MeterTitle.MAGNIFICATION,data.magnification)
                var condition = "meterNo=${data.meterNo}"
                count = update(MeterTitle.TABLE_NAME,varargs,condition,null)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }
    fun updateMeters(datas: List<Meter>){
        try {
            meterSQLite.use {
                datas.forEach({
                    var meterNo = it.meterNo
                    var varargs = ContentValues()
                    varargs.put(MeterTitle.METER_ID,it.meterId)
                    varargs.put(MeterTitle.METER_NO,it.meterNo)
                    varargs.put(MeterTitle.BIGIN_CHECK_NUM,it.beginCheckNum)
                    varargs.put(MeterTitle.END_CHECK_NUM,it.endCheckNum)
                    varargs.put(MeterTitle.END_CHECK_NUM_TWO,it.endCheckNumTwo)
                    varargs.put(MeterTitle.CHECK_DATE,it.checkDate)
                    varargs.put(MeterTitle.COLLECTOR_ID,it.collectorId)
                    varargs.put(MeterTitle.COM_PORT,it.comPort)
                    varargs.put(MeterTitle.USER_ID,it.userId)
                    varargs.put(MeterTitle.ROOM_LEVEL,it.roomLevel)
                    varargs.put(MeterTitle.MAGNIFICATION,it.magnification)
                    var condition = "meterNo=$meterNo"
                    update(MeterTitle.TABLE_NAME,varargs,condition,null)
                })
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun deleteMeter(meterNo:String ):Int{
        var count = 0
        try {
            var condition = "meterNo=$meterNo"
            meterSQLite.use {
                count=  delete(MeterTitle.TABLE_NAME,condition,null)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }
    fun queryMeter(meterNo:String): List<Meter>? {
        var MetersList: List<Meter>? = null
        try {
            meterSQLite.use {
                val dailyRequest = "${MeterTitle.METER_NO} = {meterNo}"
                val selectQueryBuilder = select(MeterTitle.TABLE_NAME)
                        .where(dailyRequest, "meterNo" to meterNo)
                MetersList = selectQueryBuilder.parseList(object : MapRowParser<Meter> {
                    override fun parseRow(columns: Map<String, Any?>): Meter {
                        var meterId = columns[MeterTitle.METER_ID] as Long
                        var MeterNo = columns[MeterTitle.METER_NO] as String
                        var currCheckNum = columns[MeterTitle.BIGIN_CHECK_NUM] as Double
                        var preCheckNum = columns[MeterTitle.END_CHECK_NUM] as Double
                        var preCheckNumTwo = columns[MeterTitle.END_CHECK_NUM_TWO] as Double
                        var checkDate = columns[MeterTitle.CHECK_DATE] as String
                        var collectorId = columns[MeterTitle.COLLECTOR_ID] as String
                        var comPort = columns[MeterTitle.COM_PORT] as String
                        var userId = columns[MeterTitle.USER_ID] as Long
                        var roomLevel = columns[MeterTitle.ROOM_LEVEL] as String
                        var magnification = columns[MeterTitle.MAGNIFICATION] as Long
                        return Meter(
                            meterId ,MeterNo ,currCheckNum,preCheckNum,preCheckNumTwo,checkDate,collectorId,comPort,userId,roomLevel,magnification)
                    }
                })
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return MetersList
    }

    /**
     * @return  List<Meter>? 返回的数据可能为null
     * */
    fun queryMeters(): List<Meter>? {
        var MetersList: List<Meter>? = null
        try {
            meterSQLite.use {
                val selectQueryBuilder = select(
                        MeterTitle.TABLE_NAME,
//                        MeterTitle._ID,
                        MeterTitle.METER_ID,
                        MeterTitle.METER_NO,
                        MeterTitle.BIGIN_CHECK_NUM,
                        MeterTitle.END_CHECK_NUM,
                        MeterTitle.END_CHECK_NUM_TWO,
                        MeterTitle.CHECK_DATE,
                        MeterTitle.COLLECTOR_ID,
                        MeterTitle.COM_PORT,
                        MeterTitle.USER_ID,
                        MeterTitle.ROOM_LEVEL,
                        MeterTitle.MAGNIFICATION
                )
                //.whereArgs("")    设置查询条件
                //难点4
                MetersList = selectQueryBuilder.parseList(object : MapRowParser<Meter> {
                    override fun parseRow(columns: Map<String, Any?>): Meter {
//                        var _id = columns[MeterTitle._ID] as Long
                        var meterId = columns[MeterTitle.METER_ID] as Long
                        var meterNo = columns[MeterTitle.METER_NO] as String
                        var currCheckNum = columns[MeterTitle.BIGIN_CHECK_NUM] as Double
                        var preCheckNum = columns[MeterTitle.END_CHECK_NUM] as Double
                        var preCheckNumTwo = columns[MeterTitle.END_CHECK_NUM_TWO] as Double
                        var checkDate = columns[MeterTitle.CHECK_DATE] as String
                        var collectorId = columns[MeterTitle.COLLECTOR_ID] as String
                        var comPort = columns[MeterTitle.COM_PORT] as String
                        var userId = columns[MeterTitle.USER_ID] as Long
                        var roomLevel = columns[MeterTitle.ROOM_LEVEL] as String
                        var magnification = columns[MeterTitle.MAGNIFICATION] as Long
                        return Meter(
//                                _id,
                                meterId ,meterNo ,currCheckNum,preCheckNum,preCheckNumTwo,checkDate,collectorId,comPort,userId,roomLevel,magnification)
                    }
                })
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return MetersList
    }


}