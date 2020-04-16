package com.vunke.electricity.device;

import android.text.TextUtils;
import android.util.Log;

import com.vunke.electricity.util.LogUtil;
import com.vunke.electricity.util.Utils;

import java.util.Arrays;

/**
 * Created by zhuxi on 2019/9/11.
 */

public class ElectrictyMeterUtil {
    private static final String TAG = "ElectrictyMeterUtil";
    public static boolean authCode(byte b1,byte b2,byte b3){
        byte [] bt = new byte[]{(byte)0x81,0x06,0x16};
        return b1 == bt[0] && b2 == bt[1]&& b3 == bt[2];
    }

    /**
     * 根据表地址查询表数据
     * @param JeteId
     * @return
     */
    public static byte[] FrmatQueryCMD(String JeteId){
        if (TextUtils.isEmpty(JeteId)){
            return null;
        }
        byte[] array =  new byte[14];
        array[0] = 0x68;
        array[7] = 0x68;
        array[8] = 0x01;
        array[9] = 0x02;
        array[10] = 0x43;
        array[11] = (byte)0xC3;
        try {
            byte[] bytes = Utils.hexStringToByte(JeteId);
            array[1] = bytes[5];
            array[2] = bytes[4];
            array[3] = bytes[3];
            array[4] = bytes[2];
            array[5] = bytes[1];
            array[6] = bytes [0];
            array[12] = makeChacksum(array)[0];
        }catch (Exception e){
            e.printStackTrace();
        }
        array[13] = 0x16;
        String strArr = Utils.bytesToHex(array).toUpperCase();
        LogUtil.i(TAG, "FrmatQueryCMD: array："+ strArr);
        return array;
    }

    public static byte[] FrmatOpenCMD(String JeteId){
        if (TextUtils.isEmpty(JeteId)){
            return null;
        }
        byte[] array = new byte[20];
        array[0] = 0x68;
        array[7] = 0x68;
//        //04 08 5B F3 34 44 44 44 99 CC 82 16
        array[8] = 0x04;
        array[9] = 0x08;
        array[10] = 0x5B;
        array[11] = (byte)0xF3;
        array[12] = 0x34;
        array[13] = 0x44;
        array[14] = 0x44;
        array[15] = 0x44;
        array[16] = (byte)0x99;
        array[17] = (byte)0xCC;
        try {
            byte[] bytes = Utils.hexStringToByte(JeteId);
            array[1] = bytes[5];
            array[2] = bytes[4];
            array[3] = bytes[3];
            array[4] = bytes[2];
            array[5] = bytes[1];
            array[6] = bytes [0];
            array[18] = makeChacksum(array)[0];
        }catch (Exception e){
            e.printStackTrace();
        }
        array[19] = 0x16;
        String strArr = Utils.bytesToHex(array).toUpperCase();
        LogUtil.i(TAG, "FrmatOpenCMD: array："+ strArr);
        return array;
    }


    public static byte[] FrmatCloseCMD(String JeteId){
        if (TextUtils.isEmpty(JeteId)){
            return null;
        }
        byte[] array = new byte[20];
        array[0] = 0x68;
        array[7] = 0x68;
//        //04 08 5B F3 34 44 44 44 99 CC 82 16
        array[8] = 0x04;
        array[9] = 0x08;
        array[10] = 0x5B;
        array[11] = (byte)0xF3;
        array[12] = 0x34;
        array[13] = 0x44;
        array[14] = 0x44;
        array[15] = 0x44;
        array[16] = (byte)0x88;
        array[17] = 0x66;
        try {
            byte[] bytes = Utils.hexStringToByte(JeteId);
            array[1] = bytes[5];
            array[2] = bytes[4];
            array[3] = bytes[3];
            array[4] = bytes[2];
            array[5] = bytes[1];
            array[6] = bytes [0];
            array[18] = makeChacksum(array)[0];
        }catch (Exception e){
            e.printStackTrace();
        }
        array[19] = 0x16;
        String strArr = Utils.bytesToHex(array).toUpperCase();
        LogUtil.i(TAG, "FrmatCloseCMD: array："+ strArr);
        return array;
    }


