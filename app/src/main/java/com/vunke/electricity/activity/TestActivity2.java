package com.vunke.electricity.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vunke.electricity.device.ElectrictyMeterUtil;
import com.vunke.electricity.device.WeiShenElectricityUtil;
import com.vunke.electricity.util.DateUtil;
import com.vunke.electricity.util.LogUtil;
import com.vunke.electricity.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhuxi on 2019/11/5.
 */

public class TestActivity2 extends AppCompatActivity {

    private static final String TAG = "TestActivity2";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        byte[] bytes = {(byte)0xFE, (byte)0xFE ,(byte)0xFE ,(byte)0xFE ,0x68 ,0x71 ,0x17 ,0x04 ,0x00 ,0x17 ,0x15 ,0x68 ,(byte)0x81 ,0x06 ,0x43 ,(byte)0xC3 ,(byte)0x96 ,0x64 ,(byte)0xB9 ,0x36 ,(byte)0xFE ,0x16};
//        double electric = ElectrictyMeterUtil.getElectric(bytes);
//        double electric2 = ElectrictyMeterUtil.getElectric2(bytes);
//        String meterNo = ElectrictyMeterUtil.getMeterNo(bytes);
//        Log.i(TAG, "onCreate: meterNo:"+meterNo);
//        Log.i(TAG, "onCreate: electric:"+electric);
//        Log.i(TAG, "onCreate: electric2:"+electric2);
// -----------------------------------------------------------------------------
        // 46 00 00 00 32 41
//       String meterNo = "413200000046";
//       String meterNo = "484527000392";
//        byte[] frmatQueryCMD = ElectrictyMeterUtil.FrmatQueryCMD(meterNo);
//        byte[] frmatCloseCMD = ElectrictyMeterUtil.FrmatCloseCMD(meterNo);
//        byte[] frmatOpenCMD = ElectrictyMeterUtil.FrmatOpenCMD(meterNo);
//        Log.i(TAG, "onCreate: frmatQueryCMD:"+ Utils.bytesToHex(frmatQueryCMD).toUpperCase());
//        Log.i(TAG, "onCreate: frmatCloseCMD:"+Utils.bytesToHex(frmatCloseCMD).toUpperCase());
//        Log.i(TAG, "onCreate: frmatOpenCMD:"+Utils.bytesToHex(frmatOpenCMD).toUpperCase());
//        String dataTimeCode = WeiShenElectricityUtil.getDataTimeCode();
//        Log.i(TAG, "onCreate: dataTimeCode:"+dataTimeCode);
//        byte[] datebyte = Utils.hexStringToByte(dataTimeCode);
//        LogUtil.i(TAG,"onCreate datebyte:"+Utils.bytesToHex(datebyte).toUpperCase());
        //默认电表测试
//        DefaultMeterTest();
        //威胜电表测试
//        WeiShenTest();
        Date pastDate = DateUtil.getPastDate(7);
        String toDay = DateUtil.getStringByFormat(pastDate, "yyyyMMdd");
        Log.i(TAG, "onCreate: toDay:"+toDay);
        Date lastMonth = DateUtil.getLastMonth();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String lastTime = simpleDateFormat.format(lastMonth);
        Log.i(TAG, "onCreate: lastTime:"+lastTime);
        Date endDate = new Date();
        String date1 = DateUtil.getStringByFormat(lastMonth, "yyyyMMdd");
        Log.i(TAG, "onCreate: date1:"+date1);
        String date2 = DateUtil.getStringByFormat(endDate, "yyyyMMdd");
        Log.i(TAG, "onCreate: date2:"+date2);
        long betweenDate = (endDate.getTime() - lastMonth.getTime())/(60*60*24*1000);
        Log.i(TAG, "onCreate: betweenDate:"+betweenDate);
//        int compare_date = DateUtil.compare_date(lastMonth, endDate);
//        Log.i(TAG, "onCreate: compare_date:"+compare_date);
//        Date date=curr.getTime();


//        Date timesMonthmorning = DateUtil.getTimesMonthmorning();
//        String times = dateFormat.format(timesMonthmorning);

//        String end = dateFormat.format(new Date());
//        Long timesData = Long.valueOf(times)+4;
//        Long endDate = Long.valueOf(end);
//        endDate = 20200101L;
//        if (endDate > timesData){
//            Long date = endDate - timesData;
//            Log.i(TAG,"date1:"+date);
//        }else{
//            Long  oldTimes =  Long.valueOf(times)+5-30;
//            Long date = endDate = oldTimes;
//            Log.i(TAG,"date2:"+date);
//        }

    }

    private void DefaultMeterTest() {
//        String data = "FE FE FE FE 68 45 23 03 00 18 15 68 81 06 43 C3 33 33 33 33 C1 16";
        String data = "FE FE FE FE 68 85 14 16 00 18 15 68 81 06 43 C3 7B 95 43 33 BF 16 ";
//        String data = "FE FE FE FE 68 31 86 11 00 18 11 68 81 06 43 C3 34 33 33 34 33 33 ";
        String replace_data = data.replace(" ", "");
        Log.i(TAG, "DefaultMeterTest: replace_data:"+replace_data);
        byte[]  databyte= Utils.HexString2Bytes(replace_data);
        Log.i(TAG, "DefaultMeterTest: databyte:"+Utils.bytesToHex(databyte).toUpperCase());
//
        String meterNo1 = ElectrictyMeterUtil.getMeterNo(databyte);
        Log.i(TAG, "DefaultMeterTest: meterNo1:"+meterNo1);
        byte[] frmatQueryCMD = ElectrictyMeterUtil.FrmatQueryCMD(meterNo1);
        LogUtil.i(TAG,"DefaultMeterTest frmatQueryCMD:"+ Utils.bytesToHex(frmatQueryCMD).toUpperCase());
        byte[] frmatOpenCMD = ElectrictyMeterUtil.FrmatOpenCMD(meterNo1);
        LogUtil.i(TAG,"DefaultMeterTest frmatOpenCMD:"+ Utils.bytesToHex(frmatOpenCMD).toUpperCase());
        byte[] frmatCloseCMD = ElectrictyMeterUtil.FrmatCloseCMD(meterNo1);
        LogUtil.i(TAG,"DefaultMeterTest frmatCloseCMD:"+ Utils.bytesToHex(frmatCloseCMD).toUpperCase());
        if(databyte.length > 10 && databyte.length == 22){
            int num = databyte.length - 10;
            byte b1 = databyte[num];
            byte b2 = databyte[num + 1];
            byte b3 = databyte[databyte.length-1];
            if (ElectrictyMeterUtil.authCode(b1, b2, b3)) {
                double hextodl = ElectrictyMeterUtil.getElectric(databyte);
                double hextodl2 = ElectrictyMeterUtil.getElectric2(databyte);
                LogUtil.i(TAG, "getElectric: hextodl：" + hextodl);
                LogUtil.i(TAG, "getElectric: hextodl2：" + hextodl2);
            }else{
                LogUtil.i(TAG, "onDataReceived: 获取正常应答 失败或者 数据域长度 不够");
            }
        }else{
            LogUtil.i(TAG,"onDataReceived  长度不够");

        }
    }

    private void WeiShenTest() {
        String meterNo = "484527000392";
        byte[] frmatCloseCMD2 = WeiShenElectricityUtil.FrmatCloseCMD(meterNo);
        LogUtil.i(TAG,"WeiShenTest frmatCloseCMD2:"+ Utils.bytesToHex(frmatCloseCMD2).toUpperCase());

        byte[] frmatOpenCMD2 = WeiShenElectricityUtil.FrmatOpenCMD(meterNo);
        LogUtil.i(TAG,"WeiShenTest frmatOpenCMD2:"+ Utils.bytesToHex(frmatOpenCMD2).toUpperCase());

        byte[] frmatQueryCMD2 = WeiShenElectricityUtil.FrmatQueryCMD(meterNo);
        LogUtil.i(TAG,"WeiShenTest frmatQueryCMD2:"+ Utils.bytesToHex(frmatQueryCMD2).toUpperCase());

//        String data = "68 04 00 00 36 86 46 68 11 04 34 37 33 37 B7 16";
        String data = "68 92 03 00 27 45 48 68 91 08 33 33 33 33 33 33 33 33 4A 16 ";
        String replace_data = data.replace(" ", "");
        Log.i(TAG, "onCreate: replace_data:"+replace_data);
        byte[]  databyte= Utils.HexString2Bytes(replace_data);
        Log.i(TAG, "onCreate WeiShenTest : databyte:"+Utils.bytesToHex(databyte).toUpperCase());
//
        String meterNo1 = WeiShenElectricityUtil.getMeterNo(databyte);
        Log.i(TAG, "onCreate: meterNo1:"+meterNo1);
//
        double hextodl = ElectrictyMeterUtil.getElectric(databyte);
        double hextodl2 = ElectrictyMeterUtil.getElectric2(databyte);
        LogUtil.i(TAG, "WeiShenTest getElectric: hextodl：" + hextodl);
        LogUtil.i(TAG, "WeiShenTest getElectric: hextodl2：" + hextodl2);
    }

}






