package com.vunke.electricity.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.x6.serialportlib.SerialPort;
import com.vunke.electricity.R;
import com.vunke.electricity.device.DeviceUtil;
import com.vunke.electricity.device.ElectrictyMeterUtil;
import com.vunke.electricity.device.WeiShenElectricityUtil;
import com.vunke.electricity.util.LogUtil;
import com.vunke.electricity.util.Utils;

import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.Thread.sleep;

public class MainActivity extends SerialPortActivity {

    private static String TAG = "MainActivity";
    private Button bt_Send, bt_Receive, bt_Send2, bt_Send3;

    private SerialPort serialttyS3;
    private InputStream ttyS3InputStream;
    private OutputStream ttyS3OutputStream;
    private EditText main_edit1;

    @Override
    protected void onDataReceived(byte[] buffer, int size) {

        if (buffer == null || buffer.length == 0) {
            LogUtil.i(TAG, "onDataReceived:  参数为空，无法解析数据");
            return;
        }
        Toast.makeText(this, Utils.bytesToHex(buffer).toUpperCase(), Toast.LENGTH_SHORT).show();
        LogUtil.i(TAG, "onDataReceived: 获取数据:" +Utils.bytesToHex(buffer).toUpperCase());
        LogUtil.i(TAG, "onDataReceived: 获取参数长度:" + buffer.length);
//        String buff  = Utils.byteTo16String(buffer);
//        LogUtil.i(TAG, "onDataReceived: buff:"+buff);
        if (buffer.length > 10 && buffer.length == 22) {
            LogUtil.i(TAG, "onDataReceived: 开始解析电量");
            int num = buffer.length - 10;
            byte b1 = buffer[num];
            byte b2 = buffer[num + 1];
            byte b3 = buffer[buffer.length-1];
            if (ElectrictyMeterUtil.authCode(b1, b2,b3)) {
                LogUtil.i(TAG, "onDataReceived: 电量应答成功，数据长度正常");
                double hextodl = ElectrictyMeterUtil.getElectric(buffer);
                double hextodl2 = ElectrictyMeterUtil.getElectric2(buffer);
                LogUtil.i(TAG, "onDataReceived: hextodl：" + hextodl);
                LogUtil.i(TAG, "onDataReceived: hextodl2：" + hextodl2);
                String getmeterNo = ElectrictyMeterUtil.getMeterNo(buffer);
                LogUtil.i(TAG, "onDataReceived: getmeterNo：" + getmeterNo);
                DeviceUtil.INSTANCE.uploadMeterReading(mcontext,getmeterNo,hextodl,hextodl2,Utils.bytesToHex(buffer).toUpperCase());
//                Toast.makeText(this, "当前已用电量"+hextodl,Toast.LENGTH_SHORT).show();
            } else {
                LogUtil.i(TAG, "onDataReceived: b1:" + Integer.toHexString(b1) + "\t  b2:" + Integer.toHexString(b2));
                LogUtil.i(TAG, "onDataReceived: 获取正常应答 失败或者 数据域长度 不够");
            }
        } else if(buffer.length == 20){
            String meterNo = WeiShenElectricityUtil.getMeterNo(buffer);
            LogUtil.i(TAG, "onDataReceived: getmeterNo：" + meterNo);
            double hextodl = ElectrictyMeterUtil.getElectric(buffer);
            double hextodl2 = ElectrictyMeterUtil.getElectric2(buffer);
            LogUtil.i(TAG, "onDataReceived: hextodl：" + hextodl);
            LogUtil.i(TAG, "onDataReceived: hextodl2：" + hextodl2);
//            try {
////                byte[] frmatCloseCMD = WeiShenElectricityUtil.FrmatCloseCMD(meterNo);
////                WriteSerial2(frmatCloseCMD);
////                sleep(2000);
////                byte[] frmatOpenCMD = WeiShenElectricityUtil.FrmatOpenCMD(meterNo);
////                WriteSerial2(frmatOpenCMD);
//
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }

        }else if(buffer.length == 16){
            String meterNo = ElectrictyMeterUtil.getDeviceMeterNo(buffer);
            LogUtil.i(TAG, "onDataReceived: getDeviceMeterNo1：" + meterNo);
            if (!TextUtils.isEmpty(meterNo)){
                boolean authMeter = ElectrictyMeterUtil.AuthMeter(buffer);
                if (authMeter){
                    LogUtil.i(TAG,"执行命令成功");
                }else {
                    LogUtil.i(TAG,"执行命令失败");
                }
            }
        }else if (buffer.length == 19){
            String meterNo = ElectrictyMeterUtil.getDeviceMeterNo(buffer);
            LogUtil.i(TAG, "onDataReceived: getDeviceMeterNo2：" + meterNo);
            if (!TextUtils.isEmpty(meterNo)){
                String meterStatus = ElectrictyMeterUtil.getMeterStatus(buffer);
                LogUtil.i(TAG,"onDataReceived getMeterStatus:"+meterStatus);
            }
        }
    }
    private SerialPort serialPortS4;
    private SerialPort serialPortS3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //打开串口
