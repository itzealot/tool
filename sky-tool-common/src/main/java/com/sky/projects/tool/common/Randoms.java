package com.sky.projects.tool.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;

public final class Randoms {

	public static final char[] BASE_16 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	// 移动手机号码前缀
	public static final String[] YD_MOBILE_PREFIX = { "1340", "1341", "1342", "1343", "1344", "1345", "1346", "1347",
			"1348", "135", "136", "137", "138", "139", "150", "151", "157", "158", "159", "182", "187", "188", "1705" };

	// 联通手机号码前缀
	public static final String[] LT_MOBILE_PREFIX = { "130", "131", "132", "152", "155", "156", "185", "186", "1709" };

	// 电信手机号码前缀
	public static final String[] DX_MOBILE_PREDIX = { "133", "1349", "153", "180", "189", "1700" };

	static enum MobilePrefix {
		YD(YD_MOBILE_PREFIX), LT(LT_MOBILE_PREFIX), DX(DX_MOBILE_PREDIX);
		String[] prifixs;

		private MobilePrefix(String[] prifixs) {
			this.prifixs = prifixs;
		}
	}

	/**
	 * 字符串包含 alpha-numeric(字母与数字)
	 * 
	 * @param length
	 * @return
	 */
	public static String randomAlphanumeric(int length) {
		return RandomStringUtils.randomAlphanumeric(length);
	}

	/**
	 * 字符串包含 Alphabetic(字母)
	 * 
	 * @param length
	 * @return
	 */
	public static String randomAlphabetic(int length) {
		return RandomStringUtils.randomAlphabetic(length);
	}

	public static String randomMacNoSpliter() {
		return RandomStringUtils.random(12, BASE_16);
	}

	public static String randomMacWithSpliter() {
		return macWithSpliter(randomMacNoSpliter());
	}

	/**
	 * 手机号码
	 * 
	 * 移动：134[0-8], 135, 136, 137, 138, 139, 150, 151, 157, 158, 159, 182, 187,
	 * 188, 1705
	 * 
	 * 联通：130, 131, 132, 152, 155, 156, 185, 186, 1709
	 * 
	 * 电信：133, 1349, 153, 180, 189, 1700
	 * 
	 * @param prefix
	 * @return
	 */
	public static String randomMobilePhone(String prefix) {
		String value = check(prefix);

		return value + randomNumeric(11 - value.length());
	}

	public static String randomMobilePhoneWithSpliter(String prefix) {
		String value = check(prefix);

		return phoneWithSpliter(value + randomNumeric(11 - value.length()), '-');
	}

	public static String randomMobilePhoneWithSpliter(String prefix, char ch) {
		String value = check(prefix);

		return phoneWithSpliter(value + randomNumeric(11 - value.length()), ch);
	}

	private static String phoneWithSpliter(String phone, char ch) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(phone.substring(0, 3));
		buffer.append(ch);
		buffer.append(phone.substring(3, 7));
		buffer.append(ch);
		buffer.append(phone.substring(7, 11));

		return buffer.toString();
	}

	public static String randomMobilePhone(MobilePrefix predix) {
		return randomMobilePhone(predix.prifixs[new Random().nextInt(predix.prifixs.length)]);
	}

	public static String randomMobilePhoneWithSpliter(MobilePrefix predix) {
		return randomMobilePhoneWithSpliter(predix.prifixs[new Random().nextInt(predix.prifixs.length)]);
	}

	public static String randomMobilePhoneWithSpliter(MobilePrefix predix, char ch) {
		return randomMobilePhoneWithSpliter(predix.prifixs[new Random().nextInt(predix.prifixs.length)], ch);
	}

	private static String check(String prefix) {
		checkNotNull(prefix, "prefix can't be null");

		String value = prefix.trim();
		if (value.length() > 4 || value.length() < 3) {
			throw new IllegalArgumentException(value);
		}

		return value;
	}

	private static String macWithSpliter(String mac) {
		StringBuffer buffer = new StringBuffer();
		final char spliter = '-';

		buffer.append(mac.substring(0, 2));
		buffer.append(spliter);
		buffer.append(mac.substring(2, 4));
		buffer.append(spliter);
		buffer.append(mac.substring(4, 6));
		buffer.append(spliter);
		buffer.append(mac.substring(6, 8));
		buffer.append(spliter);
		buffer.append(mac.substring(8, 10));
		buffer.append(spliter);
		buffer.append(mac.substring(10, 12));

		return buffer.toString();
	}

	/**
	 * 字符串包含 Numeric(数字)
	 * 
	 * @param length
	 * @return
	 */
	public static String randomNumeric(int length) {
		return RandomStringUtils.randomNumeric(length);
	}

	/**
	 * 字符串包含 字母，数字，特殊符号
	 * 
	 * @param length
	 * @return
	 */
	public static String randomAscii(int length) {
		return RandomStringUtils.randomAscii(length);
	}

	private static Date addAndTruncate(Date date, int daysOffset) {
		checkNotNull(date, "date must not be null");

		return DateUtils.truncate(DateUtils.addDays(new Date(), daysOffset), Calendar.DAY_OF_MONTH);
	}

	/**
	 * 随机生成基于当前时间前 daysOffset 天的日期时间
	 * 
	 * @param daysOffset
	 * @return
	 */
	public static long randomTime(int daysOffset) {
		return addAndTruncate(new Date(), daysOffset > 0 ? -daysOffset : daysOffset).getTime()
				+ (long) new Random().nextInt(86400);
	}

	public static boolean randomBoolean() {
		return new Random().nextBoolean();
	}

	public static String randomBooleanString() {
		return randomBoolean() ? "true" : "false";
	}

	public static float randomFloat() {
		return new Random().nextFloat();
	}

	public static float randomFloat(float start) {
		return start + randomFloat();
	}

	public static float randomFloat(int start) {
		return start + randomFloat();
	}

	public static double randomDouble() {
		return new Random().nextDouble();
	}

	public static double randomDouble(double start) {
		return start + randomDouble();
	}

	public static double randomDouble(int start) {
		return start + randomDouble();
	}

	private Randoms() {
	}
}
