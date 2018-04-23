package com.tdr.yunwei.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class MD5Util {
	public static String MD5encode(String inStr) {
		MessageDigest md5=null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		byte[] byteArray = null;
		try {
			byteArray = inStr.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	public static void main(String[] args) {
		//测试
		String str=MD5encode("");
		System.out.println(str);
		
	}
	

}