//        serialPortS4 = ComPort.Companion.getInstance(MainActivity.this).initComPort2400(BaseConfig.COM5);
//        serialPortS3 = ComPort.Companion.getInstance(MainActivity.this).initComPort2400(BaseConfig.COM4);
        initSerial();
        initSerial2();

        initView();
//
//        String [] StrArr_t1 = {"151700021229","151800032250","151800032246","151800032357","151700041775","151700041773"};
//        String [] StrArr_t1 = {"111800117816","111800117802","151700079055","151700079059","151700079053","151700041766","151700041733","151700041732","151700041736","151700041735","151700041728",
//        "151700041662","151700041660","151700041659","151700041657","151700041663","111800117809"
//        };
//-------------------------------------------查询表编号--------------------------------------------
//        String getMeterCode = "68AAAAAAAAAAAA681300DF16";
//        byte[] getCode = Utils.hexStringToByte(getMeterCode);
//        WriteSerial(getCode);
//        WriteSerial2(getCode);
//-------------------------------------------查询表编号--------------------------------------


//        String [] StrArr_t1 = {"001600054791"};
//        String [] StrArr_t1 = {"111800118189"};
        String [] StrArr_t1 = {"111800118206"};
//        String [] StrArr_t1 = {"477618000006"};
//        String meterNo = "502203000642";
//        String meterNo = "502203000174";
//        String [] StrArr_t1 = {
//                                 "502203000640"
//                                ,"502203000639"
//                                ,"502203000641"
//        };
        ;
//        String [] StrArr_t1 = {"484527000392"};
//        String [] StrArr_t1 = {"151700079329"};


//--------------------------------跳闸命令----------------------------------------------------
//        byte[] frmatCloseCMD = ElectrictyMeterUtil.FrmatCloseCMD(StrArr_t1[0]);
//        WriteSerial(frmatCloseCMD);
//        WriteSerial2(frmatCloseCMD);
//---------------------------------跳闸命令-----------------------------------------------------


//--------------------------------合闸命令----------------------------------------------------
//        byte[] frmatOpenCMD = ElectrictyMeterUtil.FrmatOpenCMD(StrArr_t1[0]);
//        WriteSerial(frmatOpenCMD);
//        WriteSerial2(frmatOpenCMD);
//---------------------------------合闸命令------------------------------------------------


//---------------------------------查询闸状态------------------------------------------------
//        byte[] frmatStatusCMD = ElectrictyMeterUtil.FrmatStatusCMD(StrArr_t1[0]);
//        WriteSerial(frmatStatusCMD);
//        WriteSerial2(frmatStatusCMD);
//----------------------------------查询闸状态---------------------------------------------

//---------------------------------------------------------------------------------------
//        query(StrArr_t1,StrArr_t1);
//---------------------------------------------------------------------------------------
//        String [] StrArr_t1 = {"151800032281"};
//        String [] StrArr_t1 = {"111800041314","151800032481"};
//        String [] StrArr_t1 = {"151700079687","111800117816"};
//        String [] StrArr_t1 = {"151800032408","151700041726"};//屈臣氏
//        String [] StrArr_t1 = {"151800032485","151800032482"};
//        String [] StrArr_t1 = {"151800032344"};
//        String [] StrArr_t1 = {"151700041771"};
//        String [] StrArr_t1 = {"111800041315"};
//        String [] StrArr_t1 = {"151800032289"};
//        String [] StrArr_t1 = {"151800032358","151800032282","151800032260"};
//        String [] StrArr_t1 = {"111700027871","111700027869","111700027883"};
//        String [] StrArr_t1 = {
//                "151700079682",
//                "151700041760",
//                "151700041843",
//                "151700021564",
//                "151700041844",
//                "151700041724",
//                "151700041744",
//                "151700041746",
//                "151700041883",
//                "151700041739",
//
//        };
//        String [] StrArr_t1 = {
//                                "001600054791",
//                               "484527000392",
////                               "77618000006"
//        };


        query(StrArr_t1,StrArr_t1);
