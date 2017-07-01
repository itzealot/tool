package com.sky.projects.tool.zookeeper.support;

import com.sky.projects.tool.zookeeper.ZkPath;
import com.sky.projects.tool.zookeeper.ZkRoot;

/**
 * 根节点 ZkRoot
 *
 * @author zealot
 *
 */
public final class ZkRootImpl extends ZkPathImpl implements ZkRoot {

	ZkRootImpl(ZkConnectionImpl connection, String path, byte[] data) {
		super(connection, path, data);
	}

	@Override
	public ZkPath getParent() {
		return null;
	}

	@Override
	public String getParentPath() {
		return null;
	}

	@Override
	public boolean isRoot() {
		return true;
	}
}
