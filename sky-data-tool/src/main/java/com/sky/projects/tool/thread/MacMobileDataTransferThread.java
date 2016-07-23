package com.sky.projects.tool.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.entity.SfData;
import com.sky.projects.tool.util.Dates;
import com.sky.projects.tool.util.FileUtil;
import com.sky.projects.tool.util.ParseLineUtil;

public class MacMobileDataTransferThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(MacMobileDataTransferThread.class);

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
	private int dataType;

	public MacMobileDataTransferThread(BlockingQueue<String> queue, String dir, int size, int sleep, int type,
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

		String path = dir + "/" + Dates.date2Str(new Date(), "yyyyMMddHHmmss") + FileUtil.random()
				+ "_999_440300_723005104_0" + type + ".log";

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

		switch (dataType) {
		case 1:// baimi_mac_i_mobile_25754871 数据处理
			mAC = fileds.get(0);
			pHONE = fileds.get(1);
			break;
		case 2: // baimi_mac_o_moblie_56839.txt 数据
			mAC = fileds.get(0);
			pHONE = fileds.get(1);
			LOG.info("fileds:{}", fileds);
			LOG.info("mAC:{}, pHONE:{}, size:{}", mAC, pHONE, fileds.size());
			break;
		case 3: // fenghuo_MOBILE_MAC.txt
			mAC = fileds.get(1);
			pHONE = fileds.get(0);
			break;
		}

		// 去掉 +
		String phone = trimPhone(pHONE);

		// 不为 phone
		if (!isPhone(phone)) {
			try {
				parseErrorDataQueue.put(line);
				LOG.error("mAC:{}, phone:{}, line:{}", mAC, phone, line);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			fileds.clear();
			return;
		}

		// deal mac
		mAC = FileUtil.dealMac(mAC);

		long startTime = new Date().getTime() / 1000;
		datas.add(new SfData(mAC, phone, iMSI, iMEI, aUTH_TYPE, aUTH_CODE, cERTIFICATE_TYPE, cERTIFICATE_CODE, iD_TYPE,
				aCCOUNT, Integer.valueOf("" + startTime), lAST_PLACE));

		// 清空字段
		fileds.clear();
	}

	private static String regexPhone = "^((\\+?86)|(\\(\\+86\\))|852)?(13[0-9][0-9]{8}|15[0-9][0-9]{8}|18[0-9][0-9]{8}|14[0-9][0-9]{8}|17[0-9][0-9]{8}|[0-9]{8})$";

	public static boolean isPhone(String phone) {
		return Pattern.compile(regexPhone).matcher(phone).matches();
	}

	private String trimPhone(final String pHONE) {
		int index = pHONE.indexOf('+');
		return index == -1 ? pHONE : pHONE.substring(index + 1);
	}

	public void stop() {
		this.running = false;
	}
}