    public static byte[] makeChacksum(byte[] array){
//        if (array==null||array.length==0){
//            return new byte[]{};
//        }
        int sum = 0;
        for (int i = 0; i <= array.length-1; i++) {
            sum += array[i];
        }
        String  sumStr = Integer.toHexString(sum);
//        sumStr = sumStr.substring(sumStr.length() - 2, 2);
        sumStr = sumStr.substring(sumStr.length() - 2, sumStr.length());
        return  Utils.HexString2Bytes(sumStr);
    }


    /**
     *
     * @param b
     * @return
     */

    public static double getElectric(byte[] b){
        if (b == null ||b.length==0){
            LogUtil.i(TAG, "onDataReceived: 获取电量失败");
            return -1;
        }
        LogUtil.i(TAG,"getElectric:"+Utils.bytesToHex(b).toUpperCase());
        int j = b.length-6;
        byte[] m = new byte[4];
        m[0] = b[j];
        m[1] = b[j + 1];
        m[2] = b[j + 2];
        m[3] = b[j + 3];
        LogUtil.i(TAG,"getElectric:"+Utils.bytesToHex(m).toUpperCase());
        return FrmatElectric(m);
    }
    public static double getElectric2(byte[] b){
        if (b == null ||b.length==0){
            LogUtil.i(TAG, "onDataReceived: 获取电量失败");
            return -1;
        }
        LogUtil.i(TAG,"getElectric:"+Utils.bytesToHex(b).toUpperCase());
        int j = b.length-6;
        byte[] m = new byte[4];
        m[0] = b[j];
        m[1] = b[j + 1];
        m[2] = b[j + 2];
        m[3] = b[j + 3];
        LogUtil.i(TAG,"getElectric:"+Utils.bytesToHex(m).toUpperCase());
        return FrmatElectric2(m);
    }
    private static double FrmatElectric(byte[] b){
        if (b==null || b.length == 0){
            return -1;
        }
        int i = 0;
        byte[] DataColle = new byte[4];
        for (i = 0; i <= 3; i++){
            DataColle[i] = (byte)(b[i]-0x33);
        }
        LogUtil.i(TAG,Utils.bytesToHex(DataColle).toUpperCase());
        double ElcEnergy = 0;
        int a = 0;
        for (i = 0; i <= 3; i++) {
            int t = DataColle[3 - i] / 16;
            int f = DataColle[3 - i] % 16;
            LogUtil.i(TAG,"get t:"+t);
            LogUtil.i(TAG,"get f:"+f);
//            a = DataColle[3 - i] / 16 * 10 + DataColle[3 - i] % 16; //数制转换
            a = t+f;
            LogUtil.i(TAG,"FrmatElectric get a1:"+a);
            if (a<0){
                a = a*-1;
            }
            LogUtil.i(TAG,"FrmatElectric get a2:"+a);
            switch (i){
                case 0:
                    double newA0 = a *10000;
                    LogUtil.i(TAG,"newA0:"+newA0);
                    ElcEnergy+=a *10000;

                    break;
                case 1:
                    double newA1 = a *100;
                    LogUtil.i(TAG,"newA1:"+newA1);
                    ElcEnergy += a*100;
                    break;
                case 2:
                    double newA2 = a *1;
                    LogUtil.i(TAG,"newA2:"+newA2);
                    ElcEnergy += a;
                    break;
                case 3:
                    double newA3 = a *0.01;
                    LogUtil.i(TAG,"newA3:"+newA3);
                    ElcEnergy +=a*0.01;
                    break;
            }
//            if (i <= 2) {
//                ElcEnergy = ElcEnergy + a;
//            } else {
//                ElcEnergy = ElcEnergy + (double) a * 0.01;
//            }
        }
        LogUtil.i(TAG,"gett ElcEnergy:"+ElcEnergy);
        return ElcEnergy;
    }

