package com.sky.projects.tool.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.entity.MysqlRelation;
import com.sky.projects.tool.util.Closeables;
import com.sky.projects.tool.util.Dates;

/**
 * HBase 基本操作
 * 
 * HBaseAdmin ---> Admin
 * 
 * HTableDescriptor ---> Table
 * 
 * HColumnDescriptor ---> ColumnFamily
 * 
 * @version 0.98
 * @author zt
 */
public final class Hbase {
	private static final Logger LOG = LoggerFactory.getLogger(Hbase.class);

	private static Configuration conf = null;
	public HTableInterface table = null;
	private static HConnection connection = null;

	public static final String COL_FAMILY = "cf";
	public static final byte[] COL_FAMILY_BYTES = Bytes.toBytes(COL_FAMILY);
	public static final String SPILITER = "|";
	private static AtomicInteger countsAll = new AtomicInteger(0);

	public Hbase(String zkUrl, String tableName) {
		try {
			conf = HBaseConfigBuilder.createConfiguration(zkUrl);
			// table = new HTable(conf, TABLE_RELATION_BYTES);
			connection = HConnectionManager.createConnection(conf);
			table = connection.getTable(tableName);
		} catch (IOException e) {
			LOG.error("get the " + tableName + " table error", e);
			throw new RuntimeException("get the " + tableName + " table error", e);
		}
	}

	/**
	 * 使用多线程存在线程安全问题，会出现 flushCommit 异常，可以使用 synchronized 关键字或者使用单线程
	 * 
	 * @param relations
	 */
	public synchronized void put(final List<MysqlRelation> relations) {
		List<Put> puts = new ArrayList<>();

		try {
			for (MysqlRelation relation : relations) {
				String rowKey = relation.getIdFrom() + SPILITER + relation.getFromType() + SPILITER + relation.getIdTo()
						+ SPILITER + relation.getToType();

				String[] columns = new String[] { "first_start_time", "first_terminal_num", "source", "create_time",
						"discover_times", "update_time", "sys_source", "last_start_time", "last_terminal_num" };

				String first_start_time = relation.getFirstStartTime();
				String first_terminal_num = relation.getFirstTerminalNum();
				String source = relation.getSource();
				String create_time = relation.getCreateTime();
				String discover_times = relation.getDiscoverTimes();
				String update_time = relation.getUpdateTime();
				String sys_source = relation.getSysSource();
				if (isBlank(sys_source)) {
					sys_source = "2";
				}

				if (isBlank(update_time)) {
					update_time = first_start_time;
				}

				if (isBlank(discover_times)) {
					discover_times = "1";
				}

				// 转换时间
				first_start_time = Dates.str2UnixString(first_start_time);
				update_time = Dates.str2UnixString(update_time);
				create_time = Dates.str2UnixString(create_time);

				String last_start_time = relation.getLastStartTime();
				String last_terminal_num = relation.getLastTerminalNum();

				if (isBlank(last_start_time) || Long.parseLong(first_start_time) > Long.parseLong(last_start_time)) {
					last_start_time = first_start_time;
					last_terminal_num = first_terminal_num;
				}

				if (isBlank(last_terminal_num)) {
					last_terminal_num = first_terminal_num;
				}

				String[] values = new String[] { first_start_time, first_terminal_num, source, create_time,
						discover_times, update_time, sys_source, last_start_time, last_terminal_num };

				for (int i = 0, len = values.length; i < len; i++) {
					if (values[i] == null) {
						values[i] = "";
					}
				}

				String rowKeyReverse = relation.getIdTo() + SPILITER + relation.getToType() + SPILITER
						+ relation.getIdFrom() + SPILITER + relation.getFromType();
				puts.add(newPut(rowKey, columns, values));
				puts.add(newPut(rowKeyReverse, columns, values));
			}

			table.put(puts);

			int batchCounts = relations.size() * 2;
			LOG.info("finish put data into hbase, batch size is " + batchCounts + ", push all counts are :"
					+ countsAll.addAndGet(batchCounts));

			puts = null;
		} catch (IOException e) {
			LOG.error("put data into hbase's relation error, colFamily=" + COL_FAMILY, e);
		}
	}

	public Put newPut(String rowKey, String[] columns, String[] values) throws IOException {
		Put put = new Put(Bytes.toBytes(rowKey));

		for (int j = 0, len = columns.length; j < len; j++) {
			put.add(COL_FAMILY_BYTES, Bytes.toBytes(columns[j]), Bytes.toBytes(values[j]));
		}

		// 这里的put操作不会立即提交数据到远程服务器，要显式调用flushCommits方法才行
		return put;
	}

	public Put newPut(String rowKey, byte[][] columns, byte[][] values) throws IOException {
		Put put = new Put(Bytes.toBytes(rowKey));

		for (int j = 0, len = columns.length; j < len; j++) {
			put.add(COL_FAMILY_BYTES, columns[j], values[j]);
		}

		// 这里的put操作不会立即提交数据到远程服务器，要显式调用flushCommits方法才行
		return put;
	}

	public Put newPut(String rowKey, String[] columns, byte[][] values) throws IOException {
		Put put = new Put(Bytes.toBytes(rowKey));

		for (int j = 0, len = columns.length; j < len; j++) {
			put.add(COL_FAMILY_BYTES, Bytes.toBytes(columns[j]), values[j]);
		}

		// 这里的put操作不会立即提交数据到远程服务器，要显式调用flushCommits方法才行
		return put;
	}

	private boolean isBlank(String source) {
		return source == null || source.trim().isEmpty() || "MULL".equals(source) || "NULL".equals(source);
	}

	public void close() {
		Closeables.close(table);
	}

}