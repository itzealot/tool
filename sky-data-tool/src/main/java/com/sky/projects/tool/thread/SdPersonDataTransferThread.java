package com.sky.projects.tool.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.entity.SfData;
import com.sky.projects.tool.util.DateUtil;
import com.sky.projects.tool.util.FileUtil;
import com.sky.projects.tool.util.IDCardUtil;
import com.sky.projects.tool.util.ParseLineUtil;

public class SdPersonDataTransferThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(SdPersonDataTransferThread.class);

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
	List<SfData> datas = new ArrayList<SfData>();
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
	int dataType;

	public SdPersonDataTransferThread(BlockingQueue<String> queue, String dir, int size, int sleep, int type,
			BlockingQueue<String> parseErrorDataQueue, AtomicInteger allCounts, int dataType) {
		this.queue = queue;
		this.dir = dir;
		this.size = size;
		this.sleep = sleep;
		this.type = type;
		this.parseErrorDataQueue = parseErrorDataQueue;
		this.allCounts = allCounts;
		this.dataType = dataType;
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

		String path = dir + "/" + DateUtil.DateToStr(new Date(), "yyyyMMddHHmmss") + FileUtil.random()
				+ "_133_440300_723005105_0" + type + ".log";

		FileUtil.writeWithJson(path, datas, allCounts);
	}

	private synchronized void writeParseErrorData() {
		if (!parseErrorDataQueue.isEmpty()) {
			parseErrorDataQueue.drainTo(errorLines, 10000);
			FileUtil.write(new File(dir + "/parse-error-" + dataType + ".txt"), errorLines);
			LOG.info("finish write the error parse data into file, size is : " + errorLines.size());
			errorLines.clear();
		}
	}

	private void doParseLine(String line) {
		ParseLineUtil.parse(line, "\t", fileds);

		String mAC = "";
		String pHONE = "";
		String iMEI = "";
		String iMSI = "";
		String aUTH_TYPE = "";
		String aUTH_CODE = "";
		String cERTIFICATE_TYPE = "";
		String cERTIFICATE_CODE = "";
		String iD_TYPE = "";
		String aCCOUNT = "";
		String lAST_PLACE = "";

		if (fileds.isEmpty()) {
			try {
				parseErrorDataQueue.put(line);
				LOG.error("parse line error, line:{}", line);
			} catch (InterruptedException e) {
				LOG.error("put line into queue error", e);
			}
			return;
		}

		int len = fileds.size();

		if (len >= 1) {// certificate code
			try {
				cERTIFICATE_CODE = IDCardUtil.transferIDCard(fileds.get(0));
			} catch (Exception e) {
				LOG.error("certificate code error, line:{}", line);
				return;
			}
			if (!isBlank(cERTIFICATE_CODE)) {
				cERTIFICATE_TYPE = "1021111";
			} else {
				cERTIFICATE_CODE = "";
			}
		}

		if (len >= 2) { // name
			aUTH_CODE = fileds.get(1);

			if (aUTH_CODE.indexOf("|") != -1) {
				aUTH_CODE = aUTH_CODE.split("\\|")[1];
			}

			if (!isBlank(aUTH_CODE)) {
				aUTH_TYPE = "1021902";
			} else {
				aUTH_CODE = "";
			}
		}

		if (len >= 3) {
			pHONE = fileds.get(2);
			if (isBlank(pHONE)) {
				pHONE = "";
			}
		}

		if (len >= 4) {
			aCCOUNT = fileds.get(3);
			if (isBlank(aCCOUNT)) {
				iD_TYPE = "1030001"; // QQ protocol
			} else {
				aCCOUNT = "";
			}
		}

		long startTime = new Date().getTime() / 1000;
		datas.add(new SfData(mAC, pHONE, iMSI, iMEI, aUTH_TYPE, aUTH_CODE, cERTIFICATE_TYPE, cERTIFICATE_CODE, iD_TYPE,
				aCCOUNT, Integer.valueOf("" + startTime), lAST_PLACE));

		// 清空字段
		fileds.clear();
	}

	private boolean isBlank(String value) {
		return value == null || "".equals(value.trim()) || "MULL".equals(value.trim());
	}

	public void stop() {
		this.running = false;
	}
}
