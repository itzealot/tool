package com.sky.projects.tool.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.entity.MysqlRelation;
import com.sky.projects.tool.thread.Threads;
import com.sky.projects.tool.util.Closeables;

public final class MysqlDao {
	private static final Logger LOG = LoggerFactory.getLogger(MysqlDao.class);

	// mysql config
	private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	private static Connection conn = null;

	public MysqlDao(String url, String username, String password) {
		try {
			Class.forName(MYSQL_DRIVER);
			conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			LOG.error("mysql connection failed. url=" + url + ", username=" + username + ", password=" + password, e);
			throw new RuntimeException("mysql connection failed", e);
		}
	}

	/**
	 * 根据传入的参数批量读取 realtion 记录到阻塞队列
	 * 
	 * @param queue
	 * @param rowSize
	 *            一次批量读取 relation 的记录条数
	 * @param queueSizeLimit
	 *            阻塞队列大小，到达该大小时，则主线程休息 sleep(毫秒数 ms)
	 * @param sleep
	 */
	public void execute(BlockingQueue<MysqlRelation> queue, final int rowSize, final int queueSizeLimit,
			final int sleep, int start) {
		String sql = "SELECT * FROM relation LIMIT ?, ?";
		PreparedStatement pstm = null;
		ResultSet rs = null;

		// 批量读取 relation 的次数
		int counts = count("relation");
		int times = (counts - start) / rowSize;
		LOG.info("all mysql's relation records counts are : " + counts);

		try {
			pstm = conn.prepareStatement(sql);
			if (queue.size() > queueSizeLimit) {
				LOG.info("current BlockingQueue size is : " + queue.size() + ", and sleep: " + sleep + " ms");
				Threads.sleep(sleep);
			}

			for (int i = 1; i <= times; i++) {
				readRealtionAndPut(pstm, start + (i - 1) * rowSize, rowSize, queue);
			}

			// counts = count("relation");
			// 将剩下的 relation 记录全部读取到阻塞队列
			readRealtionAndPut(pstm, start + times * rowSize, counts - times * rowSize, queue);
		} catch (SQLException e) {
			LOG.error("create prepareStatement error, sql=" + sql, e);
			throw new RuntimeException("stop execute read records from mysql's relation.");
		} finally {
			Closeables.close(rs, pstm);
		}
	}

	/**
	 * 从 mysql 中读取 relation 并放入阻塞队列
	 * 
	 * limit start, rows
	 * 
	 * @param pstm
	 * @param start
	 * @param rows
	 * @param queue
	 */
	private void readRealtionAndPut(PreparedStatement pstm, int start, int rows, BlockingQueue<MysqlRelation> queue) {
		ResultSet rs = null;
		LOG.info("start execute sql: select * from relation limit " + start + ", " + rows);

		try {
			pstm.setInt(1, start);
			pstm.setInt(2, rows);
			rs = pstm.executeQuery();
			int index = 0;

			while (rs.next()) {
				String idFrom = rs.getString("id_from");
				String fromType = rs.getString("from_type");
				String idTo = rs.getString("id_to");
				String toType = rs.getString("to_type");

				String firstStartTime = rs.getString("first_start_time");
				String firstTerminalNum = rs.getString("first_terminal_num");
				String source = rs.getString("source");
				String createTime = rs.getString("create_time");
				String discoverTimes = rs.getString("discover_times");
				String updateTime = rs.getString("update_time");
				String sysSource = rs.getString("sys_source");
				index++;
				queue.put(new MysqlRelation(idFrom, fromType, idTo, toType, firstStartTime, firstTerminalNum, source,
						createTime, discoverTimes, updateTime, sysSource));
			}

			LOG.info("finish execute select * from relation limit " + start + ", " + rows);
			LOG.info("put mysql's relation records size into queue are : " + index);
		} catch (Exception e) {
			LOG.error("read records from mysql's relation error, where start=" + start + ", rows=" + rows, e);
			throw new RuntimeException("stop execute read records from mysql's relation.");
		} finally {
			Closeables.close(rs);
		}
	}

