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
import com.sky.projects.tool.thread.TianWeiDataTransferThread;
import com.sky.projects.tool.util.FileUtil;

/**
 * Tain Wei数据进行解析并生成 .log 和 .log.ok 文件
 * 
 * @author zealot
 *
 */
public class TianWeiDataParseCli {
	private static final Logger LOG = LoggerFactory.getLogger(TianWeiDataParseCli.class);

	public static void main(String[] args) {
		LOG.info("Tain Wei数据处理手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		MassConfiguration configuration = new MassConfiguration();
		int poolSize = configuration.getInt("transfer.tianwei.data.pool.size", 10);
		// 根据线程池大小创建线程池
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		TianWeiDataTransferThread[] threads = new TianWeiDataTransferThread[poolSize];

		try {
			String source = configuration.get("transfer.tianwei.data.source.dir");
			String target = configuration.get("transfer.tianwei.data.target.dir");
			String suffix = configuration.get("transfer.tianwei.data.suffix");

			int poolSleep = configuration.getInt("transfer.tianwei.data.pool.sleep", 200);
			int sleep = configuration.getInt("transfer.tianwei.data.sleep", 1000);
			int counts = configuration.getInt("transfer.tianwei.data.sleep.counts", 40000);
			int size = configuration.getInt("transfer.tianwei.data.jsonSize", 5000);
			int type = configuration.getInt("transfer.tianwei.data.type", 13);

			// all counts
			AtomicInteger allCounts = new AtomicInteger(0);

			BlockingQueue<String> parseErrorDataQueue = new LinkedBlockingQueue<>();
			for (int i = 0; i < poolSize; i++) {
				threads[i] = new TianWeiDataTransferThread(queue, target, size, poolSleep, type, parseErrorDataQueue,
						allCounts);
				threadPool.execute(threads[i]);
			}

			List<File> sourcesFiles = FileUtil.getSourceFiles(source, suffix);
			for (File file : sourcesFiles) {
				LOG.info("start deal file name: " + file.getName());
				FileUtil.readByGbk(queue, file, sleep, counts);
				LOG.info("finish deal file name: " + file.getName());
			}

			// 日志文件读取完毕，主线程监测多线程是否处理完毕
			while (!queue.isEmpty()) {
				LOG.info("finish reading all lines from source FileUtil and wait for dealing all lines.");
				Threads.sleep(1000);
			}
		} catch (Exception e) {
			LOG.error("Tain Wei 数据处理手动任务手动任务失败!", e);
		} finally {
			// 关闭线程池与线程池
			for (int i = 0; i < poolSize; i++) {
				if (threads[i] != null)
					threads[i].stop();
			}
			threadPool.shutdown();
		}
	}

}