//        query(TestArr.strArr_ERROE,TestArr.strArr_ERROE);
//        query(TestArr.strArrF1,TestArr.strArrF1);
//        query(TestArr.strArr_F1,TestArr.strArr_F1);
//        query(TestArr.strArr01,TestArr.strArr01);
//        query(TestArr.strArr02, TestArr.strArr02);
//        query(TestArr.strArr03,TestArr.strArr03);
//        query(TestArr.strArr04,TestArr.strArr04);
//        query(TestArr.strArr05,TestArr.strArr05);
//        query(TestArr.strArr06,TestArr.strArr06);
//        query(TestArr.strArr07_08,TestArr.strArr0n7_08);
//        query(TestArr.strArr07,TestArr.strArr07);
//        query(TestArr.strArr08,TestArr.strArr08);
//        query(TestArr.strArr09,TestArr.strArr09);
//        query(TestArr.strArr10,TestArr.strArr10);
//        query(TestArr.strArr12,TestArr.strArr12);
//        query(TestArr.strArr13,TestArr.strArr13);
//        query(TestArr.strArr14,TestArr.strArr14);
//        query(TestArr.strArr14,TestArr.strArr15);
//        query(TestArr.strArr16,TestArr.strArr16);
//        query(TestArr.strArr21_24,TestArr.strArr21_24);
//        query(TestArr.strArr22_23,TestArr.strArr22_23);
//        TestQuery();
//        TestReboot();

    }



    public void query(final String[] str1, final String[] str2) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isDestroy) {
//                        String data = "68 AA AA AA AA AA AA 68 13 00 DF 16";
//                        String replace_data = data.replace(" ", "");
//                        Log.i(TAG, "onCreate: replace_data:"+replace_data);
//                        byte[]  databyte= Utils.HexString2Bytes(replace_data);
//                        Log.i(TAG, "onCreate WeiShenTest : databyte:"+Utils.bytesToHex(databyte).toUpperCase());
//                        WriteSerial(databyte);
//                        sleep(1000);
//                        WriteSerial2(databyte);

                        for (int i = 0; i < str1.length; i++) {
// //                           sleep(4000);
//                            sleep(1000);
//                            LogUtil.i(TAG,"发送str1:"+str1[i]);
                            byte[] frmatCMD = ElectrictyMeterUtil.FrmatQueryCMD(str1[i]);
                            WriteSerial(frmatCMD);
                            LogUtil.i(TAG," frmatCMD:"+ Utils.bytesToHex(frmatCMD).toUpperCase());
                            sleep(2000);
                            byte[] frmatOpenCMD = ElectrictyMeterUtil.FrmatOpenCMD(str1[i]);
                            WriteSerial(frmatOpenCMD);
                            LogUtil.i(TAG," frmatOpenCMD:"+ Utils.bytesToHex(frmatOpenCMD).toUpperCase());
                        }

                        for (int i = 0; i < str1.length; i++) {
//                            sleep(2000);
//                            byte[] changetBaudRate = WeiShenElectricityUtil.changetBaudRate(str1[i]);
//                            LogUtil.i(TAG,"WeiShenTest changetBaudRate:"+ Utils.bytesToHex(changetBaudRate).toUpperCase());
//                            serialPortS4.sendData(changetBaudRate);
//                            serialPortS3.sendData(changetBaudRate);

                        }
//                        sleep(2000);
//                        initSerial2();
//                        initSerial();
                        sleep(2000);
                        for (int i = 0; i < str2.length; i++) {
//                            sleep(4000);
//                            sleep(2000);
                            LogUtil.i(TAG, "发送str2:" + str2[i]);
                            byte[] frmatCMD = ElectrictyMeterUtil.FrmatQueryCMD(str2[i]);
                            WriteSerial2(frmatCMD);
                            LogUtil.i(TAG," frmatCMD2:"+ Utils.bytesToHex(frmatCMD).toUpperCase());
                            sleep(2000);
                            byte[] frmatOpenCMD = ElectrictyMeterUtil.FrmatOpenCMD(str1[i]);
                            WriteSerial2(frmatOpenCMD);
                            LogUtil.i(TAG," frmatOpenCMD2:"+ Utils.bytesToHex(frmatOpenCMD).toUpperCase());
                        }
                        for (int i = 0; i < str1.length; i++) {
// //                           sleep(4000);
//                            LogUtil.i(TAG,"发送str1:"+str1[i]);
//                            byte[] frmatCMD = WeiShenElectricityUtil.FrmatQueryCMD(str1[i]);
//                            WriteSerial(frmatCMD);

//                            byte[] frmatCloseCMD2 = WeiShenElectricityUtil.FrmatCloseCMD(str1[i]);
//                            LogUtil.i(TAG,"WeiShenTest frmatCloseCMD2:"+ Utils.bytesToHex(frmatCloseCMD2).toUpperCase());
//                            WriteSerial(frmatCloseCMD2);
//                            sleep(2000);
//                            byte[] frmatOpenCMD2 = WeiShenElectricityUtil.FrmatOpenCMD(str1[i]);
//                            LogUtil.i(TAG,"WeiShenTest frmatOpenCMD2:"+ Utils.bytesToHex(frmatOpenCMD2).toUpperCase());
//                            WriteSerial(frmatOpenCMD2);
//                            sleep(2000);
//                            byte[] frmatQueryCMD2 = WeiShenElectricityUtil.FrmatQueryCMD(str1[i]);
//                            LogUtil.i(TAG,"WeiShenTest frmatQueryCMD2:"+ Utils.bytesToHex(frmatQueryCMD2).toUpperCase());
//                            WriteSerial(frmatQueryCMD2);
                        }
//                        for (int i = 0; i < str2.length; i++) {
////                            sleep(4000);
////                            sleep(1000);
////                            LogUtil.i(TAG, "发送str2:" + str2[i]);
////                            byte[] frmatCMD = WeiShenElectricityUtil.FrmatQueryCMD(str2[i]);
////                            WriteSerial2(frmatCMD);
//
////                            byte[] frmatCloseCMD2 = WeiShenElectricityUtil.FrmatCloseCMD(str1[i]);
////                            LogUtil.i(TAG,"WeiShenTest frmatCloseCMD2:"+ Utils.bytesToHex(frmatCloseCMD2).toUpperCase());
////                            WriteSerial2(frmatCloseCMD2);
////                            byte[] frmatOpenCMD2 = WeiShenElectricityUtil.FrmatOpenCMD(str1[i]);
////                            LogUtil.i(TAG,"WeiShenTest frmatOpenCMD2:"+ Utils.bytesToHex(frmatOpenCMD2).toUpperCase());
////                            WriteSerial2(frmatCloseCMD2);
//                            sleep(2000);
//                            byte[] frmatQueryCMD2 = WeiShenElectricityUtil.FrmatQueryCMD(str2[i]);
//                            LogUtil.i(TAG,"WeiShenTest frmatQueryCMD2:"+ Utils.bytesToHex(frmatQueryCMD2).toUpperCase());
//                            WriteSerial2(frmatQueryCMD2);
//                        }
                    }
                    LogUtil.i(TAG,"query over");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    private void initView() {
        bt_Send = this.findViewById(R.id.bt_Send);
        bt_Receive = this.findViewById(R.id.bt_Receive);
        bt_Send2 = findViewById(R.id.bt_Send2);
        bt_Send3 = findViewById(R.id.bt_Send3);
        main_edit1 = findViewById(R.id.main_edit1);
        bt_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //68 91 47 05 00 16 00 68 04 08 5B F3 34 44 44 44 99 CC 82 16
                String jeteId = main_edit1.getText().toString();
                byte[] frmatOpenCMD = ElectrictyMeterUtil.FrmatOpenCMD(jeteId);
//                WriteSerial(bytes);
                WriteSerial(frmatOpenCMD);
            }
        });
        bt_Send2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jeteId = main_edit1.getText().toString();
                byte[] frmatCloseCMD = ElectrictyMeterUtil.FrmatCloseCMD(jeteId);
                WriteSerial(frmatCloseCMD);
            }
        });
        bt_Send3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jeteId = main_edit1.getText().toString();
                if (!TextUtils.isEmpty(jeteId)) {
                    byte[] frmatCMD = ElectrictyMeterUtil.FrmatQueryCMD(jeteId);
                    if (frmatCMD != null) {
                        WriteSerial(frmatCMD);
                    }
                }
//                WriteSerial(bytes4);
            }
        });
        bt_Receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.i(TAG, "onClick: get Serial Port message");
                ReadSerial();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy: ");
        closeSerialPort();
        isDestroy = true;
    }
}
