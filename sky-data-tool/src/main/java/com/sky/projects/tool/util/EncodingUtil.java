package com.sky.projects.tool.util;

import java.io.UnsupportedEncodingException;

public final class EncodingUtil {

	public static String encoding2UTF8(String str) {
		String defaultEncoding = "UTF-8";
		byte[] datas = null;
		String encode = "UTF-8";

		try {
			// UTF-8
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return str;
			}
		} catch (Exception e) {
		}

		try { // is "GB2312" and transfer to "UTF-8"
			encode = "GB2312";
			datas = str.getBytes(encode);
			if (str.equals(new String(datas, encode))) {
				return new String(datas, defaultEncoding);
			}
		} catch (Exception e) {
		}

		try { // is "ISO-8859-1" and transfer to "UTF-8"
			encode = "ISO-8859-1";
			datas = str.getBytes(encode);
			if (str.equals(new String(datas, encode))) {
				return new String(datas);
			}
		} catch (Exception e) {
		}

		try { // is "GBK" and transfer to "UTF-8"
			encode = "GBK";
			datas = str.getBytes(encode);
			if (str.equals(new String(datas, encode))) {
				return new String(datas, defaultEncoding);
			}
		} catch (Exception e) {
		}

		return "";
	}

	public static String getEncoding(String str) {
		String encode = "";

		try {
			encode = "GB2312";
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			encode = "UTF-8";
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 自动转码
	 * 
	 * @param str
	 * @param charEncode
	 *            默认UTF-8
	 * @return
	 */
	public String transcode(String str, String charEncode) {
		if (null == charEncode || "".equals(charEncode)) {
			charEncode = "UTF-8";
		}
		String temp = "";
		try {
			String code = getEncoding(str);
			temp = new String(str.getBytes(code), charEncode);
		} catch (UnsupportedEncodingException e) {
		}
		return temp;
	}

	private EncodingUtil() {
	}
}
