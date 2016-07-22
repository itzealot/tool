package com.sky.projects.tool.client;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.conf.MassConfiguration;
import com.sky.projects.tool.thread.AuthDataTransferThread;
import com.sky.projects.tool.thread.Threads;
import com.sky.projects.tool.util.Files;

/**
 * 对手机认证数据进行解析并生成 .log 和 .log.ok 文件
 * 
 * @author zealot
 *
 */
public class AuthDataParseCli {
	private static final Logger LOG = LoggerFactory.getLogger(AuthDataParseCli.class);

	public static void main(String[] args) {
		LOG.info("AuthData 处理手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		MassConfiguration massConfiguration = new MassConfiguration();
		int poolSize = massConfiguration.getInt("transfer.auth.data.pool.size", 10);
		// 根据线程池大小创建线程池
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		AuthDataTransferThread[] threads = new AuthDataTransferThread[poolSize];

		try {
			String source = massConfiguration.get("transfer.auth.data.source.dir");
			String target = massConfiguration.get("transfer.auth.data.target.dir");
			String suffix = massConfiguration.get("transfer.auth.data.suffix");

			int poolSleep = massConfiguration.getInt("transfer.auth.data.pool.sleep", 200);
			int sleep = massConfiguration.getInt("transfer.auth.data.sleep", 1000);
			int counts = massConfiguration.getInt("transfer.auth.data.sleep.counts", 10000);
			int size = massConfiguration.getInt("transfer.auth.data.jsonSize", 5000);
			int type = massConfiguration.getInt("transfer.auth.data.type", 13);

			// all counts
			AtomicInteger allCounts = new AtomicInteger(0);

			List<File> sourcesFiles = Files.getSourceFiles(source, suffix);
			BlockingQueue<String> parseErrorDataQueue = new LinkedBlockingQueue<>();
			for (int i = 0; i < poolSize; i++) {
				threads[i] = new AuthDataTransferThread(queue, target, size, poolSleep, type, parseErrorDataQueue,
						allCounts);
				threadPool.execute(threads[i]);
			}

			for (File file : sourcesFiles) {
				LOG.info("start deal file name: " + file.getName());
				Files.read(queue, file, sleep, counts);
				LOG.info("finish deal file name: " + file.getName());
			}
		} catch (Exception e) {
			LOG.error("AuthData 处理手动任务手动任务失败!", e);
		}

		// 日志文件读取完毕，主线程监测多线程是否处理完毕
		while (!queue.isEmpty()) {
			LOG.info("finish reading all lines from source files and wait for dealing all lines.");
			Threads.sleep(1000);
		}

		// 关闭线程池与线程池
		for (int i = 0; i < poolSize; i++) {
			threads[i].stop();
		}
		threadPool.shutdown();
	}

}
