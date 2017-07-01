package com.sky.projects.tool.zookeeper;

/**
 * ZkTransaction
 * 
 * @author zealot
 *
 */
public interface ZkTransaction {

	void beginTransaction();

	boolean inTransaction();

	void commitTransaction();
}
