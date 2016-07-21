package com.sky.projects.tool.client;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.conf.SkyConfiguration;
import com.sky.projects.tool.thread.SfString2JsonThread;
import com.sky.projects.tool.thread.Threads;
import com.sky.projects.tool.util.Files;

/**
 * 根据第三方的身份关系数据(使用\t分隔)的文件，将其转换为SfData的json文件，并通过Kafka传输，进行入库与抽取关系、抽取身份
 * 
 * @author zt
 *
 */
public class SfDataTransferJson {
	private static final Logger LOG = LoggerFactory.getLogger(SfDataTransferJson.class);

	public static void main(String[] args) {
		LOG.info("SfData 处理手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		// 根据线程池大小创建线程池
		SkyConfiguration massConfiguration = new SkyConfiguration();
		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		BlockingQueue<String> concurrentDatas = new LinkedBlockingQueue<>();

		int poolSize = massConfiguration.getInt("transfer.sf.data.pool.size", 10);
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		SfString2JsonThread[] threads = new SfString2JsonThread[poolSize];

		try {
			String source = massConfiguration.get("transfer.sf.data.source.dir");
			String target = massConfiguration.get("transfer.sf.data.target.dir");
			String suffix = massConfiguration.get("transfer.sf.data.suffix");
			int jsonSize = massConfiguration.getInt("transfer.sf.data.jsonSize", 5000);
			int type = massConfiguration.getInt("transfer.sf.data.type", 16);
			int poolSleep = massConfiguration.getInt("transfer.sf.data.pool.sleep", 200);
			int sleep = massConfiguration.getInt("transfer.sf.data.sleep", 1000);
			int counts = massConfiguration.getInt("transfer.sf.data.sleep.counts", 60000);

			List<File> sourcesFiles = Files.getSourceFiles(source, suffix);

			for (int i = 0; i < poolSize; i++) {
				threads[i] = new SfString2JsonThread(target, queue, concurrentDatas, jsonSize, poolSleep, type);
				threadPool.execute(threads[i]);
			}

			for (File file : sourcesFiles) {
				LOG.info("start deal file name: " + file.getName());
				Files.read(queue, file, sleep, counts);
				LOG.info("finish deal file name: " + file.getName());
			}
		} catch (Exception e) {
			LOG.error("SfData 处理手动任务手动任务失败!", e);
		}

		// 日志文件读取完毕，主线程监测多线程是否处理完毕
		while (!queue.isEmpty()) {
			LOG.info("finish reading all lines from source files and wait for dealing all lines.");
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
