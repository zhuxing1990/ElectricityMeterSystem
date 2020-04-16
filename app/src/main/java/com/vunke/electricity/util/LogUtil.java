package com.vunke.electricity.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志工具类
 * @author zhuxi
 *
 */
public class LogUtil {
	
	private static boolean DEBUG_MODE = true;
	private static String data = "Electricity \t";

	public static void i(String className, String content) {
		Log.i(className, "time:" + getDateTime() + ";" + "[i]" + ";"
				+ data + content);
	}
	public static void a(String content) {
		Log.d("System.out", "time:" + getDateTime() + ";" + "[e]" + ";"
				+ data + content);
	}

	public static void d(String className, String content) {
		Log.d(className, "time:" + getDateTime() + ";" + "[d]" + ";"
				+ data + content);
	}

	public static void e(String className, String content) {
		Log.e(className, "time:" + getDateTime() + ";" + "[e]" + ";"
				+ data + content);
	}

	public static void w(String className, String content) {
		Log.w(className, "time:" + getDateTime() + ";" + "[w]" + ";"
				+ data + content);
	}

	public static void v(String className, String content) {
		Log.v(className, "time:" + getDateTime() + ";" + "[v]" + ";"
				+ data + content);
	}
	
	public static void i(String className, String content, Throwable e) {
		Log.i(className, "time:" + getDateTime() + ";" + "[i]" + ";"
				+ data + content,e);
	}
	public static void a(String content, Throwable e) {
		Log.d("System.out", "time:" + getDateTime() + ";" + "[e]" + ";"
				+ data + content,e);
	}
	public static void d(String className, String content, Throwable e) {
		Log.d(className, "time:" + getDateTime() + ";" + "[d]" + ";"
				+ data + content,e);
	}

	public static void e(String className, String content, Throwable e) {
		Log.e(className, "time:" + getDateTime() + ";" + "[e]" + ";"
				+ data + content,e);
	}

	public static void w(String className, String content, Throwable e) {
		Log.w(className, "time:" + getDateTime() + ";" + "[w]" + ";"
				+ data + content,e);
	}

	public static void v(String className, String content, Throwable e) {
		Log.v(className, "time:" + getDateTime() + ";" + "[v]" + ";"
				+ data + content,e);
	}

	/*
	 * public static void main(String[] args) { WorkLogUtil.a("class:" +
	 * "worleLogUtil"); WorkLogUtil.a("time:" + getDateTime() + ";" + "[d]" +
	 * ";"); WorkLogUtil.a("content:"+"当前内容"); }
	 */

	/**
	 * 获取系统时间
	 * 
	 * @return String 2016-6-12 10:53:05:888
	 */
	public static String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss:SS");
		Date date = new Date(System.currentTimeMillis());
		String time = dateFormat.format(date);
		return time;
	}
	
}