package com.vunke.electricity.device;

import android.text.TextUtils;
import android.util.Log;

import com.vunke.electricity.util.LogUtil;
import com.vunke.electricity.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhuxi on 2019/12/12.
 */

public class WeiShenElectricityUtil {
    private static final String TAG = "WeiShenElectricityUtil";
    public static byte[] FrmatOpenCMD(String JeteId){
        if (TextUtils.isEmpty(JeteId)){
            return null;
        }
        byte[] array = new byte[28];
        array[0] = 0x68;
        array[7] = 0x68;
//        //04 08 5B F3 34 44 44 44 99 CC 82 16
        array[8] = 0x1C;
        array[9] = 0x10;
        array[10] = 0x35;
        array[11] = 0x33;
        array[12] = 0x33;
        array[13] = 0x33;
        array[14] = 0x34;
        array[15] = 0x33;
        array[16] = 0x33;
        array[17] = 0x33;
        try {
            byte[] bytes = Utils.hexStringToByte(JeteId);
            array[1] = bytes[5];
            array[2] = bytes[4];
            array[3] = bytes[3];
            array[4] = bytes[2];
            array[5] = bytes[1];
            array[6] = bytes [0];
        }catch (Exception e){
            e.printStackTrace();
        }
        array[18] = 0x4F;
        array[19] = 0x33;
        String dateTime = getDataTimeCode();
        Log.i(TAG, "FrmatCloseCMD: get dateTime:"+dateTime);
        byte[] datebyte = Utils.hexStringToByte(dateTime);
        LogUtil.i(TAG,"datebyte:"+Utils.bytesToHex(datebyte).toUpperCase());
        // (byte)(b[i]-0x33)
        array[20] =(byte)(datebyte[0]+0x33);
        array[21] =(byte)(datebyte[1]+0x33);
        array[22] =(byte)(datebyte[2]+0x33);
        array[23] =(byte)(datebyte[3]+0x33);
        array[24] =(byte)(datebyte[4]+0x33);
        array[25] =(byte)(datebyte[5]+0x33);
        array[26] = ElectrictyMeterUtil.makeChacksum(array)[0];
        array[27] =0x16;
        String strArr = Utils.bytesToHex(array).toUpperCase();
        LogUtil.i(TAG, "FrmatCloseCMD: array："+ strArr);
        return array;
    }

    public static byte[] FrmatCloseCMD(String JeteId){
        if (TextUtils.isEmpty(JeteId)){
            return null;
        }
        byte[] array = new byte[28];
        array[0] = 0x68;
        array[7] = 0x68;
//        //04 08 5B F3 34 44 44 44 99 CC 82 16
        array[8] = 0x1C;
        array[9] = 0x10;
        array[10] = 0x35;
        array[11] = 0x33;
        array[12] = 0x33;
        array[13] = 0x33;
        array[14] = 0x34;
        array[15] = 0x33;
        array[16] = 0x33;
        array[17] = 0x33;
        try {
            byte[] bytes = Utils.hexStringToByte(JeteId);
            array[1] = bytes[5];
            array[2] = bytes[4];
            array[3] = bytes[3];
            array[4] = bytes[2];
            array[5] = bytes[1];
            array[6] = bytes [0];
        }catch (Exception e){
            e.printStackTrace();
        }
        array[18] = 0x4D;
        array[19] = 0x33;
        String dateTime = getDataTimeCode();
        Log.i(TAG, "FrmatOpenCMD: get dateTime:"+dateTime);
        byte[] datebyte = Utils.hexStringToByte(dateTime);
        LogUtil.i(TAG,"datebyte:"+Utils.bytesToHex(datebyte).toUpperCase());
        // (byte)(b[i]-0x33)
        array[20] =(byte)(datebyte[0]+0x33);
        array[21] =(byte)(datebyte[1]+0x33);
        array[22] =(byte)(datebyte[2]+0x33);
        array[23] =(byte)(datebyte[3]+0x33);
        array[24] =(byte)(datebyte[4]+0x33);
        array[25] =(byte)(datebyte[5]+0x33);
        array[26] = ElectrictyMeterUtil.makeChacksum(array)[0];
        array[27] =0x16;
        String strArr = Utils.bytesToHex(array).toUpperCase();
        LogUtil.i(TAG, "FrmatOpenCMD: array："+ strArr);
        return array;
    }

    public static byte[] FrmatQueryCMD(String JeteId){
        if (TextUtils.isEmpty(JeteId)){
            return null;
        }
        byte[] array = new byte[16];
        array[0] = 0x68;
        array[7] = 0x68;
//        //04 08 5B F3 34 44 44 44 99 CC 82 16
        array[8] = 0x11;
        array[9] = 0x04;
        array[10] = 0x33;
        array[11] = 0x33;
        array[12] = 0x33;
        array[13] = 0x33;
        try {
            byte[] bytes = Utils.hexStringToByte(JeteId);
            array[1] = bytes[5];
            array[2] = bytes[4];
            array[3] = bytes[3];
            array[4] = bytes[2];
            array[5] = bytes[1];
            array[6] = bytes [0];
        }catch (Exception e){
            e.printStackTrace();
        }
        array[14] = ElectrictyMeterUtil.makeChacksum(array)[0];
        array[15] = 0x16;
        String strArr = Utils.bytesToHex(array).toUpperCase();
        LogUtil.i(TAG, "FrmatQueryCMD: array："+ strArr);
        return array;
    }
    public static String getDataTimeCode(){
        long timeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new  SimpleDateFormat("ssmmHHddMMyy");   //yyyy-MM-dd HH:mm:ss.SS
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
        String dataTime = sdf.format(new Date(timeMillis));
        return dataTime;
    }

    public static String getMeterNo(byte[] b) {
        if (b == null || b.length == 0) {
            LogUtil.i(TAG, "onDataReceived:  参数为空，无法获取电表编号");
            return "";
        }
        String meterNo = "";
        byte[] jeteByte = new byte[6];
        byte initByte= 0x68;
        try {
            int byteLength = b.length;
            if (byteLength>16&& byteLength==20) {
                int num = b.length-20 ;
                byte auth1 = b[num];
                byte auth2 = b[num + 7];
                if (auth1 == initByte && auth2 == initByte) {
                    LogUtil.i(TAG, " get data success");
                    byte t0 = b[num + 1];
                    byte t1 = b[num + 2];
                    byte t2 = b[num + 3];
                    byte t3 = b[num + 4];
                    byte t4 = b[num + 5];
                    byte t5 = b[num + 6];
                    jeteByte[0] = t5;
                    jeteByte[1] = t4;
                    jeteByte[2] = t3;
                    jeteByte[3] = t2;
                    jeteByte[4] = t1;
                    jeteByte[5] = t0;
                    LogUtil.i(TAG, "get jeteByte data:" + Utils.bytesToHex(jeteByte).toUpperCase());
                    meterNo = Utils.bytesToHex(jeteByte).toUpperCase().replace(" ", "");
                    LogUtil.i(TAG,"get meterNo:"+meterNo);
                    return meterNo;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return meterNo;
    }

}
