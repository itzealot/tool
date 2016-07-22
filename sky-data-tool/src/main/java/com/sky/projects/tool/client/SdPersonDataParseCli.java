package com.sky.projects.tool.client;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.conf.MassConfiguration;
import com.sky.projects.tool.thread.SdPersonDataTransferThread;
import com.sky.projects.tool.util.FileUtil;

/**
 * 对sd人员数据进行解析并生成 .log 和 .log.ok 文件
 * 
 * @author zealot
 *
 */
public class SdPersonDataParseCli {
	private static final Logger LOG = LoggerFactory.getLogger(SdPersonDataParseCli.class);

	public static void main(String[] args) {
		LOG.info("sd人员数据处理手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		MassConfiguration skyConfiguration = new MassConfiguration();
		int poolSize = skyConfiguration.getInt("transfer.person.data.pool.size", 10);
		// 根据线程池大小创建线程池
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		SdPersonDataTransferThread[] threads = new SdPersonDataTransferThread[poolSize];

		try {
			String source = skyConfiguration.get("transfer.person.data.source.dir");
			String target = skyConfiguration.get("transfer.person.data.target.dir");
			String suffix = skyConfiguration.get("transfer.person.data.suffix");

			int poolSleep = skyConfiguration.getInt("transfer.person.data.pool.sleep", 200);
			int sleep = skyConfiguration.getInt("transfer.person.data.sleep", 1000);
			int counts = skyConfiguration.getInt("transfer.person.data.sleep.counts", 40000);
			int size = skyConfiguration.getInt("transfer.person.data.jsonSize", 5000);
			int type = skyConfiguration.getInt("transfer.person.data.type", 13);
			int dataType = skyConfiguration.getInt("transfer.person.deal.type", 1);

			// all counts
			AtomicInteger allCounts = new AtomicInteger(0);

			BlockingQueue<String> parseErrorDataQueue = new LinkedBlockingQueue<>();
			for (int i = 0; i < poolSize; i++) {
				threads[i] = new SdPersonDataTransferThread(queue, target, size, poolSleep, type, parseErrorDataQueue,
						allCounts, dataType);
				threadPool.execute(threads[i]);
			}

			List<File> sourcesFiles = FileUtil.getSourceFiles(source, suffix);
			for (File file : sourcesFiles) {
				LOG.info("start deal file name: " + file.getName());
				FileUtil.read(queue, file, sleep, counts);
				LOG.info("finish deal file name: " + file.getName());
			}
		} catch (Exception e) {
			LOG.error("sd人员数据处理手动任务手动任务失败!", e);
		}

		// 日志文件读取完毕，主线程监测多线程是否处理完毕
		while (!queue.isEmpty()) {
			LOG.info("finish reading all lines from source FileUtil and wait for dealing all lines.");
			Threads.sleep(1000);
		}

		// 关闭线程池与线程池
		for (int i = 0; i < poolSize; i++) {
			if (threads[i] != null)
				threads[i].stop();
		}
		threadPool.shutdown();
	}

}
