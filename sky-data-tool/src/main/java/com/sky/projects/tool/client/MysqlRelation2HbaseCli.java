package com.sky.projects.tool.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.conf.SkyConfiguration;
import com.sky.projects.tool.entity.MysqlRelation;
import com.sky.projects.tool.hbase.Hbase;
import com.sky.projects.tool.mysql.MysqlDao;
import com.sky.projects.tool.thread.MysqlRelation2HbaseThread;
import com.sky.projects.tool.thread.Threads;

/**
 * 将 mysql 的 relation 记录导入 habse 工具
 * 
 * @author zealot
 *
 */
public class MysqlRelation2HbaseCli {
	private static final Logger LOG = LoggerFactory.getLogger(MysqlRelation2HbaseCli.class);

	public static void main(String[] args) {
		LOG.info("读取relation记录到hbase手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		BlockingQueue<MysqlRelation> queue = new LinkedBlockingQueue<>();
		SkyConfiguration massConfiguration = new SkyConfiguration();
		int poolSize = massConfiguration.getInt("copy.relation.to.hbase.pool.size", 10);
		// 根据线程池大小创建线程池
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);

		int rowSize = massConfiguration.getInt("copy.relation.to.hbase.mysql.rows", 5000);
		int start = massConfiguration.getInt("copy.relation.to.hbase.mysql.start", 0);
		int queueSizeLimit = massConfiguration.getInt("copy.relation.to.hbase.queue.size.limit", 50000);
		int sleep = massConfiguration.getInt("copy.relation.to.hbase.sleep", 1000);
		int batchSize = massConfiguration.getInt("copy.relation.to.hbase.batch.size", 1000);

		MysqlRelation2HbaseThread[] threads = new MysqlRelation2HbaseThread[poolSize];
		String zkUrl = massConfiguration.get("copy.relation.to.hbase.zookeeper.url");
		String tableName = massConfiguration.get("copy.relation.to.hbase.table");
		Hbase hbase = new Hbase(zkUrl, tableName);

		for (int i = 0; i < poolSize; i++) {
			threads[i] = new MysqlRelation2HbaseThread(queue, batchSize, hbase);
			threadPool.execute(threads[i]);
		}

		try {
			String jdbcUrl = massConfiguration.get("mysql.conn");
			String[] values = jdbcUrl.split("\\|");
			String url = values[0];
			String username = values[1];
			String password = values[2];

			LOG.info("start read mysql's relation table records...................");
			MysqlDao dao = new MysqlDao(url, username, password);
			dao.execute(queue, rowSize, queueSizeLimit, sleep, start);
			LOG.info("finish read mysql's relation table records...................");
		} catch (Exception e) {
			LOG.error("读取relation记录到hbase手动任务任务失败!", e);
		}

		while (!queue.isEmpty()) {
			LOG.info("wait to finish dealing with the queue........");
			Threads.sleep(2000);
		}

		// close the thread and the pool shut down
		for (int i = 0; i < poolSize; i++) {
			threads[i].close();
		}
		threadPool.shutdown();
	}
}
