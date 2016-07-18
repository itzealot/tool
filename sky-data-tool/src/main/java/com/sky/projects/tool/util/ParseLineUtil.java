package com.sky.projects.tool.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * 解析行的工具类
 * 
 * @author zealot
 *
 */
public final class ParseLineUtil {

	/**
	 * 解析不规则的行，如果解析成功，返回解析后的字段列表，长度为 filedSize；否则返回长度为 0 的列表
	 * 
	 * @param line
	 * @param delim
	 *            分隔符
	 * @param defaultValue
	 *            解析时值不存在使用默认值替代
	 * @param filedSize
	 *            字段数
	 * @return
	 */
	public static List<String> parse(String line, String delim, String defaultValue, int filedSize) {
		// 处理第一个为空的情况
		if (line.indexOf(delim) == 0) {
			line = defaultValue + delim + line.substring(1);
		}

		// 进行替换匹配，再创建相应 的 StringTokenizer
		StringTokenizer tokenizer = new StringTokenizer(replace(line, delim, defaultValue), delim);
		List<String> results = append(tokenizer);

		// 解析出错
		if (results.size() >= filedSize) {
			System.out.println(results);
			return results;
		}

		for (int i = results.size(); i < filedSize; i++) {
			results.add(defaultValue);
		}

		return results;
	}

	/**
	 * 解析不规则的行，如果解析成功，返回解析后的字段列表，长度为 filedSize；否则返回长度为 0 的列表
	 * 
	 * @param line
	 * @param delim
	 *            分隔符
	 * @param defaultValue
	 *            解析时值不存在使用默认值替代
	 * @param filedSize
	 *            字段数
	 * @return
	 */
	public static void parse(String line, String delim, String defaultValue, int filedSize, List<String> results) {
		// 处理第一个为空的情况
		if (line.indexOf(delim) == 0) {
			line = defaultValue + delim + line.substring(1);
		}

		// 进行替换匹配，再创建相应 的 StringTokenizer
		append(new StringTokenizer(replace(line, delim, defaultValue), delim), results);

		// 解析出错，清空解析结果
		if (results.size() > filedSize) {
			results.clear();
			return;
		}

		// 填充默认值
		for (int i = results.size(); i < filedSize; i++) {
			results.add(defaultValue);
		}

	}

	public static List<String> parseAuthData(String line) {
		return append(new StringTokenizer(line.replaceAll("\t\t\t", "\tMULL\t"), "\t"));
	}

	public static void parseAuthData(String line, List<String> resluts) {
		append(new StringTokenizer(line.replaceAll("\t\t\t", "\tMULL\t"), "\t"), resluts);
	}

	/**
	 * 如果解析的行是规则的
	 * 
	 * @param line
	 * @param delim
	 * @return
	 */
	public static List<String> parse(String line, String delim) {
		return append(new StringTokenizer(line, delim));
	}

	/**
	 * 如果解析的行是规则的
	 * 
	 * @param line
	 * @param delim
	 * @return
	 */
	public static void parse(String line, String delim, List<String> results) {
		append(new StringTokenizer(line, delim), results);
	}

	/**
	 * 替换，即将 ||| 形式替换为|value|
	 * 
	 * @param source
	 * @param delim
	 * @param defaultValue
	 * @return
	 */
	private static String replace(final String source, String delim, String defaultValue) {
		String oldString = delim + delim + delim;
		String newString = delim + defaultValue;

		StringBuffer buffer = new StringBuffer();
		int index = 0;
		String current = source;

		while ((index = current.indexOf(oldString)) != -1) {
			buffer.append(current.substring(0, index));
			buffer.append(newString);
			current = current.substring(index + delim.length() * 2);
		}

		return buffer.append(current).toString();
	}

	/**
	 * 将 StringTokenizer 的解析结果添加到列表中
	 * 
	 * @param tokenizer
	 * @return
	 */
	private static List<String> append(StringTokenizer tokenizer) {
		List<String> results = new ArrayList<>();

		while (tokenizer.hasMoreTokens()) {
			results.add(tokenizer.nextToken());
		}

		return results;
	}

	/**
	 * 将 StringTokenizer 的解析结果添加到列表中
	 * 
	 * @param tokenizer
	 * @return
	 */
	private static void append(StringTokenizer tokenizer, List<String> results) {
		while (tokenizer.hasMoreTokens()) {
			results.add(tokenizer.nextToken());
		}
	}

	/**
	 * 验证 idTo 与 idFrom 是否非法
	 * 
	 * @param idFrom
	 * @return
	 */
	public static boolean filter(String source, String filter) {
		return filter(source, filter.split(","));
	}

	/**
	 * 验证 idTo 与 idFrom 是否非法
	 * 
	 * @param idFrom
	 * @return
	 */
	public static boolean filter(String source, String[] filters) {
		for (int i = 0, len = filters.length; i < len; i++) {
			if (source.contains(filters[i])) {
				return true;
			}
		}

		return isContainsChinese(source);
	}

	public static boolean isContainsChinese(String source) {
		return Pattern.compile("[\u4e00-\u9fa5]").matcher(source).find();
	}

	public static boolean matchChinese(String source) {
		return Pattern.compile("([\u4e00-\u9fa5]+)").matcher(source).matches();
	}

	private ParseLineUtil() {
	}
}
