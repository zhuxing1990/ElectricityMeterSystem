package com.vunke.electricity.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import java.io.File

/**
 * Created by zhuxi on 2019/10/9.
 */
class SendSMS_SQLite (context: Context): ManagedSQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    var TAG = "SendSMS_SQLite"
    companion object {
        val DATABASE_NAME = "sms.db"
        val DATABASE_VERSION = 5
        private var instance : SendSMS_SQLite? = null
        @Synchronized
        fun getInstance(context: Context) : SendSMS_SQLite{
            if(instance == null){
                instance = SendSMS_SQLite(context.applicationContext)
            }
            return instance!!
        }
    }
    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.createTable(SMS_Title.TABLE_NAME,true,
                    SMS_Title._ID  to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
//                                SMS_Title.USER_ID to INTEGER,
                                SMS_Title.METER_NO to TEXT+UNIQUE,
                                SMS_Title.SEND_TIME to TEXT,
                                SMS_Title.SMS_TYPE to INTEGER)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    override fun getReadableDatabase(): SQLiteDatabase {
        if (!MeterSQLite.mainTmpDirSet) {
            val rs = File("/data/data/com.vunke.electricity/databases/main").mkdir()
            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory='/data/data/com.vunke.electricity/databases/main'")
            MeterSQLite.mainTmpDirSet = true
            return super.getReadableDatabase()
        }
        return super.getReadableDatabase()
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try{
            db.dropTable(MeterTitle.TABLE_NAME, true)
            onCreate(db)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}