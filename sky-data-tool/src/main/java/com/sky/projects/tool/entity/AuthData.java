package com.sky.projects.tool.entity;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * 
 * @author zt
 *
 */
@JsonAutoDetect(JsonMethod.FIELD)
public class AuthData implements Serializable {
	private static final long serialVersionUID = 6372465966560342119L;

	public static final int JSON_SIZE = 11;
	// 分隔符
	public static final String SPLITER = "\t";

	@JsonProperty("MAC")
	private String MAC;

	@JsonProperty("IP")
	private String IP;

	@JsonProperty("CERT_TYPE")
	private String CERT_TYPE;

	@JsonProperty("CERT_CODE")
	private String CERT_CODE;

	@JsonProperty("USER_NAME")
	private String USER_NAME;

	@JsonProperty("NATION_NUM")
	private String NATION_NUM;

	@JsonProperty("COUNTRY")
	private String COUNTRY;

	@JsonProperty("WORK_COMPANY")
	private String WORK_COMPANY;

	@JsonProperty("AUTH_TYPE")
	private String AUTH_TYPE;

	@JsonProperty("AUTH_CODE")
	private String AUTH_CODE;

	@JsonProperty("AUTH_TIME")
	private Long AUTH_TIME;

	public AuthData() {
		super();
	}

	public AuthData(String mAC, String iP, String cERT_TYPE, String cERT_CODE, String uSER_NAME, String nATION_NUM,
			String cOUNTRY, String wORK_COMPANY, String aUTH_TYPE, String aUTH_CODE, Long aUTH_TIME) {
		super();
		MAC = mAC;
		IP = iP;
		CERT_TYPE = cERT_TYPE;
		CERT_CODE = cERT_CODE;
		USER_NAME = uSER_NAME;
		NATION_NUM = nATION_NUM;
		COUNTRY = cOUNTRY;
		WORK_COMPANY = wORK_COMPANY;
		AUTH_TYPE = aUTH_TYPE;
		AUTH_CODE = aUTH_CODE;
		AUTH_TIME = aUTH_TIME;
	}

	public String getMAC() {
		return MAC;
	}

	public void setMAC(String mAC) {
		MAC = mAC;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getCERT_TYPE() {
		return CERT_TYPE;
	}

	public void setCERT_TYPE(String cERT_TYPE) {
		CERT_TYPE = cERT_TYPE;
	}

	public String getCERT_CODE() {
		return CERT_CODE;
	}

	public void setCERT_CODE(String cERT_CODE) {
		CERT_CODE = cERT_CODE;
	}

	public String getUSER_NAME() {
		return USER_NAME;
	}

	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}

	public String getNATION_NUM() {
		return NATION_NUM;
	}

	public void setNATION_NUM(String nATION_NUM) {
		NATION_NUM = nATION_NUM;
	}

	public String getCOUNTRY() {
		return COUNTRY;
	}

	public void setCOUNTRY(String cOUNTRY) {
		COUNTRY = cOUNTRY;
	}

	public String getWORK_COMPANY() {
		return WORK_COMPANY;
	}

	public void setWORK_COMPANY(String wORK_COMPANY) {
		WORK_COMPANY = wORK_COMPANY;
	}

	public String getAUTH_TYPE() {
		return AUTH_TYPE;
	}

	public void setAUTH_TYPE(String aUTH_TYPE) {
		AUTH_TYPE = aUTH_TYPE;
	}

	public String getAUTH_CODE() {
		return AUTH_CODE;
	}

	public void setAUTH_CODE(String aUTH_CODE) {
		AUTH_CODE = aUTH_CODE;
	}

	public Long getAUTH_TIME() {
		return AUTH_TIME;
	}

	public void setAUTH_TIME(Long aUTH_TIME) {
		AUTH_TIME = aUTH_TIME;
	}

	@Override
	public String toString() {
		return "AuthData [MAC=" + MAC + ", IP=" + IP + ", CERT_TYPE=" + CERT_TYPE + ", CERT_CODE=" + CERT_CODE
				+ ", USER_NAME=" + USER_NAME + ", NATION_NUM=" + NATION_NUM + ", COUNTRY=" + COUNTRY + ", WORK_COMPANY="
				+ WORK_COMPANY + ", AUTH_TYPE=" + AUTH_TYPE + ", AUTH_CODE=" + AUTH_CODE + ", AUTH_TIME=" + AUTH_TIME
				+ "]";
	}

}
