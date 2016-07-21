package com.sky.projects.tool.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.mysql.MysqlDao;

public class SdPersonThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(SdPersonThread.class);

	private volatile boolean running = true;
	private BlockingQueue<String> queue;
	private List<String> lines = new ArrayList<>();
	private int batchSize;
	private MysqlDao dao;

	public SdPersonThread(BlockingQueue<String> queue, int batchSize, String url, String username, String password) {
		this.queue = queue;
		this.batchSize = batchSize;
		dao = new MysqlDao(url, username, password);
	}

	@Override
	public void run() {
		while (running) {
			if (queue.isEmpty()) {
				Threads.sleep(200);
			} else {
				queue.drainTo(lines, batchSize);
				if (!lines.isEmpty()) {
					LOG.info("take from BlockingQueue lines size :{}.", lines.size());
					dao.batchInsertSDPerson(lines);
				}
			}
		}

		dao.closeConnection();
	}

	public void stop() {
		this.running = false;
	}

}
