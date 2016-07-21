package com.sky.projects.tool.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.entity.SfData;
import com.sky.projects.tool.util.Dates;
import com.sky.projects.tool.util.Files;
import com.sky.projects.tool.util.SfDataTransferUtil;

public class SfString2JsonThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(SfString2JsonThread.class);

	public static final String SPLITER = "\t";
	public static final int JSON_SIZE = 12;
	private static final int bufferSize = 10000;

	private static AtomicInteger allCounts = new AtomicInteger(0);
	private static AtomicInteger allErrorCounts = new AtomicInteger(0);

	private String dir;
	private List<String> lines = new ArrayList<>();
	private volatile boolean running = true;
	private int size = 1000;
	private BlockingQueue<String> queue;
	List<SfData> datas = new ArrayList<SfData>();

	// 存储未正常解析的数据，需要写入文件
	private BlockingQueue<String> concurrentDatas;

	// 存储
	private List<String> currentStrings = new ArrayList<>();
	private int sleep;
	private int type;

	public SfString2JsonThread(String dir, BlockingQueue<String> queue, BlockingQueue<String> concurrentDatas, int size,
			int sleep, int type) {
		super();
		this.dir = dir;
		this.queue = queue;
		this.size = size;
		this.concurrentDatas = concurrentDatas;
		this.sleep = sleep;
		this.type = type;
	}

	@Override
	public void run() {
		while (running) {
			if (!queue.isEmpty()) {// 阻塞队列不为空
				queue.drainTo(this.lines, size);// 批量从 BlockingQueue 中获取数据并进行解析
				if (this.lines.size() == 0) {
					continue;
				}

				LOG.info("start deal the data and write into file, size is: " + lines.size());
				for (String line : lines) {
					SfData data = null;
					try {
						data = parseLine(line);
						if (data != null)
							datas.add(data);
					} catch (Exception e) {
						// TODO
					}
				}

				this.lines.clear();

				Files.writeWithJson(dir, type, datas, allCounts);

				writeIntoFile();
			} else {
				writeIntoFile();
				Threads.sleep(sleep);
			}
		}

		writeIntoFile();
		LOG.info("============ stop......................");
	}

	private void validate(SfData data) throws Exception {
		// 认证数据
		if ("".equals(data.getAUTH_CODE()) && !"".equals(data.getAUTH_TYPE())
				|| !"".equals(data.getAUTH_CODE()) && "".equals(data.getAUTH_TYPE())) {
			data.setAUTH_CODE("");
			data.setAUTH_TYPE("");
		}

		// 过滤CERTIFICATE
		if (!(("".equals(data.getCERTIFICATE_CODE()) && "".equals(data.getCERTIFICATE_TYPE()))
				|| (!"".equals(data.getCERTIFICATE_CODE()) && !"".equals(data.getCERTIFICATE_TYPE())))) {
			data.setCERTIFICATE_CODE("");
			data.setCERTIFICATE_TYPE("");
		}

		// 过滤CERTIFICATE
		if (!(("".equals(data.getID_TYPE()) && "".equals(data.getACCOUNT()))
				|| (!"".equals(data.getID_TYPE()) && !"".equals(data.getACCOUNT())))) {
			data.setID_TYPE("");
			data.setACCOUNT("");
		}

		// 校验长度
		if (data.getACCOUNT().length() > 64 || data.getCERTIFICATE_CODE().length() > 32
				|| data.getAUTH_CODE().length() > 64 || data.getLAST_PLACE().length() > 256) {
			throw new Exception("data length error.");
		}
	}

	/**
	 * 将未正常解析的文件写入文件目录，该方法为同步方法
	 */
	private synchronized void writeIntoFile() {
		this.concurrentDatas.drainTo(currentStrings, bufferSize);
		String dateDir = dir + "/dest/";
		new File(dateDir).mkdir();// 创建目录
		String filePath = dateDir + Dates.toString(new Date(), "yyyyMMdd-HH:mm") + ".dsv";

		int len = currentStrings.size();
		if (len == 0) {
			return;
		}

		Files.write(new File(filePath), this.currentStrings);
		LOG.info("finish write the not parse data into file, size:{}, all counts:{}", len,
				allErrorCounts.addAndGet(len));

		this.currentStrings.clear();
	}

	private SfData parseLine(String line) throws Exception {
		StringTokenizer tokenizer = new StringTokenizer(line, SPLITER, true);
		int index = 0;
		String[] current = new String[30];

		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (!token.equals(SPLITER)) {
				token = token.trim();
				token = token.replace(" ", "");
				current[index] = token;

				if (tokenizer.hasMoreTokens())
					tokenizer.nextToken();
			} else {
				current[index] = "";
			}

			index++;
		}

		for (int i = 0; i < JSON_SIZE; i++) {
			if (current[i] == null) {
				current[i] = "";
			}
		}

		SfData data = new SfData(current[0], current[1], current[2], current[3], current[4], current[5], current[6],
				current[7], current[8], current[9], current[11]);

		try {
			// 时间转换与判断
			data.setLAST_TIME(Integer.valueOf(current[10]));
		} catch (Exception e) {
			data.setLAST_TIME((int) (new Date().getTime() / 1000));
		}

		try {
			// 校验 data
			validate(data);

			// 处理 mac 地址
			data.setMAC(SfDataTransferUtil.dealMac(data.getMAC()));

			return data.map(concurrentDatas, line);
		} catch (Exception e) {
			throw new Exception("validate data error..");
		}
	}

	public void stop() {
		this.running = false;
	}

}
