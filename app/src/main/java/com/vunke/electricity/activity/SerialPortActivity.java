package com.vunke.electricity.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.vunke.electricity.util.LogUtil;

import com.example.x6.serialportlib.SerialPort;
import com.vunke.electricity.base.BaseConfig;
import com.vunke.electricity.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhuxi on 2019/9/10.
 */

public abstract class SerialPortActivity extends AppCompatActivity {
    private static final String TAG = "SerialPortActivity";
    protected SerialPortActivity mcontext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcontext = this;
    }

    protected abstract void onDataReceived(final byte[] buffer, final int size);


    private SerialPort serialttyS3;
    private InputStream ttyS3InputStream;
    private OutputStream ttyS3OutputStream;

    protected boolean isDestroy = false;
    //串口接受字节线程
    Thread receiveThread=new Thread(new Runnable() {
        @Override
        public void run() {

            try {
                LogUtil.i(TAG, "run: start read");
                while (!receiveThread.isInterrupted()){
                    if (!isDestroy){
                        try {
                            receiveThread.sleep(1000);
                            ReadSerial();
                            ReadSerial2();
                        }catch (NegativeArraySizeException e){
                            LogUtil.i(TAG, "run: get data is null");
                        }catch (InterruptedException e){

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    });

    /* 串口接受字节 */
    public void ReadSerial() {
        try {
            byte[] buf=new byte[1024];
            if (null==ttyS3InputStream){
                LogUtil.i(TAG, "ReadSerial1: 警告:获取S3输入流失败,请检查串口是否打开。");
                return;
            }
            final int size = ttyS3InputStream.read(buf);
            if (size>0){
                final byte[] bufData = new byte[size];
                System.arraycopy(buf, 0, bufData, 0, size);
                LogUtil.d(TAG,"size: " + size + ",接收1："+ Utils.bytesToHex(bufData).toUpperCase());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onDataReceived(bufData, size);
                    }
                });
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (NegativeArraySizeException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* 串口接受字节 */
    public void ReadSerial2() {
        try {
            byte[] buf=new byte[1024];
            if (null==ttyS4InputStream){
                LogUtil.i(TAG, "ReadSerial2: 警告:获取S4输入流失败,请检查串口是否打开。");
                return;
            }
            final int size = ttyS4InputStream.read(buf);
            if (size>0){
                final byte[] bufData = new byte[size];
                System.arraycopy(buf, 0, bufData, 0, size);
                LogUtil.d(TAG,"size: " + size + ",接收2："+ Utils.bytesToHex(bufData).toUpperCase());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onDataReceived(bufData, size);
                    }
                });
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (NegativeArraySizeException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSerial(){
        try{
            LogUtil.d(TAG,"开始初始化串口1");
            serialttyS3 = new SerialPort(BaseConfig.COM4, BaseConfig.BAUDRATE_1200,8,1,'e',true);
            ttyS3InputStream = serialttyS3.getInputStream();
            ttyS3OutputStream = serialttyS3.getOutputStream();
            if (!receiveThread.isAlive()){
                receiveThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private SerialPort serialttyS4;
    private InputStream ttyS4InputStream;
    private OutputStream ttyS4OutputStream;
    public void initSerial2(){
        try{
            LogUtil.d(TAG,"开始初始化串口2");
            serialttyS4 = new SerialPort(BaseConfig.COM5, BaseConfig.BAUDRATE_1200,8,1,'e',true);
            ttyS4InputStream = serialttyS4.getInputStream();
            ttyS4OutputStream = serialttyS4.getOutputStream();
            if (!receiveThread.isAlive()){
                receiveThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 串口发送字节 */
    public void WriteSerial(byte[] b) {
        try {
            if (null!=serialttyS3){
                if (null!=serialttyS3) {
                    serialttyS3.sendData(b);
                    LogUtil.d(TAG, "发送1：" + Utils.bytesToHex(b).toUpperCase());
                }else {
                    LogUtil.i(TAG, "ReadSerial: 警告:获取输出流失败,请检查串口是否打开。");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* 串口发送字节 */
    public void WriteSerial2(byte[] b) {
        try {
            if (null!=serialttyS4){
                if (null!=serialttyS4) {
                    serialttyS4.sendData(b);
                    LogUtil.d(TAG, "发送2：" + Utils.bytesToHex(b).toUpperCase());
                }else {
                    LogUtil.i(TAG, "ReadSerial: 警告:获取输出流失败,请检查串口是否打开。");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        if (receiveThread != null){
            receiveThread.interrupt();
        }
        closeSerialPort();
        serialttyS3 = null;
        serialttyS4 = null;
        isDestroy = true;
        super.onDestroy();
    }
    public void  closeSerialPort() {
        if (serialttyS3 != null) {
            serialttyS3.closeSerial();
            serialttyS3 = null;
        }
        if (serialttyS4 != null) {
            serialttyS4.closeSerial();
            serialttyS4 = null;
        }
    }

}
