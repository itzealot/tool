package com.sky.projects.tool.conf;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class MassConfiguration {

	private Configuration config;

	public MassConfiguration() {
		try {
			// 正式的情况下,需要在配置目录下读取配置文件
			String userPath = System.getProperty("user.dir");
			String configPath = userPath + "/conf/";
			config = new PropertiesConfiguration(configPath + "conf.properties");
			
			// for eclipse
//			config = new PropertiesConfiguration("conf.properties");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public String get(String key) {
		return this.config.getString(key);
	}

	public Integer getInt(String key) {
		return this.config.getInt(key);
	}

	public Integer getInt(String key, int defaultValue) {
		return this.config.getInt(key, defaultValue);
	}

	public String[] getArray(String key) {
		return this.config.getStringArray(key);
	}

	public String getStrings(String key) {
		String[] results = getArray(key);
		if (results == null)
			return null;

		StringBuilder stringBuilder = new StringBuilder();
		if (results != null && results.length > 0) {
			for (String value : results) {
				stringBuilder.append(value).append(",");
			}
		}
		return stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(",")).toString();
	}
}