	/**
	 * 从 mysql 中读取 relation 并放入阻塞队列
	 * 
	 * limit start, rows
	 * 
	 * @param pstm
	 * @param start
	 * @param rows
	 * @param queue
	 */
	public void readRealtionAndPut(int start, int rows, BlockingQueue<MysqlRelation> queue, int queueSizeLimit,
			int sleep) {
		if (queue.size() > queueSizeLimit) {
			LOG.info("current BlockingQueue size is : " + queue.size() + ", and sleep: " + sleep + " ms");
			Threads.sleep(sleep);
		}

		String sql = "SELECT * FROM relation LIMIT ?, ?";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		LOG.info("start execute sql: select * from relation limit " + start + ", " + rows);

		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, start);
			pstm.setInt(2, rows);
			rs = pstm.executeQuery();
			int index = 0;
			while (rs.next()) {
				String idFrom = rs.getString("id_from");
				String fromType = rs.getString("from_type");
				String idTo = rs.getString("id_to");
				String toType = rs.getString("to_type");

				String firstStartTime = rs.getString("first_start_time");
				String firstTerminalNum = rs.getString("first_terminal_num");
				String source = rs.getString("source");
				String createTime = rs.getString("create_time");
				String discoverTimes = rs.getString("discover_times");
				String updateTime = rs.getString("update_time");
				String sysSource = rs.getString("sys_source");
				index++;
				queue.put(new MysqlRelation(idFrom, fromType, idTo, toType, firstStartTime, firstTerminalNum, source,
						createTime, discoverTimes, updateTime, sysSource));
			}

			LOG.info("finish execute select * from relation limit " + start + ", " + rows);
			LOG.info("put mysql's relation records size into queue are : " + index);
		} catch (Exception e) {
			LOG.error("read records from mysql's relation error, where start=" + start + ", rows=" + rows, e);
			throw new RuntimeException("stop execute read records from mysql's relation.");
		} finally {
			Closeables.close(rs, pstm);
		}
	}

	private int count(String tableName) {
		int value = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			pstm = conn.prepareStatement("SELECT count(*) FROM " + tableName);
			rs = pstm.executeQuery();

			if (rs.next()) {
				value = Integer.parseInt(rs.getString(1));
			}
		} catch (SQLException e) {
			LOG.error("count from Mysql's " + tableName + " error, {}", e);
		} finally {
			Closeables.close(rs, pstm);
		}

		return value;
	}

	private String sql = "insert into focus_mac_info(`store_id`,`match_type`,`match_child_type`,`match_value`,create_time) values(23,?,?,?,now())";

	/**
	 * batch insert sd persons into focus_mac_info
	 * 
	 * @param lines
	 */
	public void batchInsertSDPerson(List<String> lines) {
		if (lines == null || lines.isEmpty()) {
			return;
		}

		PreparedStatement pstm = null;
		try {
			pstm = conn.prepareStatement(sql);

			for (String line : lines) {
				if (isBlank(line)) {
					continue;
				}

				String[] arrays = line.split("\t");
				String certCode = "";
				String phone = "";
				String qq = "";

				if (arrays.length >= 1) {
					certCode = arrays[0];
				}

				if (arrays.length >= 3) {
					phone = arrays[2];
				}

				if (arrays.length >= 4) {
					qq = arrays[3];
				}

				if (!isBlank(certCode)) {
					pstm.setString(1, "5");
					pstm.setString(2, "11");
					pstm.setString(3, certCode);
					pstm.addBatch();
				}

				if (!isBlank(phone)) {
					pstm.setString(1, "2");
					pstm.setString(2, "");
					pstm.setString(3, phone);
					pstm.addBatch();
				}

				if (!isBlank(qq)) {
					pstm.setString(1, "6");
					pstm.setString(2, "1030001");
					pstm.setString(3, qq);
					pstm.addBatch();
				}
			}

			pstm.executeBatch();

			LOG.info("finish insert into table, batch lines size {}.", lines.size());
			lines.clear();
		} catch (SQLException e) {
			LOG.error("insert into focus_mac_info table error.", e);
		} finally {
			Closeables.close(pstm);
		}
	}

	private boolean isBlank(String value) {
		return value == null || "".equals(value.trim()) || "MULL".equals(value.trim());
	}

	public void closeConnection() {
		Closeables.close(conn);
	}
}
