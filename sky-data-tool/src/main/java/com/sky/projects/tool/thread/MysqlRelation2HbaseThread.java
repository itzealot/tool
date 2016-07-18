package com.sky.projects.tool.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.sky.projects.tool.entity.MysqlRelation;
import com.sky.projects.tool.hbase.Hbase;

public class MysqlRelation2HbaseThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(MysqlRelation2HbaseThread.class);

	private BlockingQueue<MysqlRelation> queue;
	private List<MysqlRelation> relations = new ArrayList<>();
	private volatile boolean running = true;
	private int batchSize;
	private Hbase hbase = null;

	public MysqlRelation2HbaseThread(BlockingQueue<MysqlRelation> queue, int batchSize, Hbase hbase) {
		Preconditions.checkNotNull(queue, "queue must not be null");
		Preconditions.checkNotNull(hbase, "hbase must not be null");

		this.queue = queue;
		this.batchSize = batchSize;
		this.hbase = hbase;
	}

	@Override
	public void run() {
		while (running) {
			if (queue.isEmpty()) {
				Threads.sleep(200);
			} else {
				queue.drainTo(relations, batchSize);

				if (!relations.isEmpty()) {
					hbase.put(relations);
					relations.clear();
				}
			}
		}

		LOG.info("stop................................");
	}

	public void close() {
		this.running = false;
	}
}
