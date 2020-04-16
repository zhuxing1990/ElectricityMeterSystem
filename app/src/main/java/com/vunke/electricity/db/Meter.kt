package com.vunke.electricity.db

/**
 * Created by zhuxi on 2019/9/17.
 */
data class Meter (
//                  var _id:Long,//主键
        var meterId:Long, //电表ID
        var meterNo:String,//电表编号
        var beginCheckNum:Double,//开始抄表数
        var endCheckNum:Double,//上次抄表数
        var endCheckNumTwo:Double,//上次抄表数
        var checkDate:String,//创建时间
        var collectorId:String,//采集器ID
        var comPort:String,
        var userId:Long,//用户ID
        var roomLevel:String,//房价等级
        var magnification:Long)//倍率