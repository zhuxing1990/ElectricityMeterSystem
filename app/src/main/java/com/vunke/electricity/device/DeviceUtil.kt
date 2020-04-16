package com.vunke.electricity.device

import android.content.Context
import android.text.TextUtils
import com.example.x6.serialportlib.SerialPort
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.vunke.electricity.base.BaseUrl
import com.vunke.electricity.dao.MeterDao
import com.vunke.electricity.dao.SendSMS_Dao
import com.vunke.electricity.db.Meter
import com.vunke.electricity.db.SMS
import com.vunke.electricity.modle.*
import com.vunke.electricity.service.ConfigService
import com.vunke.electricity.util.*
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by zhuxi on 2019/9/17.
 */
object DeviceUtil{
    var TAG = "DeviceUtil"
    fun getTheMeter(mac:String,context: Context){
        LogUtil.i(TAG,"getTheMeter")
        OkGo.get<String>(BaseUrl.INIT_URL + BaseUrl.GET_THE_METER).tag("getTheMeter")
                .params("collectorId", mac)
                .params("versionCode",VersionUtil.getVersionCode("com.vunke.electricity",context))
                .execute(object : StringCallback() {
            override fun onSuccess(response: Response<String>) {
                var s = response.body().toString();
                LogUtil.i(TAG,"getTheMeter onSuccess:$s")
                if (!TextUtils.isEmpty(response.body())) {
                    try {
                        val bean = Gson().fromJson(response.body(), TheMeterBean::class.java)
                        if (bean != null) {
                            val code = bean.respCode
                            if (code == 2000) {
                                val bizBody = bean.bizBody
                                if (bizBody != null && bizBody.size != 0) {
                                    val list = ArrayList<Meter>()
                                    for (bizBodyBean in bizBody) {
                                        val meter = Meter(bizBodyBean.meterId, bizBodyBean.meterNo, bizBodyBean.beginCheckNum, bizBodyBean.endCheckNum, bizBodyBean.endCheckNumTwo,bizBodyBean.checkDate, bizBodyBean.collectorId, bizBodyBean.comPort,bizBodyBean.userId, bizBodyBean.roomLevel,bizBodyBean.magnification)
                                        list.add(meter)
                                    }
                                    val meterDao = MeterDao(context)
                                    val result = meterDao.saveMeters(list)
                                    LogUtil.i(TAG, "onSuccess: result:" + result)
                                }


                            } else {
                                LogUtil.i(TAG, "getTheMeter onSuccess: get device info  failed ")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

            override fun onError(response: Response<String>) {
                super.onError(response)
                LogUtil.i(TAG, "getTheMeter onError: ")
            }
        })
    }

    fun uploadMeterReading(context: Context, meterNo: String,beginCheckNum:Double,beginCheckNum2:Double,beginCheckCode: String){
        try {
//            var gson = Gson()
//            var strJson =  gson.toJson(meter,Meter::class.java)
//            var json = JSONObject(strJson)
            var json = JSONObject()
            json.put("versionCode",VersionUtil.getVersionCode("com.vunke.electricity",context))
            json.put("collectorId", MACUtil.getSERIAL())
            json.put("meterNo",meterNo)
            json.put("beginCheckNum",beginCheckNum)
            json.put("beginCheckNum2",beginCheckNum2)
            json.put("beginCheckCode",beginCheckCode)
            json.put("tableType",1)
            LogUtil.i(TAG,"uploadMeterReading json:${json.toString()}")
            OkGo.post<String>(BaseUrl.INIT_URL+BaseUrl.RECORD_UPLOAD_FO_METER_READING).tag("meterReading")
                    .upJson(json)
                    .execute(object :StringCallback(){
                        override fun onSuccess(response: Response<String>?) {
                            var s = response!!.body().toString();
                            LogUtil.i(TAG,"uploadMeterReading onSuccess:$s")
                        }

                        override fun onError(response: Response<String>?) {
                            super.onError(response)
                            LogUtil.i(TAG,"uploadMeterReading onError")
                        }
                    })
        }catch (e:JSONException){
            e.printStackTrace()
        }


    }

    fun upgradeApp(context: Context){
        LogUtil.i(TAG,"upgradeApp")
        var oldVersion = VersionUtil.getVersionCode("com.vunke.electricity",context)
        OkGo.get<String>(BaseUrl.INIT_URL+BaseUrl.GET_UPGRADE_INFO).tag("get_upgrade_info")
                .params("versionCode",oldVersion)
                .execute(object:StringCallback(){
                    override fun onSuccess(response: Response<String>?) {
                        var s = response!!.body().toString();
                        LogUtil.i(TAG,"upgradeApp onSuccess:$s")
                        if (!TextUtils.isEmpty(response.body())) {
                            try {
                                val bean = Gson().fromJson(response.body(), UpgradeInfoBean::class.java)
                                if (bean != null) {
                                    val code = bean.respCode
                                    if (code == 2000) {
                                        val bizBody = bean.bizBody
                                        if (bizBody != null && bizBody.size != 0) {
                                            bizBody.forEach {
                                                var status = it.status
                                                if (status.equals("1")){
                                                    var versionCode = it.versionCode
                                                    if(versionCode>oldVersion){
                                                        var download_url = it.url
                                                        LogUtil.i(TAG,"get download url")
                                                        UpgradeUtil.startUpgrade(context,download_url)
                                                    }else{
                                                        LogUtil.i(TAG,"don't upgrade  oldVersion:$oldVersion")
                                                    }
                                                }else if (status.equals("0")){
                                                    LogUtil.i(TAG,"get status is 0  ,stop upgrade")
                                                }
                                            }
                                        }
                                    }
                                }
                            }catch (e:Exception){
                             e.printStackTrace()
                            }
                        }
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                        LogUtil.i(TAG,"upgradeApp onError")
                    }
                })
    }

    fun getCostInfo(context:Context,meterNo: String) {
        LogUtil.i(TAG,"getCostInfo")
        OkGo.get<String>(BaseUrl.INIT_URL+BaseUrl.GET_COST_INFO).tag("getCostInfo")
                .params("versionCode",VersionUtil.getVersionCode("com.vunke.electricity",context))
                .execute(object:StringCallback(){
            override fun onSuccess(response: Response<String>?) {
                var s = response!!.body().toString();
                LogUtil.i(TAG,"getCostInfo onSuccess:$s")
                if (!TextUtils.isEmpty(response.body())) {
                    try {
                        val bean = Gson().fromJson(response.body(), CostInfoBean::class.java)
                        if (bean != null) {
                            val code = bean.respCode
                            if (code == 2000) {
                                val bizBody = bean.bizBody
                                if (bizBody != null && bizBody.size != 0) {
                                    bizBody.forEach {
                                       var costId =  it.costId
                                        when (costId){
                                            "1"->{
                                                var price = it.price
                                                LogUtil.i(TAG,"getCostInfo get price:$price")
//                                                getAccountList(context,meterNo,price)
                                                getBalance(context,meterNo,price)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }

            override fun onError(response: Response<String>?) {
                super.onError(response)
                LogUtil.i(TAG,"getCostInfo onError")
            }
        })

    }

    fun getBalance(context: Context,meterNo: String,price: Long){
        LogUtil.i(TAG,"getBalance")
        try {
            var json = JSONObject()
            json.put("meterNo",meterNo)
                .put("versionCode",VersionUtil.getVersionCode("com.vunke.electricity",context))
            var postRequest =  OkGo.post<String>(BaseUrl.INIT_URL+BaseUrl.BALANCE_INQUIRY).tag("balanceInquiry")
            var meters = MeterDao.getInstance(context).queryMeter(meterNo)
            LogUtil.i(TAG,"getBalance meters:${meters.toString()}")
            if (meters!=null&&meters.size!=0 ){
                meters.forEach {
                    meter ->
                    LogUtil.i(TAG,"getBalance meter:$meter")
//                    var userId = meter.userId
                    postRequest.upJson(json)
                            .execute(object : StringCallback() {
                                override fun onSuccess(response: Response<String>?) {
                                    LogUtil.i(TAG,"getBalance onSuccess :${response!!.body()}")
                                    var s = response.body().toString()
                                    if(!TextUtils.isEmpty(s)){
                                        try {
                                            var bean = Gson().fromJson(s,BalanceBean::class.java)
                                            if (bean!=null){
                                                var code = bean.respCode
                                                if (code == 2000){
                                                    val bizBody = bean.bizBody
                                                    bizBody.forEach{it ->
                                                            var amount = it.amount
                                                        var userMobile = it.userMobile
                                                        var userName = it.userName
                                                        LogUtil.i(TAG,"getBalance get amount:$amount")
                                                        if (amount<0){
                                                            var roomLevel = meter.roomLevel
                                                            var smsType = "3"
                                                            if (roomLevel.equals("0")){
                                                                LogUtil.i(TAG,"getBalance 欠费,关闸")
                                                                CloseMeter(context, meter)
                                                                getSendData(context, meterNo, smsType, userMobile, userName, amount)
                                                            }else if (roomLevel.equals("1")){
                                                                LogUtil.i(TAG,"getBalance VIP房间,不处理")
                                                                getSendData(context, meterNo, smsType, userMobile, userName, amount)
                                                            }else if (roomLevel.equals("9")){
                                                                LogUtil.i(TAG,"getBalance 内部房间,不断闸")
                                                                getSendData(context, meterNo, smsType, userMobile, userName, amount)
                                                            }
                                                        }else{
                                                            LogUtil.i(TAG,"getBalance 余额充足，开闸")
                                                            OpenMeter(context, meter)
                                                            var AvailableDays =getAvailableDays(meter, price, amount)
                                                            var DefaultWaringValue = SPUtils.getLong(context, ConfigService.WaringValue, ConfigService.DefaultWaringValue)
                                                            LogUtil.i(TAG, "getBalance: 短信阀值:$DefaultWaringValue")
                                                            LogUtil.i(TAG, "getBalance: 余额可用天数:$AvailableDays" )
                                                            if (AvailableDays<DefaultWaringValue){
                                                                LogUtil.i(TAG,"getBalance 余额不足")
                                                                var smsType = "4"
                                                                getSendData(context, meter.meterNo, smsType, userMobile, userName, amount)
                                                            } else{
                                                                LogUtil.i(TAG,"getBalance 余额大于阀值")
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }catch (e:Exception){
                                            e.printStackTrace()
                                        }
                                    }

                                }

                                override fun onError(response: Response<String>?) {
                                    super.onError(response)
                                    LogUtil.i(TAG,"getBalance onError")
                                }
                            })
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

//    private fun getSendData(context: Context, meterNo: String, smsType: String, userMobile: String, userName: String, amount: Float) {
//        var date = DateUtil.getTimesMonthmorning()
//        var dateFormat = SimpleDateFormat("yyyy-MM-dd")
//        var times = dateFormat.format(date)
//        var smsList = SendSMS_Dao.getInstance(context).queryData(meterNo, smsType.toLong())
//        if (smsList != null && smsList.size != 0) {
//            LogUtil.i(TAG, "getSendData get smsList success ")
//            smsList.forEach {
//                var sendTime = it.sendTime
//                if (sendTime.equals(times)) {
//                    LogUtil.i(TAG, "本月发送短信已经上限，无法再发送短信")
//                } else {
//                    LogUtil.i(TAG, "本月未发送短信,开始发送短信")
//                    SendMessage(context, smsType, userMobile, userName, amount.toString(), meterNo)
//                }
//            }
//        } else {
//            LogUtil.i(TAG, "get smsList failed ,start send mseeage")
//            SendMessage(context, smsType, userMobile, userName, amount.toString(), meterNo)
//        }
//    }
    private fun getSendData(context: Context, meterNo: String, smsType: String, userMobile: String, userName: String, amount: Float) {
        var smsList = SendSMS_Dao.getInstance(context).queryData(meterNo, smsType.toLong())
        if (smsList != null && smsList.size != 0) {
            LogUtil.i(TAG, "getSendData get smsList success ")
            smsList.forEach {
                var sendTime = it.sendTime.toLong()
                LogUtil.i(TAG,"getSendData 上次发送短信时间:$sendTime")
                var oldTime = DateUtil.getPastDate(7);
                LogUtil.i(TAG,"getSendData 上次发送短信时间:$sendTime")
                var today = DateUtil.getStringByFormat(oldTime,"yyyyMMdd").toLong();
                if (today<= sendTime){
                    LogUtil.i(TAG, "本周发送短信已经上限，无法再发送短信")
                }else{
                    LogUtil.i(TAG, "本周未发送短信,开始发送短信")
                    SendMessage(context, smsType, userMobile, userName, amount.toString(), meterNo)
                }
            }
        } else {
            LogUtil.i(TAG, "get smsList failed ,start send mseeage")
            SendMessage(context, smsType, userMobile, userName, amount.toString(), meterNo)
        }
    }
    private fun CloseMeter(context: Context, meter: Meter) {
        LogUtil.i(TAG,"start Meter Close : ${meter.meterNo}")
        var serialPort = ComPort.getInstance(context).initComPort(meter.comPort)
        var bytes = ElectrictyMeterUtil.FrmatCloseCMD(meter.meterNo)
        serialPort.sendData(bytes)
        upLoadGATELog(context,meter,bytes,0,1)
        getStatus(context,meter,serialPort)
    }
    private fun OpenMeter(context: Context, meter: Meter) {
        LogUtil.i(TAG,"start Meter Open: ${meter.meterNo}")
        var serialPort = ComPort.getInstance(context).initComPort(meter.comPort)
        var bytes = ElectrictyMeterUtil.FrmatOpenCMD(meter.meterNo)
        serialPort.sendData(bytes)
        upLoadGATELog(context,meter,bytes,1,1)
        getStatus(context,meter,serialPort)
    }
    public fun getStatus(context: Context,meter: Meter,serialPort: SerialPort){
        LogUtil.i(TAG,"getStatus")
        Observable.interval(2,TimeUnit.SECONDS)
                .subscribe(object:DisposableObserver<Long>(){
                    override fun onComplete() {
                        dispose()
                    }

                    override fun onError(e: Throwable) {
                        dispose()
                    }

                    override fun onNext(t: Long) {
                        LogUtil.i(TAG,"getStatus start get meter open status")
                        var bytes = ElectrictyMeterUtil.FrmatStatusCMD(meter.meterNo)
                        serialPort.sendData(bytes)
                        onComplete()
                    }
                })
    }
    fun upLoadGATELog(context: Context,meter: Meter,bytes: ByteArray,actionValue:Int,actionType:Int){
        try {
            LogUtil.i(TAG,"upLoadGATELog")
            var json = JSONObject()
           json .put("versionCode",VersionUtil.getVersionCode("com.vunke.electricity",context))
                .put("meterNo",meter.meterNo)
                .put("comPort",meter.comPort)
                .put("collectorId",meter.collectorId)
                .put("command",Utils.bytesToHex(bytes).toUpperCase())
                .put("actionValue",actionValue)
                .put("actionType",actionType)
            LogUtil.i(TAG,"upLoadGATELog json:${json.toString()}")
            OkGo.post<String>(BaseUrl.INIT_URL+BaseUrl.UPLOAD_GATE_LOG).tag("uploadLog")
                    .upJson(json)
                    .execute(object :StringCallback(){
                        override fun onSuccess(response: Response<String>?) {
                            var data = response!!.body().toString()
                            if (!TextUtils.isEmpty(data)){
                                LogUtil.i(TAG,"upLoadGATELog onSuccess:"+response!!.body().toString())
                            }
                        }

                        override fun onError(response: Response<String>?) {
                            super.onError(response)
                            LogUtil.i(TAG,"upLoadGATELog onError")
                        }

                    })
        }catch (e:Exception){
            e.printStackTrace()
        }
    }



    @Deprecated(
            "该方法已经弃用,请勿使用"
            ,ReplaceWith("getAccountList(a, b)", "getBalance")
            ,DeprecationLevel.HIDDEN
    )
    fun getAccountList(context:Context,meterNo: String,price:Long){
        LogUtil.i(TAG,"getAccountList")
        try {
            var postRequest =  OkGo.post<String>(BaseUrl.INIT_URL+BaseUrl.ACCOUNT_LIST).tag("account_list")
            var meters = MeterDao.getInstance(context).queryMeter(meterNo)
            if (meters!=null&&meters.size!=0){
            meters.forEach {meter ->
                postRequest.params("userId",meter.userId)
                postRequest.params("meterNo",meter.meterNo)
                           .params("versionCode",VersionUtil.getVersionCode("com.vunke.electricity",context))
                           .execute(object:StringCallback(){
                            override fun onSuccess(response: Response<String>?) {
                                var s = response!!.body().toString();
                                LogUtil.i(TAG,"getAccountList onSuccess:$s")
                                if (!TextUtils.isEmpty(response.body())) {
                                    try {
                                        val bean = Gson().fromJson(response.body(), AccountBean::class.java)
                                        if (bean != null) {
                                            val code = bean.respCode
                                            if (code == 2000) {
                                                val bizBody = bean.bizBody
                                                if (bizBody != null && bizBody.size != 0) {
                                                    bizBody.forEach {
                                                        var costId =    it.costId
                                                        when (costId){
                                                            "1"->{
                                                                var fee = it.usableBalance
                                                                if (fee<1){
                                                                    var roomLevel = meter.roomLevel
                                                                    if (roomLevel.equals("0")){
                                                                        LogUtil.i(TAG,"欠费,关闸")
                                                                        CloseMeter(context, meter)
                                                                        var smsType = "3"
                                                                        getUserInfo(context,meter.meterNo,smsType,fee)
                                                                    }else if (roomLevel.equals("1")){
                                                                        LogUtil.i(TAG,"VIP房间,不处理")
                                                                    }else if (roomLevel.equals("9")){
                                                                        LogUtil.i(TAG,"内部房间,不断闸")
                                                                    }
                                                                }else{
                                                                    var AvailableDays =getAvailableDays(meter, price, it.usableBalance)
                                                                    var  DefaultWaringValue = SPUtils.getLong(context, ConfigService.WaringValue, ConfigService.DefaultWaringValue)
                                                                    LogUtil.i(TAG, "getAccountList: 短信阀值:$DefaultWaringValue")
                                                                    LogUtil.i(TAG, "getAccountList: 余额可用天数:$AvailableDays" )
                                                                    if (AvailableDays<DefaultWaringValue){
                                                                        LogUtil.i(TAG,"余额不足")
                                                                        var smsType = "4"
                                                                        getUserInfo(context,meter.meterNo,smsType,fee)
                                                                    } else{
                                                                        LogUtil.i(TAG,"余额大于阀值,开闸")
                                                                        OpenMeter(context, meter)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            override fun onError(response: Response<String>?) {
                                super.onError(response)
                                LogUtil.i(TAG,"getAccountList onError")
                            }
                        })
            }
            }else{
                LogUtil.i(TAG,"getAccountList  get meter data is null")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }




    /**
     *上月抄表数 减去  当前抄表数  =  上月总电量
     *上月抄表数 减去  当前抄表数  乘以 电量单价  =  上月总电费
     *上月抄表数 减去  当前抄表数  乘以 电量单价  除以 当月天数  =   平均每天消费额
     *余额  除以   平均每天消费额    =    余额可用天数
     * 若  余额可用天数 小于可用天数阀值  发送短信
     */
    private fun getAvailableDays(meter: Meter, price: Long,usableBalance:Float): Double {
        LogUtil.i(TAG,"getAvailableDays")
        var beginCheckNum = meter.beginCheckNum
//        beginCheckNum = 1000.0 //测试参数
        LogUtil.i(TAG, "getAvailableDays 上月抄表数:$beginCheckNum")

        var endCheckNum = meter.endCheckNum
        LogUtil.i(TAG, "getAvailableDays 本月抄表数1:$endCheckNum")
        var nowCheckNum1 = endCheckNum - beginCheckNum
        LogUtil.i(TAG, "getAvailableDays 本月已用电量1:$nowCheckNum1")


        var endCheckNumTwo = meter.endCheckNumTwo
        LogUtil.i(TAG, "getAvailableDays 本月抄表数2:$endCheckNumTwo")
        var nowCheckNum2 = endCheckNumTwo - beginCheckNum
        LogUtil.i(TAG, "getAvailableDays 本月已用电量2:$nowCheckNum2")

        var magnification = meter.magnification
        LogUtil.i(TAG,"getAvailableDays 电表倍率:$magnification")
        var Price = price*0.01
        LogUtil.i(TAG, "getAvailableDays 电费单价:$Price 元")
       if (nowCheckNum1>nowCheckNum2){
           var endPrice = nowCheckNum1 * Price * magnification
           LogUtil.i(TAG, "getAvailableDays 本月总电费:$endPrice")

           var AveragePrice = endPrice / DifferenceDate()
           LogUtil.i(TAG, "getAvailableDays 平均每天消费额:$AveragePrice")
           LogUtil.i(TAG, "getAvailableDays 可用余额:$usableBalance")
//           var newBalance = usableBalance - endPrice;
//           LogUtil.i(TAG, "getAvailableDays 结算电费后还剩余额:$newBalance")
//           var AvailableDays = newBalance / AveragePrice
           var AvailableDays = usableBalance / AveragePrice
           LogUtil.i(TAG, "getAvailableDays 余额可用天数:$AvailableDays")
           return AvailableDays
       }else{
           var endPrice = nowCheckNum2 * Price * magnification
           LogUtil.i(TAG, "getAvailableDays 本月总电费:$endPrice")
           var AveragePrice = endPrice / DifferenceDate()
           LogUtil.i(TAG, "getAvailableDays 平均每天消费额:$AveragePrice")
           LogUtil.i(TAG, "getAvailableDays 可用余额:$usableBalance")
//           var newBalance = usableBalance - endPrice;
//           LogUtil.i(TAG, "getAvailableDays 结算电费后还剩余额:$newBalance")
//           var AvailableDays = newBalance / AveragePrice
           var AvailableDays = usableBalance / AveragePrice
           LogUtil.i(TAG, "getAvailableDays 余额可用天数:$AvailableDays")
           return AvailableDays
       }
    }
    fun DifferenceDate():Long{
        var lastMonth =DateUtil.getLastMonth()
        var endDate = Date()
        return DateUtil.getDifference(endDate,lastMonth)
    }
    fun getUserInfo(context: Context,meterNo:String,smsType: String,usableBalance:Float){
        try {
            var json = JSONObject()
//            json.put("userId",userId)
            json.put("versionCode",VersionUtil.getVersionCode("com.vunke.electricity",context))
            OkGo.post<String>(BaseUrl.INIT_URL+BaseUrl.GET_USER_INFO).tag("get_user_info")
                    .upJson(json)
                    .execute(object :StringCallback(){
                        override fun onSuccess(response: Response<String>?) {
                            var s = response!!.body().toString();
                            LogUtil.i(TAG,"getAccountList onSuccess:$s")
                            if (!TextUtils.isEmpty(response.body())) {
                                try {
                                    val bean = Gson().fromJson(response.body(), UserInfoBean::class.java)
                                    if (bean != null) {
                                        val code = bean.respCode
                                        if (code == 2000) {
                                            val bizBody = bean.bizBody
                                            if (bizBody != null ) {
                                                var userMobile = bizBody.userMobile
                                                var store = bizBody.userName
                                                var date =  DateUtil.getTimesMonthmorning()
                                                var dateFormat = SimpleDateFormat("yyyy-MM-dd")
                                                var  times =  dateFormat.format(date)
                                                var  smsList = SendSMS_Dao.getInstance(context).queryData(meterNo,smsType.toLong())
                                                if (smsList!=null&&smsList.size!=0){
                                                    LogUtil.i(TAG,"get smsList success ")
                                                    smsList.forEach {
                                                       var sendTime =  it.sendTime
                                                        if (sendTime.equals(times)){
                                                            LogUtil.i(TAG,"本月发送短信已经上限，无法再发送短信")
                                                        }else{
                                                            LogUtil.i(TAG,"本月未发送短信,开始发送短信")
                                                            SendMessage(context,smsType,userMobile,store,usableBalance.toString(),meterNo)
                                                        }
                                                    }
                                                }else{
                                                    LogUtil.i(TAG,"get smsList failed ,start send mseeage")
                                                    SendMessage(context,smsType,userMobile,store,usableBalance.toString(),meterNo)
                                                }
//                                                var first =  SPUtils.getString(context,"SendMessage","");
//                                                LogUtil.i(TAG,"first:$first")
//                                                var date =  DateUtil.getTimesMonthmorning()
//                                                var dateFormat = SimpleDateFormat("yyyy-MM-dd")
//                                                var  times =  dateFormat.format(date)
//                                                LogUtil.i(TAG,"times:$times")
//                                                if (TextUtils.isEmpty(first)){
//                                                    SendMessage(context,smsType,userMobile,store,usableBalance.toString())
//                                                    SPUtils.putString(context,"SendMessage",times)
//                                                }else if (first.equals(times)){
//                                                    LogUtil.i(TAG,"second time get userInfo ,don't send message")
//                                                }else{
//                                                    SendMessage(context,smsType,userMobile,store,usableBalance.toString())
//                                                    SPUtils.putString(context,"SendMessage",times)
//                                                }
                                            }
                                        }
                                    }
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                            }

                        }

                        override fun onError(response: Response<String>?) {
                            super.onError(response)
                            LogUtil.i(TAG,"getUserInfo onError")
                        }
                    })
        }catch (e:Exception){
            e.printStackTrace()
        }


    }

    fun SendMessage(context: Context,smsType:String,userMobile:String,store:String,fee:String, meterNo:String){
        try {
        var json = JSONObject()
            json.put("userMobile",userMobile)
            json.put("smsType",smsType)
            json.put("store",store)
            json.put("fee",fee)
            json.put("date", Utils.getDate())
            json.put("versionCode",VersionUtil.getVersionCode("com.vunke.electricity",context))
        OkGo.post<String>(BaseUrl.INIT_URL+BaseUrl.SEND_MESSAGE).tag("sendMessage")
                .upJson(json)
                .execute(object :StringCallback(){
                    override fun onSuccess(response: Response<String>?) {
                        var s = response!!.body().toString();
                        LogUtil.i(TAG,"SendMessage onSuccess:$s")
                        var dateFormat = SimpleDateFormat("yyyyMMdd")
                        var times = dateFormat.format(Date())
                        var sms = SMS(meterNo,smsType.toLong(),times)
                        SendSMS_Dao.getInstance(context).saveData(sms)
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                        LogUtil.i(TAG,"SendMessage onError")
                    }
                })
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}