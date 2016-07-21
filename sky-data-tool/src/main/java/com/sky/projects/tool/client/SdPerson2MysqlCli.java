package com.sky.projects.tool.client;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.conf.SkyConfiguration;
import com.sky.projects.tool.thread.SdPersonThread;
import com.sky.projects.tool.util.FileUtil;

/**
 * 读取SdPerson插入Mysql 工具
 * 
 * @author zealot
 *
 */
public class SdPerson2MysqlCli {
	private static final Logger LOG = LoggerFactory.getLogger(SdPerson2MysqlCli.class);

	public static void main(String[] args) {
		LOG.info("读取SdPerson插入Mysql手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		SkyConfiguration configuration = new SkyConfiguration();
		int poolSize = configuration.getInt("sd.person.pool.size", 5);
		SdPersonThread[] threads = new SdPersonThread[poolSize];
		// 根据线程池大小创建线程池
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		BlockingQueue<String> queue = new LinkedBlockingQueue<>();

		try {
			String file = configuration.get("sd.person.source.file.path");
			int batchSize = configuration.getInt("sd.person.pool.batch.size", 1000);
			int counts = configuration.getInt("sd.person.limit.counts", 20000);
			int sleep = configuration.getInt("sd.person.limit.counts.sleep", 1000);

			String jdbcUrl = configuration.get("mysql.conn");
			String[] values = jdbcUrl.split("\\|");
			String url = values[0];
			String username = values[1];
			String password = values[2];

			for (int i = 0; i < poolSize; i++) {
				threads[i] = new SdPersonThread(queue, batchSize, url, username, password);
				threadPool.execute(threads[i]);
			}

			FileUtil.read(queue, new File(file), sleep, counts);

			while (!queue.isEmpty()) {
				LOG.info("main thread wait to deal the lines.........");
				Threads.sleep(1000);
			}
		} catch (Exception e) {
			LOG.error("读取SdPerson插入Mysql手动任务任务失败!", e);
		} finally {
			for (int i = 0; i < poolSize; i++) {
				if (threads[i] != null) {
					threads[i].stop();
				}
			}
			threadPool.shutdown();
		}
	}
}
