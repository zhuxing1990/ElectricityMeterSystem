package com.vunke.electricity.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.vunke.electricity.util.LogUtil
import org.jetbrains.anko.db.*
import java.io.File


/**
 * Created by zhuxi on 2019/9/16.
 */
class MeterSQLite(context: Context?):
        ManagedSQLiteOpenHelper(context!!, DATABASE_NAME, null, DATABASE_VERSION) {
    var TAG = "MeterSQLite"
    companion object {
        val DATABASE_NAME = "meter.db"
        val DATABASE_VERSION = 4
        private var instance : MeterSQLite? = null
        @Synchronized
        fun getInstance(context: Context) : MeterSQLite{
            if(instance == null){
                instance = MeterSQLite(context.applicationContext)
            }
            return instance!!
        }

         var mainTmpDirSet = false
    }


    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.createTable(MeterTitle.TABLE_NAME, true,
        MeterTitle._ID  to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                    MeterTitle.METER_ID to INTEGER,
                    MeterTitle.METER_NO to TEXT +UNIQUE,
                    MeterTitle.BIGIN_CHECK_NUM to REAL,
                    MeterTitle.END_CHECK_NUM to REAL,
                    MeterTitle.END_CHECK_NUM_TWO to REAL,
                    MeterTitle.CHECK_DATE to TEXT,
                    MeterTitle.COLLECTOR_ID to TEXT,
                    MeterTitle.COM_PORT to TEXT,
                    MeterTitle.USER_ID to INTEGER,
                    MeterTitle.ROOM_LEVEL to TEXT,
                    MeterTitle.MAGNIFICATION to INTEGER
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        if (!mainTmpDirSet) {
            val rs = File("/data/data/com.vunke.electricity/databases/main").mkdir()
            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory='/data/data/com.vunke.electricity/databases/main'")
            mainTmpDirSet = true
            return super.getReadableDatabase()
        }
        return super.getReadableDatabase()
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try{
         LogUtil.i(TAG,"onUpgrade")
        db.dropTable(MeterTitle.TABLE_NAME, true)
        onCreate(db)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}