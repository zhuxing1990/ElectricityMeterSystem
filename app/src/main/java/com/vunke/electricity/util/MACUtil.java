package com.vunke.electricity.util;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MACUtil {
    private static final String TAG = "MACUtil";

    public static String getSERIAL(){
        return Build.SERIAL;
    }
    /**
     * 获取的MAC地址和机顶盒MAC地址不一致，不建议使用 根据文件获取MAC地址
     *
     * @return
     */
//    @Deprecated
    public static String getMac() {
        String macSerial = "00:00:00:00:00:00";
        String str = "";

        try {
//            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/eth1/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial.toUpperCase();
    }

    /**
     * 未开启WIFI 报错 不推荐使用 根据Wifi信息获取本地Mac
     *
     * @param context
     * @return
     */
    @Deprecated
    public static String getLocalMacAddressFromWifiInfo(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     *
     * 推荐使用 获取MAC正确 根据busybox获取本地Mac
     *
     * @return
     */
    public static String getLocalMacAddressFromBusybox() {
        String result = "00:00:00:00:00:00";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");

        // 如果返回的result == null，则说明网络不可取
        if (result == null) {
            return "网络出错，请检查网络";
        }

        // 对该行数据进行解析
        // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr") == true) {
            Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
            LogUtil.i(TAG, "Mac:" + Mac + " Mac.length: " + Mac.length());
//
			/*
             * if(Mac.length()>1){ Mac = Mac.replaceAll(" ", ""); result = "";
			 * String[] tmp = Mac.split(":"); for(int i = 0;i<tmp.length;++i){
			 * result +=tmp[i]; } }
			 */
            result = Mac;
            LogUtil.i(TAG, result + " result.length: " + result.length());
        }
        return result.trim();
    }

    private static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            // 执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null && line.contains(filter) == false) {
                // result += line;
                LogUtil.i(TAG, "line: " + line);
            }

            result = line;
            LogUtil.i(TAG, "result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    @Deprecated
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            LogUtil.i(TAG, "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }

}
