package com.sky.projects.tool.zookeeper.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;

import com.sky.projects.tool.common.Paths;
import com.sky.projects.tool.common.Serializables;
import com.sky.projects.tool.zookeeper.ZkPath;

/**
 * 路径节点 ZkPath
 *
 * @author zt
 *
 */
public class ZkPathImpl implements ZkPath {
	// 绝对路径
	protected String path;
	protected ZkConnectionImpl connection;
	private byte[] data;

	ZkPathImpl(ZkConnectionImpl connection, String path, byte[] data) {
		checkNotNull(connection, "connection can not be null");
		checkNotNull(path, "path can not be null");

		this.path = path;
		this.connection = connection;
		this.data = data;
	}

	@Override
	public String getName() {
		return Paths.dirName(path);
	}

	@Override
	public ZkPath getParent() {
		return new ZkPathImpl(connection, Paths.getParentPath(path), null);
	}

	@Override
	public String getParentPath() {
		return Paths.getParentPath(path);
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public ZkPath create(String path) {
		return create(path, null);
	}

	@Override
	public ZkPath create(String path, byte[] data) {
		return "/".equals(path) ? this : this.getChildren(path, data);
	}

	private ZkPathImpl getChildren(String path, byte[] data) {
		return new ZkPathImpl(connection, this.getPathBaseOnCurrent(path), data);
	}

	private ZkPathImpl getChildren(String path) {
		return getChildren(path, null);
	}

	/**
	 * 获取当前对象拼接 path 后的 路径
	 *
	 * @param path
	 * @return
	 */
	private String getPathBaseOnCurrent(String path) {
		checkNotNull(path, "path can not be null");
		return this.path + (path.charAt(0) != '/' ? "/" : "") + path;
	}

	public EventType eventType(ZkEventType type) {
		if (ZkEventType.None.equals(type))
			return Watcher.Event.EventType.None;
		if (ZkEventType.NodeCreated.equals(type))
			return Watcher.Event.EventType.NodeCreated;
		if (ZkEventType.NodeDeleted.equals(type))
			return Watcher.Event.EventType.NodeDeleted;
		if (ZkEventType.NodeDataChanged.equals(type))
			return Watcher.Event.EventType.NodeDataChanged;
		if (ZkEventType.NodeChildrenChanged.equals(type))
			return Watcher.Event.EventType.NodeChildrenChanged;
		throw new IllegalArgumentException();
	}

	private List<ACL> convertACL(ZkACL acl) {
		if (ZkACL.OPEN_ACL_UNSAFE.equals(acl))
			return ZooDefs.Ids.OPEN_ACL_UNSAFE;
		if (ZkACL.CREATOR_ALL_ACL.equals(acl))
			return ZooDefs.Ids.CREATOR_ALL_ACL;
		if (ZkACL.READ_ACL_UNSAFE.equals(acl))
			return ZooDefs.Ids.READ_ACL_UNSAFE;
		throw new IllegalArgumentException();
	}

	@Override
	public ZkPath create(String path, boolean isEphemeral) {
		try {
			if (isEphemeral) {
				connection.client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
						.forPath(getPathBaseOnCurrent(path));
			} else {
				connection.client.create().creatingParentsIfNeeded().forPath(getPathBaseOnCurrent(path));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.getChildren(path);
	}

	@Override
	public ZkPath create(String path, boolean isEphemeral, byte[] data) {
		try {
			if (isEphemeral) {
				connection.client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
						.forPath(getPathBaseOnCurrent(path), data);
			} else {
				connection.client.create().creatingParentsIfNeeded().forPath(getPathBaseOnCurrent(path), data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.getChildren(path);
	}

	@Override
	public ZkPath create(String path, boolean isEphemeral, byte[] data, ZkACL acl) {
		try {
			if (isEphemeral) {
				connection.client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
						.withACL(convertACL(acl)).forPath(getPathBaseOnCurrent(path), data);
			} else {
				connection.client.create().creatingParentsIfNeeded().withACL(convertACL(acl))
						.forPath(getPathBaseOnCurrent(path), data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.getChildren(path);
	}

	@Override
	public ZkPath createSequential(boolean isEphemeral) {
		return createSequential(isEphemeral, null);
	}

	@Override
	public ZkPath createSequential(boolean isEphemeral, byte[] data) {
		try {
			if (isEphemeral) {
				connection.client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
						.forPath(path);
			} else {
				connection.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL)
						.forPath(path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this;
	}

	@Override
	public ZkPath createSequential(String path, boolean isEphemeral, byte[] data, ZkACL acl) {
		try {
			if (isEphemeral) {
				connection.client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
						.withACL(convertACL(acl)).forPath(getPathBaseOnCurrent(path), Serializables.serialize(data));
			} else {
				connection.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL)
						.withACL(convertACL(acl)).forPath(getPathBaseOnCurrent(path), Serializables.serialize(data));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.getChildren(path);
	}

	@Override
	public boolean exists() {
		return connection.exists(this);
	}

	@Override
	public boolean delete() {
		return connection.delete(this);
	}

	@Override
	public boolean deleteRecursive() {
		return connection.deleteRecursive(this);
	}

	@Override
	public List<ZkPath> children() {
		return connection.children(this);
	}

	@Override
	public List<ZkPath> children(PathFilter filter) {
		return connection.children(this, filter);
	}

	@Override
	public boolean mkdir() {
		return connection.mkdir(this);
	}

	@Override
	public boolean mkdirs() {
		return connection.mkdirs(this);
	}

	@Override
	public byte[] getData() {
		return data;
	}

	@Override
	public ZkPath setData(byte[] data) {
		this.data = data;
		return this;
	}

	@Override
	public byte[] load() {
		return connection.loadData(this);
	}

	@Override
	public <T extends Serializable> T loadSerializeData() {
		return connection.loadSerializeData(this);
	}

	@Override
	public String loadJson(Class<?> clazz) {
		return connection.loadJson(this, clazz);
	}

	@Override
	public void persist() {
		this.persist(this.data);
	}

	public void persist(String json) {
		persist(json.getBytes());
	}

	public void persist(byte[] data) {
		connection.persist(this.path, data);
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	@Override
	public String toString() {
		return "path is : " + path;
	}

}
