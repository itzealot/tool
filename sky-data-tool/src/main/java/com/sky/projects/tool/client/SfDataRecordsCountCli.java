package com.sky.projects.tool.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.conf.MassConfiguration;
import com.sky.projects.tool.thread.SfStringCountThread;
import com.sky.projects.tool.thread.Threads;
import com.sky.projects.tool.util.Closeables;

public class SfDataRecordsCountCli {
	private static final Logger LOG = LoggerFactory.getLogger(SfDataTransferJson.class);

	public static void main(String[] args) {
		LOG.info("SfData 统计手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		MassConfiguration massConfiguration = new MassConfiguration();
		// 根据线程池大小创建线程池
		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		int poolSize = massConfiguration.getInt("transfer.sf.data.pool.size", 10);
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		SfStringCountThread[] threads = new SfStringCountThread[poolSize];

		// 关系存储 map
		Map<String, Integer> relationMap = new ConcurrentHashMap<>();

		// 身份存储 map
		Map<String, Integer> certificationMap = new ConcurrentHashMap<>();

		// 错误数据
		AtomicInteger errorCounts = new AtomicInteger(0);

		// 没有身份没有关系
		AtomicInteger emptyAllCounts = new AtomicInteger(0);

		// 没有关系
		AtomicInteger emptyRelationCounts = new AtomicInteger(0);

		// 未正常解析，保存到文件的数量
		AtomicInteger saveInFileCounts = new AtomicInteger(0);

		// 未正常解析，保存到文件的数量
		AtomicInteger indexCounts = new AtomicInteger(0);

		int allCounts = 0;
		String dir = massConfiguration.get("transfer.sf.data.target.dir");

		try {
			String countsFileName = massConfiguration.get("transfer.sf.data.counts.file.name");
			int poolSleep = massConfiguration.getInt("transfer.sf.data.pool.sleep", 2000);
			int sleep = massConfiguration.getInt("transfer.sf.data.sleep", 200);
			int counts = massConfiguration.getInt("transfer.sf.data.sleep.counts", 10000);
			int size = massConfiguration.getInt("transfer.sf.data.jsonSize");

			for (int i = 0; i < poolSize; i++) {
				threads[i] = new SfStringCountThread(dir, relationMap, certificationMap, errorCounts, emptyAllCounts,
						emptyRelationCounts, saveInFileCounts, indexCounts, queue, size, poolSleep);
				threadPool.execute(threads[i]);
			}

			allCounts = deal(threadPool, queue, new File(countsFileName), size, sleep, counts);
		} catch (Exception e) {
			LOG.error("SfData 处理手动任务手动任务失败!", e);
			e.printStackTrace();
		}

		// 日志文件读取完毕，主线程监测多线程是否处理完毕
		while (!queue.isEmpty()) {
			LOG.info("finish reading all lines from source files and wait for dealing all lines.");
			Threads.sleep(1000);
		}

		// 关闭线程池与线程池
		for (int i = 0; i < poolSize; i++) {
			threads[i].stop();
			threads[i] = null;
		}

		threadPool.shutdown();
		threadPool = null;

		writeResults(dir, allCounts, relationMap, certificationMap, errorCounts, emptyAllCounts, emptyRelationCounts,
				saveInFileCounts);
	}

	public static void writeResults(String dir, int allCounts, Map<String, Integer> relationMap,
			Map<String, Integer> certificationMap, AtomicInteger errorCounts, AtomicInteger emptyAllCounts,
			AtomicInteger emptyRelationCounts, AtomicInteger saveInFileCounts) {
		try {
			new File(dir).mkdirs();
		} catch (Exception e) {
		}
		LOG.info("start write counts into " + dir + "/resuts-counts.txt");
		writeCounts(dir, allCounts, relationMap, certificationMap, errorCounts, emptyAllCounts, emptyRelationCounts,
				saveInFileCounts);
		LOG.info("finish write counts into " + dir + "/resuts-counts.txt");
		errorCounts = null;
		emptyAllCounts = null;
		emptyRelationCounts = null;

		LOG.info("start write counts into " + dir + "/resuts-relation.txt");
		writeMap(dir + "/resuts-relation.txt", relationMap);
		LOG.info("finish write counts into " + dir + "/resuts-relation.txt");
		relationMap = null;

		LOG.info("start write counts into " + dir + "/resuts-certification.txt");
		writeMap(dir + "/resuts-certification.txt", certificationMap);
		LOG.info("finish write counts into " + dir + "/resuts-certification.txt");
		certificationMap = null;
	}

	public static void write(String dir, int allCounts, Map<String, Integer> relationMap,
			Map<String, Integer> certificationMap, AtomicInteger errorCounts, AtomicInteger emptyAllCounts,
			AtomicInteger emptyRelationCounts, AtomicInteger saveInFileCounts, int counts) {
		String countsDir = dir + "/counts" + counts;

		try {
			new File(countsDir).mkdirs();
		} catch (Exception e) {
		}
		LOG.info("start write counts into " + countsDir + "/resuts-counts.txt");
		writeCounts(countsDir, allCounts, relationMap, certificationMap, errorCounts, emptyAllCounts,
				emptyRelationCounts, saveInFileCounts);
		LOG.info("finish write counts into " + countsDir + "/resuts-counts.txt");

		LOG.info("start write counts into " + countsDir + "/resuts-relation.txt");
		writeMap(countsDir + "/resuts-relation.txt", relationMap);
		LOG.info("finish write counts into " + countsDir + "/resuts-relation.txt");

		LOG.info("start write counts into " + countsDir + "/resuts-certification.txt");
		writeMap(countsDir + "/resuts-certification.txt", certificationMap);
		LOG.info("finish write counts into" + countsDir + "/resuts-certification.txt");
	}

	public static void writeCounts(String dir, int allCounts, Map<String, Integer> relationMap,
			Map<String, Integer> certificationMap, AtomicInteger errorCounts, AtomicInteger emptyAllCounts,
			AtomicInteger emptyRelationCounts, AtomicInteger saveInFileCounts) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(dir + "/resuts-counts.txt"));

			// write all counts
			writer.write("all records count: " + allCounts + "\n");
			writer.write("error records count: " + errorCounts.get() + "\n");
			writer.write("empty certification and relation records count: " + emptyAllCounts.get() + "\n");
			writer.write("save into file records count: " + saveInFileCounts.get() + "\n");
			writer.write("empty relation records count: " + emptyRelationCounts.get() + "\n");
			writer.write("all relation records count: " + relationMap.size() + "\n");
			writer.write("all certification records count: " + certificationMap.size() + "\n");
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		} finally {
			Closeables.close(writer);
		}
	}

	private static void writeMap(String path, Map<String, Integer> map) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(path), true);
			for (Entry<String, Integer> set : map.entrySet()) {
				writer.write(set.getKey() + "\t" + set.getValue() + "\n");
			}
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		} finally {
			Closeables.close(writer);
		}
	}

	/**
	 * 处理单个文件
	 * 
	 * @param file
	 * @param target
	 * @param jsonSize
	 */
	private static int deal(ExecutorService threadPool, BlockingQueue<String> queue, File file, int size, long sleep,
			int counts) {
		BufferedReader reader = null;
		int index = 0;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = reader.readLine()) != null) {
				queue.put(line);

				if (queue.size() >= counts) {
					LOG.info("read thread sleep " + sleep + " ms");
					Threads.sleep(sleep);
				}

				index++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return index;
	}
}
