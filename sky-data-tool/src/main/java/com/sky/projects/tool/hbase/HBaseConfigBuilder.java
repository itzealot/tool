package com.sky.projects.tool.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HBaseConfigBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(HBaseConfigBuilder.class);

	public static Configuration createConfiguration(String zkUrl) {
		Configuration config = HBaseConfiguration.create();

		config.set("hbase.zookeeper.quorum", zkUrl);

		return config;
	}

	public static HBaseAdmin create(String zkUrl) {
		try {
			return new HBaseAdmin(createConfiguration(zkUrl));
		} catch (IOException e) {
			LOG.error("HBaseAdmin create by zkUrl error, zkUrl={}, {}", zkUrl, e);
			throw new RuntimeException("HBaseAdmin create by zkUrl error.");
		}
	}

	public static HBaseAdmin create(Configuration conf) {
		try {
			return new HBaseAdmin(conf);
		} catch (IOException e) {
			LOG.error("HBaseAdmin create by Configuration error", e);
			throw new RuntimeException("HBaseAdmin create by Configuration error.");
		}
	}

	private HBaseConfigBuilder() {
	}
}
