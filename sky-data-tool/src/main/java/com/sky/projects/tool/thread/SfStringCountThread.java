package com.sky.projects.tool.thread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.tool.client.SfDataRecordsCountCli;
import com.sky.projects.tool.entity.SfData;

public class SfStringCountThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(SfStringCountThread.class);

	private volatile boolean running = true;
	public static final String SPLITER = "\t";
	public static final int JSON_SIZE = 12;
	private List<String> lines = new ArrayList<>();
	// json size from
	private int size = 1000;
	private BlockingQueue<String> queue;
	private int sleep;

	// 关系存储 map
	private Map<String, Integer> relationMap;
	// 身份存储 map
	private Map<String, Integer> certificationMap;
	// 错误数据
	private AtomicInteger errorCounts;
	// 没有身份没有关系
	private AtomicInteger emptyAllCounts;
	// 没有关系
	private AtomicInteger emptyRelationCounts;

	private AtomicInteger saveInFileCounts;

	// 记录行数
	private AtomicInteger indexCounts;

	List<String> relations = new ArrayList<>();
	List<String> certs = new ArrayList<>();

	private String dir;

	public SfStringCountThread(String dir, Map<String, Integer> relationMap, Map<String, Integer> certificationMap,
			AtomicInteger errorCounts, AtomicInteger emptyAllCounts, AtomicInteger emptyRelationCounts,
			AtomicInteger saveInFileCounts, AtomicInteger indexCounts, BlockingQueue<String> queue, int size,
			int sleep) {
		super();
		this.dir = dir;
		this.queue = queue;
		this.size = size;
		this.sleep = sleep;
		this.relationMap = relationMap;
		this.certificationMap = certificationMap;
		this.errorCounts = errorCounts;
		this.emptyAllCounts = emptyAllCounts;
		this.emptyRelationCounts = emptyRelationCounts;
		this.saveInFileCounts = saveInFileCounts;
		this.indexCounts = indexCounts;
	}

	@Override
	public void run() {
		while (running) {
			// 阻塞队列不为空
			if (!queue.isEmpty()) {
				// 批量从 BlockingQueue 中获取数据并进行解析
				queue.drainTo(this.lines, size);
				if (this.lines.size() == 0) {
					continue;
				}

				int counts = indexCounts.getAndAdd(size);
				if (counts != 0)
					save(counts);
				LOG.info("start deal the data, current line is " + counts + ", queue size: " + queue.size());

				for (String line : lines) {
					SfData data = null;
					try {
						data = parseLine(line);
						if (data != null) {
							extract(data);
						} else {// 未正常解析保存到文件中的记录数
							saveInFileCounts.incrementAndGet();
						}
					} catch (Exception e) { // 错误数据，进行累加
						errorCounts.incrementAndGet();
					}
				}

				this.lines.clear();
			} else {
				Threads.sleep(sleep);
			}
		}

		LOG.info("============ stop......................");
	}

	private synchronized void save(int counts) {
		if (counts % 2000000 == 0) {
			SfDataRecordsCountCli.writeCounts(dir, counts, relationMap, certificationMap, errorCounts, emptyAllCounts,
					emptyRelationCounts, saveInFileCounts);
		}

		if (counts % 4000000 == 0) {
			SfDataRecordsCountCli.write(dir, counts, relationMap, certificationMap, errorCounts, emptyAllCounts,
					emptyRelationCounts, saveInFileCounts, counts);
		}
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

	public SfData parseLine(String line) throws Exception {
		StringTokenizer tokenizer = new StringTokenizer(line, SPLITER, true);
		int index = 0;
		String[] current = new String[30];

		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (!token.equals(SPLITER)) {
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
			data.setMAC(dealMac(data.getMAC()));

			data = data.map(line);

			return data;
		} catch (Exception e) {
			throw new Exception("validate data error..", e);
		}
	}

	/**
	 * 统计身份与关系
	 * 
	 * @param data
	 */
	private void extract(SfData data) {
		extractCertification(data);
		extractRelation(certs);
		certs.clear();
	}

	private List<String> extractCertification(SfData data) {
		int counts = 0;

		String mac = data.getMAC();
		if (!isBlank(mac)) {
			certs.add(mac + "|" + "");
			counts++;
		}

		String phone = data.getPHONE();
		if (!isBlank(phone)) {
			certs.add(phone + "|" + "");
			counts++;
		}

		String imsi = data.getIMSI();
		if (!isBlank(imsi)) {
			certs.add(imsi + "|" + "");
			counts++;
		}

		String imei = data.getIMEI();
		if (!isBlank(imei)) {
			certs.add(imei + "|" + "");
			counts++;
		}

		String authType = data.getAUTH_TYPE();
		String authCode = data.getAUTH_CODE();
		if (!isBlank(authType) && !isBlank(authCode)) {
			certs.add(authCode + "|" + authType);
			counts++;
		}

		String certType = data.getCERTIFICATE_TYPE();
		String certCode = data.getCERTIFICATE_CODE();
		if (!isBlank(certType) && !isBlank(certCode)) {
			certs.add(certCode + "|" + certType);
			counts++;
		}

		String idType = data.getID_TYPE();
		String idCode = data.getACCOUNT();
		if (!isBlank(idType) && !isBlank(idCode)) {
			certs.add(idCode + "|" + idType);
			counts++;
		}

		if (counts >= 2) {// kafka 验证必须有两个或两个以上身份的关系数据才为合法
			getAndPut(certificationMap, certs);
		} else { // 错误数据
			errorCounts.incrementAndGet();

			if (counts == 1) {// 只有单个身份，没有关系
				emptyRelationCounts.incrementAndGet();
			} else { // 没有身份也没有关系
				emptyAllCounts.incrementAndGet();
			}
		}

		return certs;
	}

	/**
	 * 根据身份列表抽取关系并同步保存到关系 Map中
	 * 
	 * @param certs
	 */
	private void extractRelation(List<String> certs) {
		int len = certs.size();
		for (int i = 0; i < len; i++) {
			for (int j = i + 1; j < len; j++) {
				relations.add(certs.get(i) + "|" + certs.get(j));
				LOG.info("relation : " + certs.get(i) + "|" + certs.get(j));
			}
		}
		getAndPut(relationMap, relations);
		relations.clear();
	}

	/**
	 * 同步更新 map，对相同的 key 的 value 值进行累加
	 * 
	 * @param map
	 * @param keys
	 */
	private synchronized void getAndPut(Map<String, Integer> map, List<String> keys) {
		for (String key : keys) {
			Integer value = map.get(key);
			if (value == null) {
				value = 1;
			} else {
				value++;
			}
			map.put(key, value);
		}
	}

	private boolean isBlank(String source) {
		return source == null || "".equals(source);
	}

	private String dealMac(String mac) {
		if (mac == null || "MULL".equals(mac) || "".equals(mac)) {
			return mac;
		}

		if (mac.length() == 17) {
			return mac.toUpperCase();
		}

		if (mac.length() == 12) {
			StringBuffer buffer = new StringBuffer();
			final char spiliter = '-';
			buffer.append(mac.substring(0, 2));
			buffer.append(spiliter);
			buffer.append(mac.substring(2, 4));
			buffer.append(spiliter);
			buffer.append(mac.substring(4, 6));
			buffer.append(spiliter);
			buffer.append(mac.substring(6, 8));
			buffer.append(spiliter);
			buffer.append(mac.substring(8, 10));
			buffer.append(spiliter);
			buffer.append(mac.substring(10, 12));

			return buffer.toString().toUpperCase();
		}

		return mac;
	}

	public void stop() {
		this.running = false;
	}

}