    private static double FrmatElectric2(byte[] b){
        if (b==null || b.length == 0){
            return -1;
        }
        double ElcEnergy = 0;
        byte[] DataColle = new byte[4];
        for (int i = 0; i <= 3; i++){
            DataColle[i] = (byte)(b[i]-0x33);
            String hex = Integer.toHexString(DataColle[i] & 0xFF);
            Log.i(TAG, "getElectric2: hex:"+hex);
            int a = Integer.valueOf(hex);
            Log.i(TAG, "getElectric2: a:"+a);
            switch (i){
                case 3:
                    double newA0 = a *10000;
                    LogUtil.i(TAG,"newA1:"+newA0);
                    ElcEnergy+=a *10000;
                    break;
                case 2:
                    double newA1 = a *100;
                    LogUtil.i(TAG,"newA1:"+newA1);
                    ElcEnergy+=a *100;
                    break;
                case 1:
                    double newA2 = a *1;
                    LogUtil.i(TAG,"newA2:"+newA2);
                    ElcEnergy+=a *1;
                    break;
                case 0:
                    double newA3 = a *0.01;
                    LogUtil.i(TAG,"newA3:"+newA3);
                    ElcEnergy+=a *0.01;
                    break;
            }
        }
        LogUtil.i(TAG,"get ElcEnergy:"+ElcEnergy);
        return ElcEnergy;
    }
    public static String getMeterNo(byte[] b) {
        if (b == null || b.length == 0) {
            LogUtil.i(TAG, "getMeterNo:  参数为空，无法获取电表编号");
            return "";
        }
        String meterNo = "";
        byte[] jeteByte = new byte[6];
        byte initByte= 0x68;
        try {
            if (b.length > 18 && b.length == 22) {
                int num = b.length - 18;
                byte auth1 = b[num];
                byte auth2 = b[num + 7];
                if (auth1 == initByte && auth2 == initByte) {
                    LogUtil.i(TAG, "getMeterNo get data success");
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
                    LogUtil.i(TAG, "getMeterNo get jeteByte data:" + Utils.bytesToHex(jeteByte).toUpperCase());
                    meterNo = Utils.bytesToHex(jeteByte).toUpperCase().replace(" ", "");
                    LogUtil.i(TAG,"getMeterNo:"+meterNo);
                    return meterNo;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return meterNo;
    }
    public static byte[] concat2(byte [] a, byte[] b){
        int alen = a.length;
        int blen = b.length;
        if (alen == 0) {
            return b;
        }
        if (blen == 0) {
            return a;
        }
//        val result = java.lang.reflect.Array.newInstance(a.componentType, alen + blen) as ByteArray
        byte[] result = new byte[a.length+b.length];
        System.arraycopy(a, 0, result, 0, alen);
        System.arraycopy(b, 0, result, alen, blen);
        return result;
    }

    public static String getDeviceMeterNo(byte[] b) {
        if (b == null || b.length == 0) {
            LogUtil.i(TAG, "getDeviceMeterNo: 参数为空，无法获取电表编号");
            return "";
        }
        String meterNo = "";
        byte[] jeteByte = new byte[6];
        byte initByte= 0x68;
        byte endByte = 0x16;
        try {
                int num = b.length - (b.length-4);
                byte auth1 = b[num];
                byte auth2 = b[num + 7];
                byte auth3 =b[b.length-1];
                if (auth1 == initByte && auth2 == initByte && auth3== endByte) {
                    LogUtil.i(TAG, "getDeviceMeterNo: get data success");
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
                    LogUtil.i(TAG, "getDeviceMeterNo:get jeteByte data:" + Utils.bytesToHex(jeteByte).toUpperCase());
                    meterNo = Utils.bytesToHex(jeteByte).toUpperCase().replace(" ", "");
                    LogUtil.i(TAG,"getDeviceMeterNo:"+meterNo);
                    return meterNo;
                }
        }catch (Exception e){
            e.printStackTrace();
        }
        return meterNo;
    }

    public static boolean AuthMeter(byte[] b) {
        boolean isAuchSuccess = false;
        if (b == null || b.length == 0) {
            LogUtil.i(TAG, "AuthMeter:  参数为空，无法验证回调是否正常");
            return isAuchSuccess;
        }
        byte initByte= 0x68;
        byte endByte = 0x16;
        byte success = (byte)0x84;
        byte failed = (byte)0xC4;
        try {
            int num = b.length - (b.length-4);
            byte auth1 = b[num];
            byte auth2 = b[num + 7];
            byte auth3 =b[b.length-1];
            if (auth1 == initByte && auth2 == initByte && auth3== endByte) {
                LogUtil.i(TAG, "AuthMeter get data success");
                byte auhtData = b[num+8];
                LogUtil.i(TAG,"AuthMeter data:"+Utils.bytesToHex(new byte[]{auhtData}).toUpperCase());
                if( auhtData == success){
                    LogUtil.i(TAG,"AuthMeter success");
                    isAuchSuccess = true;
                }else if (auhtData == failed){
                    LogUtil.i(TAG,"AuthMeter failed");
                }else {
                    LogUtil.i(TAG,"AuthMeter Error");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isAuchSuccess;
    }

    public static byte[] FrmatStatusCMD(String JeteId){
        if (TextUtils.isEmpty(JeteId)){
            return null;
        }
        byte[] array = new byte[14];
        array[0] = 0x68;
        array[7] = 0x68;
//        //04 08 5B F3 34 44 44 44 99 CC 82 16
        array[8] = 0x01;
        array[9] = 0x02;
        array[10] = 0x53;
        array[11] = (byte)0xF3;
        try {
            byte[] bytes = Utils.hexStringToByte(JeteId);
            array[1] = bytes[5];
            array[2] = bytes[4];
            array[3] = bytes[3];
            array[4] = bytes[2];
            array[5] = bytes[1];
            array[6] = bytes [0];
            array[12] = makeChacksum(array)[0];
        }catch (Exception e){
            e.printStackTrace();
        }
        array[13] = 0x16;
        String strArr = Utils.bytesToHex(array).toUpperCase();
        LogUtil.i(TAG, "FrmatStatusCMD: array："+ strArr);
        return array;
    }


    public static String getMeterStatus(byte[] b) {
        String status = "";
        if (b == null || b.length == 0) {
            LogUtil.i(TAG, "MeterStatus:  参数为空，无法验证回调是否正常");
            return status;
        }
        LogUtil.i(TAG, "MeterStatus data:" + Utils.bytesToHex(b).toUpperCase());
        byte initByte= 0x68;
        byte endByte = 0x16;
        byte[] status1 = {(byte)0x81,0x03,0x53,(byte)0xF3,0x33};
        byte[] status2 = {(byte)0x81,0x03,0x53,(byte)0xF3,0x3B};
        try {
            int num = b.length - (b.length-4);
            byte auth1 = b[num];
            byte auth2 = b[num + 7];
            byte auth3 =b[b.length-1];
            if (auth1 == initByte && auth2 == initByte && auth3== endByte) {
                LogUtil.i(TAG, "MeterStatus get data success");
                byte[] meterStatus = { b[num+8],b[num+9],b[num+10],b[num+11],b[num+12]};
                LogUtil.i(TAG, "MeterStatus data:" + Utils.bytesToHex(meterStatus).toUpperCase());
                if (Arrays.equals(meterStatus,status1)){
                    status = "on";
                }else if (Arrays.equals(meterStatus,status2)){
                    status = "off";
                }else{
                    status = "error";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            status = "error";
        }
        return status;
    }
}
