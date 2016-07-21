package com.sky.projects.tool.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.entity.AuthData;
import com.sky.projects.tool.entity.MobileAuth;
import com.sky.projects.tool.util.Dates;
import com.sky.projects.tool.util.Files;
import com.sky.projects.tool.util.ParseLineUtil;

public class AuthDataTransferThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(AuthDataTransferThread.class);

	// 目标文件写入目录
	private String dir;
	// 存储当前要处理的行
	private List<String> lines = new ArrayList<>();
	// 是否运行标志
	private volatile boolean running = true;
	// 一次从阻塞队列中取的数据
	private int size = 5000;
	private BlockingQueue<String> queue;
	// 存储转换后的认证数据
	List<AuthData> datas = new ArrayList<AuthData>();
	// 阻塞队列无数据时休息毫秒数
	private int sleep;
	// 日志类型
	private int type;

	// 存储字段
	private List<String> fileds = new ArrayList<>();

	// 保存解析错误的数据
	private BlockingQueue<String> parseErrorDataQueue;
	// 解析错误的行数据
	private List<String> errorLines = new ArrayList<>();

	// 写入 json 文件的数量
	private AtomicInteger allCounts;

	public AuthDataTransferThread(BlockingQueue<String> queue, String dir, int size, int sleep, int type,
			BlockingQueue<String> parseErrorDataQueue, AtomicInteger allCounts) {
		this.queue = queue;
		this.dir = dir;
		this.size = size;
		this.sleep = sleep;
		this.type = type;
		this.parseErrorDataQueue = parseErrorDataQueue;
		this.allCounts = allCounts;
	}

	@Override
	public void run() {
		while (running) {
			if (!queue.isEmpty()) {
				// 从阻塞队列中取数据
				queue.drainTo(lines, size);
				doParse();
				this.lines.clear();
			} else {
				Threads.sleep(sleep);
				writeParseErrorData();
			}
		}

		writeParseErrorData();
		LOG.info("thread stop execute........................");
	}

	private void doParse() {
		if (lines.isEmpty()) {
			return;
		}

		for (String line : lines) {
			doParseLine(line);
		}

		Files.writeWithJson(dir, type, datas, allCounts);
	}

	private synchronized void writeParseErrorData() {
		if (!parseErrorDataQueue.isEmpty()) {
			parseErrorDataQueue.drainTo(errorLines, 10000);
			Files.write(new File(dir + "/parse-error.txt"), errorLines);
			LOG.info("finish write the error parse data into file, size is : " + errorLines.size());
			errorLines.clear();
		}
	}

	private void doParseLine(String line) {
		String defaultValue = "MULL";
		// 解析出行记录
		ParseLineUtil.parse(line, "\t", defaultValue, MobileAuth.JSON_SIZE, fileds);

		if (fileds.isEmpty()) {
			try {
				parseErrorDataQueue.put(line);
				LOG.error("parse data error and put into parseErrorDataQueue, line is : " + line);
			} catch (InterruptedException e) {
				LOG.error("put data into parseErrorDataQueue error, line is : " + line, e);
			}

			return;
		}

		// 将默认值转换为空串
		for (String filed : fileds) {
			if (defaultValue.equals(filed) || "\\N".equals(filed) || "MULL".equals(filed) || "NULL".equals(filed)) {
				filed = "";
			}
		}

		extractAuthData(line);

		// 清空字段
		fileds.clear();
	}

	private void extractAuthData(String line) {
		String mAC = "";
		String iP = "";
		String cERT_TYPE = fileds.get(2); // cert_type
		String cERT_CODE = fileds.get(3); // cert_code
		if ("11".equals(cERT_TYPE) || ("0".equals(cERT_TYPE) && cERT_CODE != null && cERT_CODE.length() == 18)) {
			cERT_TYPE = "";
		} else {// 不是身份证号解析出错，将错误数据添加到阻塞队列
			try {
				parseErrorDataQueue.put(line);
				LOG.error("put data into parseErrorDataQueue by cert type, line is : " + line);
			} catch (InterruptedException e) {
				LOG.error("put data into parseErrorDataQueue error, line is : " + line, e);
			}

			return;
		}

		String uSER_NAME = new String(fileds.get(5).getBytes()); // 姓名
		String nATION_NUM = change2NationNum(fileds.get(8)); // 名族编码
		String cOUNTRY = "CHN";// 国籍
		String wORK_COMPANY = new String(fileds.get(9).getBytes()); // work_company
		String aUTH_TYPE = ""; // mobile protocol
		String aUTH_CODE = fileds.get(1);// Phone number as the auth_code
		String authTime = fileds.get(4);// bind_date

		// 转换时间
		long aUTH_TIME = 0L;
		try {
			aUTH_TIME = Dates.str2UnixTime(authTime);
		} catch (Exception e) {
			aUTH_TIME = Dates.getUnixTime(new Date());
		}

		datas.add(new AuthData(mAC, iP, cERT_TYPE, cERT_CODE, uSER_NAME, nATION_NUM, cOUNTRY, wORK_COMPANY, aUTH_TYPE,
				aUTH_CODE, aUTH_TIME));
	}

	public String change2NationNum(String source) {
		Integer value = 1;
		try {
			value = Integer.valueOf(source);
		} catch (Exception e) {
		}

		return value >= 10 ? "" + value : "0" + value;
	}

	public void stop() {
		this.running = false;
	}
}
