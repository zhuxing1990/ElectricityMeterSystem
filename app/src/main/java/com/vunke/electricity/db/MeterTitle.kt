package com.vunke.electricity.db

/**
 * Created by zhuxi on 2019/9/16.
 */
object MeterTitle{
    /**
     * _ID
     * 主键  自动增量 Int类型
     */
    var _ID  = "_id"
    /**
     * TABLE_NAME
     * 表名
     */
    val TABLE_NAME ="meter";
    var METER_ID = "meterId"
    var METER_NO = "meterNo"
    var BIGIN_CHECK_NUM = "beginCheckNum"
    var END_CHECK_NUM = "endCheckNum"
    var END_CHECK_NUM_TWO = "endCheckNumTwo"
    var CHECK_DATE ="checkDate"
    var COLLECTOR_ID="collectorId"
    var COM_PORT ="comPort"
    var USER_ID ="userId"
    var ROOM_LEVEL="roomLevel"
    var MAGNIFICATION="magnification"
}