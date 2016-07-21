package com.sky.projects.tool.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * 文件读取工具类
 * 
 * @author zealot
 *
 */
public final class Files {

	private static final Logger LOG = LoggerFactory.getLogger(Files.class);

	/**
	 * 处理单个文件，读取内容到阻塞队列中,阻塞队列数据量到达 counts 数量时，休息 sleep ms(用于降低cpu)
	 * 
	 * @param queue
	 * @param file
	 * @param sleep
	 * @param counts
	 * @throws Exception
	 */
	public static void read(BlockingQueue<String> queue, File file, long sleep, int counts) throws Exception {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			long index = 0;

			while ((line = reader.readLine()) != null) {
				queue.put(line);
				index++;

				if (queue.size() >= counts) {
					LOG.info("read thread sleep " + sleep + " ms, current line is : {}, current queue size:{}.", index,
							queue.size());
					Threads.sleep(sleep);
				}
			}

			LOG.info("finish read all lines from file, line counts are : {}", index);
		} catch (Exception e) {
			LOG.error("read file contents line by line into BlockingQueue error.", e);
			throw new Exception("read file contents line by line into BlockingQueue error.", e);
		} finally {
			Closeables.close(reader);
		}
	}

	/**
	 * 从第 currentLine 行开始处理单个文件，读取内容到阻塞队列中,阻塞队列数据量到达 counts 数量时，休息 sleep
	 * ms(用于降低cpu)
	 * 
	 * @param queue
	 * @param file
	 * @param sleep
	 * @param counts
	 */
	public static void read(BlockingQueue<String> queue, File file, long sleep, int counts, int currentLine) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			// 记录第多少行
			long index = 0;

			// 指向要读的第多少行，且每读 10000 行休息，降低 CPU
			while ((line = reader.readLine()) != null && index < currentLine) {
				index++;
				if (index % 10000 == 0) {
					Threads.sleep(sleep);
				}
			}

			LOG.info("current line is : {}, and start read other line and put into BlockingQueue.", index);

			while ((line = reader.readLine()) != null) {
				queue.put(line);
				index++;

				if (queue.size() >= counts) {
					LOG.info("current line is : {}, sleep {} ms.......................", index, sleep);
					Threads.sleep(sleep);
				}
			}
			LOG.info("finish read all lines from file, line counts are : {}", index);
		} catch (Exception e) {
			LOG.error("read file contents line by line into BlockingQueue error.", e);
		} finally {
			Closeables.close(reader);
		}
	}

	/**
	 * 根据配置的源文件目录和文件后缀扫描目标文件并返回s
	 * 
	 * @param source
	 * @param suffix
	 * @return
	 */
	public static List<File> getSourceFiles(final String source, final String suffix) {
		File file = new File(source);
		if (file.exists()) {// 文件存在
			if (file.isDirectory()) {// 是文件目录
				FileFilter filter = new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.getName().endsWith(suffix);
					}
				};
				return Arrays.asList(file.listFiles(filter));
			} else {
				if (source.endsWith(suffix)) {
					return Arrays.asList(new File(source));
				}
			}
		}

		throw new RuntimeException("数据配置路径设置非法，请在conf/conf.properties文件中进行配置");
	}

	/**
	 * 向文件中写入 json 数据及 .ok 文件，并清除元数据
	 * 
	 * @param path
	 * @param json
	 */
	public static <T> void writeWithJson(final String path, final List<T> datas) {
		if (datas == null || datas.isEmpty()) {
			return;
		}

		int len = datas.size();
		String json = new Gson().toJson(datas);
		datas.clear();

		BufferedWriter writer = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(new File(path));
			writer = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("UTF-8")));
			writer.write(json);

			new File(path + ".ok").createNewFile();

			LOG.info("finish write datas with json into file, size is :{}.", len);
		} catch (Exception e) {
			LOG.error("write datas with json into file and create ok file error.", e);
		} finally {
			Closeables.close(writer, fos);
		}

		json = null;
	}

	/**
	 * 向文件中写入 json 数据及 .ok 文件，并清除元数据
	 * 
	 * @param path
	 * @param json
	 */
	public static <T> void writeWithJson(final String dir, int type, final List<T> datas, AtomicInteger counts) {
		if (datas == null || datas.isEmpty()) {
			return;
		}

		String path = dir + "/" + Dates.toString(new Date(), "yyyyMMddHHmmss") + random() + "_124_440200_743218887_0"
				+ type + ".log";

		int len = datas.size();
		String json = new Gson().toJson(datas);
		datas.clear();

		BufferedWriter writer = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(new File(path));
			writer = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("UTF-8")));
			writer.write(json);

			new File(path + ".ok").createNewFile();

			LOG.info("finish write datas with json into file, size is :{}, write all counts are : {}", len,
					counts.addAndGet(len));
			json = null;
		} catch (Exception e) {
			LOG.error("write datas with json into file and create ok file error.", e);
		} finally {
			Closeables.close(writer, fos);
		}
	}

	/**
	 * 随机生成三位数，不够0补齐
	 * 
	 * @return
	 */
	private static String random() {
		int i = new Random().nextInt(1000);
		if (i < 10) {
			return "00" + i;
		} else if (i < 100) {
			return "0" + i;
		}

		return "" + i;
	}

	/**
	 * 向文件中追加写入多行数据
	 * 
	 * @param path
	 * @param json
	 */
	public static void write(final File file, final List<String> lines) {
		BufferedWriter writer = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file, true);
			writer = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("UTF-8")));

			for (String line : lines) {
				writer.write(line);
				writer.write("\n");
			}
		} catch (IOException e) {
			LOG.error("write lines into file and create ok file error.", e);
		} finally {
			Closeables.close(writer, fos);
		}
	}

	private Files() {
	}
}
