package com.sky.projects.tool.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Date Util
 * 
 * @author zealot
 */
public final class Dates {

	public static Date add(Date date, int offset) {
		checkNotNull(date, "date must not be null");

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		// 正数往后推,负数往前移动
		calendar.add(Calendar.DATE, offset);

		return calendar.getTime();
	}

	public static String date2Str(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	public static String str2UnixString(String source) {
		return String.valueOf(str2UnixTime(source));
	}

	public static String toString(Date date, String fmt) {
		return new SimpleDateFormat(fmt).format(date);
	}

	public static Long str2UnixTime(String source) {
		try {
			return getUnixTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(source));
		} catch (ParseException e) {
			return getUnixTime(new Date());
		}
	}

	public static Long getUnixTime(Date date) {
		return date.getTime() / 1000;
	}

}
