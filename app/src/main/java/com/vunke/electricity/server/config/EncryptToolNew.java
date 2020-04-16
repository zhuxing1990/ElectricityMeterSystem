package com.vunke.electricity.server.config;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author
 * @date Sep 17, 2010
 */
public class EncryptToolNew {

	private static byte[] myIV = { 50, 51, 52, 53, 54, 55, 56, 57 };

	public static String b2s(byte[]bytes) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			buffer.append(Integer.toString(bytes[i], 16));
		}
		return buffer.toString();
	}

	public static byte[]s2b(String str) {
		str = str.toUpperCase();
		byte[]result = new byte[str.length() / 2];
		for (int pos = 0, i = 0; i < str.length(); ) {
			String s1 = String.valueOf(str.charAt(i));
			String s2 = String.valueOf(str.charAt(i + 1));
			int b1 = Byte.parseByte(s1, 16) << 4;
			int b2 = Byte.parseByte(s2, 16);
			result[pos] = (byte) (b1 | b2);
			pos++;
			i += 2;
		}
		return result;
	}

	//private static String strkey = "W9qPIzjaVGKUp7CKRk/qpCkg/SCMkQRu"; //
	//private static String strkey = "12345678123456781234567812345678";
	public static String DESEncrypt(String input, String strkey)
			throws Exception {

		DESedeKeySpec p8ksp = null;
		p8ksp = new DESedeKeySpec(Base64.decode(strkey, Base64.DEFAULT));
		Key key = null;
		key = SecretKeyFactory.getInstance("DESede").generateSecret(p8ksp);

		input = padding(input);
		byte[] plainBytes = (byte[]) null;
		Cipher cipher = null;
		byte[] cipherText = (byte[]) null;

		plainBytes = input.getBytes("UTF8");
		cipher = Cipher.getInstance("DESede/ECB/NoPadding");
		SecretKeySpec myKey = new SecretKeySpec(key.getEncoded(), "DESede");
		IvParameterSpec ivspec = new IvParameterSpec(myIV);
		cipher.init(1, myKey);
		cipherText = cipher.doFinal(plainBytes);
		return b2s(removeBR(Base64.encodeToString(cipherText, Base64.DEFAULT)).getBytes("UTF-8"));

	}

	public static String DESDecrypt(String cipherText, String strkey)
			throws Exception {
		cipherText = new String(s2b(cipherText), "UTF-8");

		DESedeKeySpec p8ksp = null;
		p8ksp = new DESedeKeySpec(Base64.decode(strkey, Base64.DEFAULT));
		Key key = null;
		key = SecretKeyFactory.getInstance("DESede").generateSecret(p8ksp);

		Cipher cipher = null;
		byte[] inPut = Base64.decode(cipherText, Base64.DEFAULT);
		cipher = Cipher.getInstance("DESede/ECB/NoPadding");
		SecretKeySpec myKey = new SecretKeySpec(key.getEncoded(), "DESede");
		// IvParameterSpec ivspec = new IvParameterSpec(myIV);
		cipher.init(2, myKey);
		byte[] output = removePadding(cipher.doFinal(inPut));

		return new String(output, "UTF8");

	}

	private static String removeBR(String str) {
		StringBuffer sf = new StringBuffer(str);

		for (int i = 0; i < sf.length(); ++i) {
			if (sf.charAt(i) == '\n') {
				sf = sf.deleteCharAt(i);
			}
		}
		for (int i = 0; i < sf.length(); ++i)
			if (sf.charAt(i) == '\r') {
				sf = sf.deleteCharAt(i);
			}

		return sf.toString();
	}

	public static String padding(String str) {
		byte[] oldByteArray;
		try {
			oldByteArray = str.getBytes("UTF8");
			int numberToPad = 8 - oldByteArray.length % 8;
			byte[] newByteArray = new byte[oldByteArray.length + numberToPad];
			System.arraycopy(oldByteArray, 0, newByteArray, 0,
					oldByteArray.length);
			for (int i = oldByteArray.length; i < newByteArray.length; ++i) {
				newByteArray[i] = 0;
			}
			return new String(newByteArray, "UTF8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Crypter.padding UnsupportedEncodingException");
		}
		return null;
	}

	public static byte[] removePadding(byte[] oldByteArray) {
		int numberPaded = 0;
		for (int i = oldByteArray.length; i >= 0; --i) {
			if (oldByteArray[(i - 1)] != 0) {
				numberPaded = oldByteArray.length - i;
				break;
			}
		}

		byte[] newByteArray = new byte[oldByteArray.length - numberPaded];
		System.arraycopy(oldByteArray, 0, newByteArray, 0, newByteArray.length);

		return newByteArray;
	}

	public static String getMD5Encrypt(String[] strList, String key,
                                       String seperate) throws Exception {

		if ((strList == null) || (strList.length == 0)) {
			return "";
		}
		if ((seperate == null) || (seperate == "")) {
			seperate = ",";
		}
		StringBuilder builder = new StringBuilder(0x400);
		for (int i = 0; i < strList.length; i++) {
			builder.append(strList[i].toString());
			if (i < (strList.length - 1)) {
				builder.append(seperate);
			}
		}
		return getMD5Encrypt(builder.toString(), key, seperate);
	}

	public static String getMD5Encrypt(String inMsg, String key, String seperate)
			throws Exception {
		String baseKey = "";
		byte[] digesta = null;
		long t1, t2;
		try {
			inMsg = inMsg + seperate + key;

			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(inMsg.getBytes("utf-8"));
			digesta = md.digest();
			//t2 = Calendar.getInstance().getTimeInMillis();
		} catch (Exception e) {
			throw new Exception("ENCRYPT_ERROR");
		}
		return byte2hex(digesta);
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs + "";
		}
		return hs;
	}

	public static void main(String args[]) {
		String ENCRYPT_KEY = "FLIKPQ0K7TLR05IONAUNM60KGXUHTPUH";
		String userName = "073188865648@VOD";
        try {
			String ESE_DATA=EncryptToolNew.DESEncrypt(userName,ENCRYPT_KEY);
			System.out.println("ESE_DATA:"+ESE_DATA);
            String DES_DATA = EncryptToolNew.DESDecrypt(ESE_DATA, ENCRYPT_KEY);
			System.out.println( "DES_DATA:"+DES_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
