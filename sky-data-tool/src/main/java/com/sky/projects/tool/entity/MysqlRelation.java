package com.sky.projects.tool.entity;

public class MysqlRelation {
	private String idFrom;
	private String fromType;
	private String idTo;
	private String toType;

	private String firstStartTime;
	private String firstTerminalNum;
	private String source;
	private String createTime;
	private String discoverTimes;
	private String updateTime;
	private String sysSource;

	private String lastStartTime;
	private String lastTerminalNum;

	public MysqlRelation(String idFrom, String fromType, String idTo, String toType, String firstStartTime,
			String firstTerminalNum, String source, String createTime, String discoverTimes, String updateTime,
			String sysSource) {
		super();
		this.idFrom = idFrom;
		this.fromType = fromType;
		this.idTo = idTo;
		this.toType = toType;
		this.firstStartTime = firstStartTime;
		this.firstTerminalNum = firstTerminalNum;
		this.source = source;
		this.createTime = createTime;
		this.discoverTimes = discoverTimes;
		this.updateTime = updateTime;
		this.sysSource = sysSource;
	}

	public MysqlRelation() {
		super();
	}

	public String getIdFrom() {
		return idFrom;
	}

	public void setIdFrom(String idFrom) {
		this.idFrom = idFrom;
	}

	public String getFromType() {
		return fromType;
	}

	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

	public String getIdTo() {
		return idTo;
	}

	public void setIdTo(String idTo) {
		this.idTo = idTo;
	}

	public String getToType() {
		return toType;
	}

	public void setToType(String toType) {
		this.toType = toType;
	}

	public String getFirstStartTime() {
		return firstStartTime;
	}

	public void setFirstStartTime(String firstStartTime) {
		this.firstStartTime = firstStartTime;
	}

	public String getFirstTerminalNum() {
		return firstTerminalNum;
	}

	public void setFirstTerminalNum(String firstTerminalNum) {
		this.firstTerminalNum = firstTerminalNum;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getDiscoverTimes() {
		return discoverTimes;
	}

	public void setDiscoverTimes(String discoverTimes) {
		this.discoverTimes = discoverTimes;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getSysSource() {
		return sysSource;
	}

	public void setSysSource(String sysSource) {
		this.sysSource = sysSource;
	}

	public String getLastStartTime() {
		return lastStartTime;
	}

	public void setLastStartTime(String lastStartTime) {
		this.lastStartTime = lastStartTime;
	}

	public String getLastTerminalNum() {
		return lastTerminalNum;
	}

	public void setLastTerminalNum(String lastTerminalNum) {
		this.lastTerminalNum = lastTerminalNum;
	}

	@Override
	public String toString() {
		return "MysqlRelation [idFrom=" + idFrom + ", fromType=" + fromType + ", idTo=" + idTo + ", toType=" + toType
				+ ", firstStartTime=" + firstStartTime + ", firstTerminalNum=" + firstTerminalNum + ", source=" + source
				+ ", createTime=" + createTime + ", discoverTimes=" + discoverTimes + ", updateTime=" + updateTime
				+ ", sysSource=" + sysSource + ", lastStartTime=" + lastStartTime + ", lastTerminalNum="
				+ lastTerminalNum + "]";
	}

}
