package com.sky.projects.tool.client;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.conf.SkyConfiguration;
import com.sky.projects.tool.hbase.Hbase;
import com.sky.projects.tool.redis.RedisDao;
import com.sky.projects.tool.thread.FileRelation2HbaseThread;
import com.sky.projects.tool.thread.Threads;
import com.sky.projects.tool.util.FileUtil;

/**
 * 从文件中的relation记录导入 hbase
 * 
 * @author zealot
 *
 */
public class FileRelation2HbaseCli {
	private static final Logger LOG = LoggerFactory.getLogger(FileRelation2HbaseCli.class);

	public static void main(String[] args) {
		LOG.info("从文件中读取relation记录到hbase手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		SkyConfiguration massConfiguration = new SkyConfiguration();
		int poolSize = massConfiguration.getInt("copy.relation.to.hbase.pool.size", 10);
		// 根据线程池大小创建线程池
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		int queueSizeLimit = massConfiguration.getInt("copy.relation.to.hbase.queue.size.limit", 50000);
		int sleep = massConfiguration.getInt("copy.relation.to.hbase.sleep", 1000);
		int batchSize = massConfiguration.getInt("copy.relation.to.hbase.batch.size", 5000);

		FileRelation2HbaseThread[] threads = new FileRelation2HbaseThread[poolSize];
		String zkUrl = massConfiguration.get("copy.relation.to.hbase.zookeeper.url");

		String tableName = massConfiguration.get("copy.relation.to.hbase.table");
		String fileName = massConfiguration.get("copy.relation.to.hbase.source.file");

		String dir = massConfiguration.get("copy.relation.to.hbase.target.dir");
		String filter = "\",\\,/,',>,<,|,?, ,=,+,[,],{,},%,;,&,^,!,(,)";

		Hbase hbase = new Hbase(zkUrl, tableName);
		RedisDao redisDao = RedisDao.getInstance(massConfiguration.get("redis.conf"));

		for (int i = 0; i < poolSize; i++) {
			threads[i] = new FileRelation2HbaseThread(queue, batchSize, hbase, redisDao, dir, filter);
			threadPool.execute(threads[i]);
		}

		try {
			FileUtil.read(queue, new File(fileName), sleep, queueSizeLimit);
		} catch (Exception e) {
			LOG.error("读取文件中的relation记录到hbase手动任务任务失败!", e);
		}

		while (!queue.isEmpty()) {
			Threads.sleep(2000);
			LOG.info("wait to finish dealing with the queue........");
		}

		// close the thread and the pool shut down
		for (int i = 0; i < poolSize; i++) {
			threads[i].close();
		}
		threadPool.shutdown();
	}
}
