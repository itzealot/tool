package com.sky.projects.tool.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.sky.projects.tool.entity.MysqlRelation;
import com.sky.projects.tool.hbase.Hbase;
import com.sky.projects.tool.redis.RedisDao;
import com.sky.projects.tool.util.Files;
import com.sky.projects.tool.util.ParseLineUtil;

public class FileRelation2HbaseThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(FileRelation2HbaseThread.class);

	private BlockingQueue<String> queue;
	private List<MysqlRelation> relations = new ArrayList<>();
	private List<String> lines = new ArrayList<>();
	private List<String> fileds = new ArrayList<>();
	private volatile boolean running = true;
	private int batchSize;
	private Hbase hbase;
	private RedisDao redisDao;

	// to use get the mysql's info
	private String redisKey = "relation";
	private List<String> hashKeyList = new ArrayList<>();
	private List<String> hashValues = new ArrayList<>();

	// 错误数据
	private static BlockingQueue<String> errorQueue = new LinkedBlockingQueue<String>();
	// 错误数据缓冲
	private List<String> errorLines = new ArrayList<>();

	private String dir;
	private String[] filters;

	public FileRelation2HbaseThread(BlockingQueue<String> queue, int batchSize, Hbase hbase, RedisDao redisDao,
			String dir, String filter) {
		Preconditions.checkNotNull(queue, "queue must not be null");
		Preconditions.checkNotNull(hbase, "hbase must not be null");
		Preconditions.checkNotNull(dir, "dir must not be null");
		Preconditions.checkNotNull(filter, "filter must not be null");

		this.queue = queue;
		this.batchSize = batchSize;
		this.hbase = hbase;
		this.redisDao = redisDao;
		this.dir = dir;
		this.filters = filter.split(",");
	}

	@Override
	public void run() {
		while (running) {
			if (queue.isEmpty()) {
				writeDataLines();
				Threads.sleep(200);
			} else {
				queue.drainTo(lines, batchSize);

				if (lines.isEmpty()) {
					return;
				}

				parseLines();

				loadRedisDataAndSet();

				hbase.put(relations);
				relations.clear();
			}
		}

		writeDataLines();
		LOG.info("stop................................");
	}

	private void writeDataLines() {
		errorQueue.drainTo(errorLines, 10000);
		if (!errorLines.isEmpty()) {
			Files.write(new File(dir + "/error-relation.txt"), errorLines);
			errorLines.clear();
		}
	}

	/**
	 * 从redis 中加载身份数据
	 */
	private void loadRedisDataAndSet() {
		redisDao.hget(redisKey, hashKeyList, hashValues);

		for (int i = 0, len = hashValues.size(); i < len; i++) {
			MysqlRelation relation = relations.get(i);
			String value = hashValues.get(i);

			try {
				String[] values = value.split("\\|");
				int lenValues = values.length;

				if (lenValues >= 1) {// 有 DiscoverTimes
					relation.setDiscoverTimes(values[0]);
				}

				if (lenValues >= 2) {// 有 TerminalNum
					relation.setLastTerminalNum(values[1]);
				}

				if (lenValues >= 3) {// 有 LastStartTime
					relation.setLastStartTime(values[2]);
				}

				if (lenValues >= 4) {// 有 sys_source
					String sys_source1 = values[3]; // sys_source from redis
					// sys_source from file
					String sys_source2 = relation.getSysSource();
					if (isBlank(sys_source2)) {
						sys_source2 = "2";
					}

					int sys_source = 0;
					try {
						sys_source = sys_source | Integer.parseInt(sys_source2);
						sys_source = sys_source | Integer.parseInt(sys_source1);
					} catch (Exception e) {
					}

					relation.setSysSource(String.valueOf(sys_source));
				}
			} catch (Exception e) {
				// TODO
			}
		}

		hashKeyList.clear();
		hashValues.clear();
	}

	private boolean isBlank(String source) {
		return source == null || source.trim().isEmpty() || "MULL".equals(source) || "NULL".equals(source);
	}

	/**
	 * 从读取的行中抽取关系实体
	 */
	private void parseLines() {
		for (String line : lines) {
			ParseLineUtil.parse(line, "\t", fileds);
			if (fileds.size() >= 9) {
				extractRelationAndAdd(line);
			} else {
				try {
					errorQueue.put(line);
					LOG.error("parse line error, line is:" + line);
				} catch (InterruptedException e) {
					LOG.error("parse line into errorQueue error, line is:" + line, e);
				}
			}
			this.fileds.clear();
		}
		this.lines.clear();
	}

	private void extractRelationAndAdd(String line) {
		String idFrom = fileds.get(0);
		String fromType = fileds.get(1);
		String idTo = fileds.get(2);
		String toType = fileds.get(3);

		if (ParseLineUtil.filter(idFrom, filters) || ParseLineUtil.filter(idTo, filters)) {
			try {
				errorQueue.put(line);
			} catch (InterruptedException e) {
				LOG.error("validate line into errorQueue error, line is:" + line, e);
			}

			return;
		}

		String firstStartTime = fileds.get(4);
		String firstTerminalNum = fileds.get(5);
		String source = fileds.get(6);
		String createTime = fileds.get(7);
		String sysSource = fileds.get(8);
		String discoverTimes = "";
		String updateTime = "";

		String spliter = "|";
		this.hashKeyList.add(new StringBuilder().append(idFrom).append(spliter).append(fromType).append(spliter)
				.append(idTo).append(spliter).append(toType).toString());

		this.relations.add(new MysqlRelation(idFrom, fromType, idTo, toType, firstStartTime, firstTerminalNum, source,
				createTime, discoverTimes, updateTime, sysSource));
	}

	public void close() {
		this.running = false;
	}
}
