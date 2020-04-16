package com.vunke.electricity.base;

/**
 * Created by zhuxi on 2019/9/16.
 */

public class BaseUrl {
//    public static final String INIT_URL =  "http://148.70.154.88";//云平台
//    public static final String INIT_URL =  "http://220.168.29.202:8082/pm";//本地外网
    public static final String INIT_URL =  "http://192.168.8.2:8082/pm";//本地内网

    public static final String  GET_THE_METER = "/hardware/getTheMeter";

    public static final String  RECORD_UPLOAD_FO_METER_READING =  "/hardware/recordUploadOfMeterReading";

    public static final String GET_CONFIG_INFO = "/hardware/getConfigInfo";

    public static final String GET_UPGRADE_INFO  = "/hardware/getUpgradeInfo";

    public static final String SEND_MESSAGE = "/hardware/sendMessage";

    public static final String GET_COST_INFO = "/hardware/getCostInfo";

    public static final String ACCOUNT_LIST = "/account/accountList";

    public static final String GET_USER_INFO = "/hardware/getUserInfo";

    public static final String BALANCE_INQUIRY = "/hardware/balanceInquiry";

    public static final String UPLOAD_GATE_LOG = "/hardware/uploadGateLog";
}
