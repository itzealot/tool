package com.sky.projects.tool.zookeeper.support;

import com.sky.projects.tool.zookeeper.ZkBackground;
import com.sky.projects.tool.zookeeper.ZkConnection;
import com.sky.projects.tool.zookeeper.ZkRoot;
import com.sky.projects.tool.zookeeper.ZkTransaction;

public abstract class AbstractZkConnection implements ZkConnection, ZkBackground, ZkTransaction {
	// 是否进行事务
	protected boolean transaction = false;
	// 是否后台运行
	protected boolean background = false;
	protected ZkRoot root = null;

	AbstractZkConnection() {
	}

	@Override
	public void beginTransaction() {
		transaction = true;
	}

	@Override
	public boolean inTransaction() {
		return transaction;
	}

	@Override
	public void commitTransaction() {
		this.transaction = false;
	}

	@Override
	public void setBackground(boolean backgroundMode) {
		this.background = backgroundMode;
	}

	@Override
	public boolean getBackground() {
		return background;
	}

}
