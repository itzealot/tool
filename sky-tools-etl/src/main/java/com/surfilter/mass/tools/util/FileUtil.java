package com.surfilter.mass.tools.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件读取工具类
 * 
 * @author zealot
 *
 */
public final class FileUtil {

	public static final Charset UTF_8 = StandardCharsets.UTF_8;
	public static final String NEW_LINE = "\n";
	public static final int BASH_SIZE = 2000;

	/**
	 * 根据配置的源文件目录和文件后缀扫描目标文件并返回
	 * 
	 * @param src
	 * @param suffix
	 * @return
	 */
	public static List<File> getSourceFiles(final String src, final String suffix) {
		File file = new File(src);
		if (file.exists()) {
			if (file.isDirectory()) {
				return Arrays.asList(file.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.getName().endsWith(suffix);
					}
				}));
			} else {
				if (src.endsWith(suffix)) {
					return Arrays.asList(new File(src));
				}
			}
		}

		throw new RuntimeException("path error :" + src);
	}

	/**
	 * 根据配置的源文件目录和文件后缀扫描目标文件并返回
	 * 
	 * @param src
	 * @return
	 */
	public static List<File> getSourceFiles(final String src) {
		File file = new File(src);
		if (file.exists() && file.isDirectory()) {
			return Arrays.asList(file.listFiles());
		}

		throw new RuntimeException("src dir error :" + src);
	}

	/**
	 * 文件追加写入
	 * 
	 * @param file
	 * @param lines
	 */
	public static void append(final File file, final List<String> lines) {
		BufferedWriter writer = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file, true);
			writer = new BufferedWriter(new OutputStreamWriter(fos, UTF_8));

			for (String line : lines) {
				writer.write(line);
				writer.write(NEW_LINE);
			}
		} catch (IOException e) {
			throw new RuntimeException("append into file with line error.", e);
		} finally {
			Closeables.close(writer, fos);
		}
	}

	/**
	 * 将多行合并为一行
	 * 
	 * @param src
	 * @param dst
	 * @param everyLineCounts
	 * @param spliter
	 * @throws Exception
	 */
	public static void read(File src, File dst, int everyLineCounts, char spliter) throws Exception {
		BufferedReader reader = null;
		FileInputStream in = null;

		try {
			in = new FileInputStream(src);
			reader = new BufferedReader(new InputStreamReader(in, FileUtil.UTF_8));

			long lineCounts = 0; // 对象数
			int filedCounts = 0; // 字段数

			List<String> lines = new ArrayList<>(BASH_SIZE);
			StringBuffer buffer = new StringBuffer();

			String line = null;
			while ((line = reader.readLine()) != null) {
				lineCounts++;
				buffer.append(line.replace("" + spliter, " ").trim()); // 替换所有的分隔符为空格
				filedCounts++;

				if (filedCounts == everyLineCounts) {// 批量行的最后一行
					filedCounts = 0;
					lines.add(buffer.toString());

					if (lines.size() >= BASH_SIZE) { // 批量行，追加写入目标文件
						FileUtil.append(dst, lines);
						lines.clear();
					}

					buffer = new StringBuffer();
				} else {
					buffer.append(spliter);
				}

				if (lineCounts % 2000 == 0) {
					Thread.sleep(200);
				}
			}

			if (lines.size() > 0) {
				FileUtil.append(dst, lines);
			}

			System.out.println("finish deal file, all lines : " + lineCounts);
		} catch (Exception e) {
			throw new RuntimeException("read file contents line by line error.", e);
		} finally {
			Closeables.close(reader, in);
		}
	}

	public static String suffix(String src) {
		int index = src.indexOf('.');
		return index == -1 ? ".txt" : src.substring(index);
	}

	public static String trim(String src) {
		return src.replaceAll("\"", "").trim();
	}

	private FileUtil() {
	}
}
