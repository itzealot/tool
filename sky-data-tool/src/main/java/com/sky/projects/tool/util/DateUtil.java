/**
 * 
 */
package com.sky.projects.tool.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: 任子行网络技术股份有限公司
 * </p>
 * 
 * @version 1.00
 * @since May 11, 2015
 * @author Zhushengzun
 * 
 *         Modified History:
 * 
 */
public class DateUtil {

	public static String DateToStr(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String str = format.format(date);
		return str;
	}

	public static String DateToStr(Date date, String fmt) {

		SimpleDateFormat format = new SimpleDateFormat(fmt);
		String str = format.format(date);
		return str;
	}

	public static long Date2Long(Date d, int sec) {
		return d.getTime() / 1000 + sec;
	}

	public static Date StrToDate(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date strToDate(String str, String format) {
		Date date = null;

		try {
			date = new SimpleDateFormat("yyyyMMdd").parse(str);
		} catch (ParseException e) {
			// TODO
		}

		return date;
	}

	public static Date add(Date date, int offset) {
		checkNotNull(date, "date must not be null");

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		// 正数往后推,负数往前移动
		calendar.add(Calendar.DATE, offset);

		return calendar.getTime();
	}

	public static Date StrToDate(String str, SimpleDateFormat format) {
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 
	 * @param 系统数据库默认日期格式
	 *            dateTime
	 * @return yyyy-MM-dd HH:mm:ss
	 * @author add by shen
	 */
	public static String strToDateTime(Date dateTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = null;
		try {
			date = format.format(dateTime);
		} catch (Exception e) {
			e.getMessage();
		}
		return date;
	}

	public static String str2UnixString(String source) {
		return String.valueOf(str2UnixTime(source));
	}

	public static Long str2UnixTime(String source) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;

		try {
			date = format.parse(source);
		} catch (ParseException e) {
			date = new Date();
		}

		return getUnixTime(date);
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * 将date类型转换unix时间的Long值
	 * 
	 * @param date
	 * @return
	 */
	public static Long getUnixTime(Date date) {
		long diffSeconds = date.getTime() / 1000;
		return diffSeconds;
	}

	public static String getUnixTimeStr(long seconds) {
		String time = sdf.format(new Date(seconds * 1000));
		return time;
	}

	public static String getUnixTimeStr(Date date) {
		String dateStr = null;
		try {
			dateStr = sdf.format(date);
		} catch (Exception e) {
			e.getMessage();
		}
		return dateStr;
	}

	public static String getCurDateTime() {
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		String curTime = formatter.format(new Date());
		return curTime;
	}

	public static String getDateTimeStr(Date d) {
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		String curTime = formatter.format(d);
		return curTime;
	}

	private static SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 获取当前日期的制定前几天
	 * 
	 * @param day:比如获取前1天的就写1
	 * @return 以yyyyMMdd为格式的日期字符串
	 */
	public static String getBeforeDate(int day) {
		Calendar c = Calendar.getInstance();
		c = getDay(c, day);
		return dateSdf.format(c.getTime());
	}

	/** 查找日历 **/
	private static Calendar getDay(Calendar c, int day) {
		c.add(Calendar.DATE, day);
		return c;

	}

	public static String changeDiffSecondToDate(long seconds) {
		String time = sdf.format(new Date(seconds * 1000));
		return time;
	}

	public static void main(String[] args) {

		getBeforeDate(-27);
		Date d = new Date();
		long l = 0L;
		l = DateUtil.Date2Long(d, 0);
		System.out.println("long is: " + l);
		l = DateUtil.Date2Long(d, 3600);
		System.out.println("long is: " + l);

		System.out.println(str2UnixString("2016-06-06 18:18:18"));
		System.out.println(str2UnixTime("2016-06-06 18:18:18"));
	}

}
