package com.sky.projects.tool.zookeeper;

public interface ZkTransaction {
	public void beginTransaction();

	public boolean inTransaction();

	public void commitTransaction();
}
