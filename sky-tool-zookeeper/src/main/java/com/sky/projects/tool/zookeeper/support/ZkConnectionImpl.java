package com.sky.projects.tool.zookeeper.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.sky.projects.tool.common.Serializables;
import com.sky.projects.tool.zookeeper.ZkPath;
import com.sky.projects.tool.zookeeper.ZkPath.PathFilter;
import com.sky.projects.tool.zookeeper.ZkRoot;

public final class ZkConnectionImpl extends AbstractZkConnection {
	private static final byte[] DEFAULT_DATA = new byte[0];
	private static final Logger LOG = LoggerFactory.getLogger(ZkConnectionImpl.class);

	CuratorFramework client;

	public ZkConnectionImpl(String connectionString) {
		client = CuratorFrameworkFactory.builder().connectString(connectionString).build();
		client.start();
	}

	public ZkConnectionImpl(String connectionString, String namespace) {
		client = CuratorFrameworkFactory.builder().connectString(connectionString).namespace(namespace).build();
		client.start();
	}

	@Override
	public ZkRoot createRoot() {
		return createRoot("/", null);
	}

	@Override
	public ZkRoot createRoot(String path) {
		return createRoot(path, null);
	}

	@Override
	public ZkRoot createRoot(String path, byte[] data) {
		return new ZkRootImpl(this, "/", data);
	}

	@Override
	public boolean exists(ZkPath path) {
		return exists(path.getPath());
	}

	@Override
	public boolean exists(String path) {
		try {
			return client.checkExists().forPath(path) != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean mkdir(ZkPath path) {
		return mkdir(path.getPath(), path.getData());
	}

	@Override
	public boolean mkdir(String path) {
		return mkdir(path, DEFAULT_DATA);
	}

	@Override
	public boolean mkdir(String path, byte[] data) {
		try {
			client.create().forPath(path, data == null ? DEFAULT_DATA : data);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean mkdirs(ZkPath path) {
		return mkdirs(path.getPath(), path.getData());
	}

	@Override
	public boolean mkdirs(String path) {
		return mkdirs(path, DEFAULT_DATA);
	}

	@Override
	public boolean mkdirs(String path, byte[] data) {
		try {
			client.create().creatingParentsIfNeeded().forPath(path, data == null ? DEFAULT_DATA : data);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean delete(ZkPath path) {
		return delete(path.getPath());
	}

	@Override
	public boolean delete(String path) {
		try {
			client.delete().forPath(path);
			return true;
		} catch (Exception e) {
			LOG.error("delete path error, path is: {}, {}", path, e);
			return false;
		}
	}

	@Override
	public boolean deleteRecursive(ZkPath path) {
		return deleteRecursive(path.getPath());
	}

	@Override
	public boolean deleteRecursive(String path) {
		try {
			client.delete().deletingChildrenIfNeeded().forPath(path);
			return true;
		} catch (Exception e) {
			LOG.error("delete path recursive error, path is: {}, {}", path.toString(), e);
			return false;
		}
	}

	@Override
	public List<ZkPath> children(ZkPath path) {
		return children(path.getPath());
	}

	@Override
	public List<ZkPath> children(String path) {
		List<ZkPath> paths = Lists.newArrayList();
		List<String> childrens = childrenNames(path);

		String base = path;
		base = "/".equals(base) ? "" : base;

		for (String str : childrens) {
			paths.add(new ZkPathImpl(this, base + "/" + str, DEFAULT_DATA));
		}

		return paths;
	}

	@Override
	public List<ZkPath> children(ZkPath path, PathFilter filter) {
		return children(path.getPath(), filter);
	}

	@Override
	public List<ZkPath> children(String path, PathFilter filter) {
		List<ZkPath> childrens = children(path);
		List<ZkPath> paths = Lists.newArrayList();

		for (ZkPath p : childrens) {
			if (filter.accpet(p.getPath())) {
				paths.add(p);
			}
		}

		return paths;
	}

	@Override
	public List<String> childrenNames(ZkPath path) {
		return childrenNames(path.getPath());
	}

	@Override
	public List<String> childrenNames(String path) {
		try {
			return client.getChildren().forPath(path);
		} catch (Exception e) {
			LOG.error("get children for path error, path is: {}, {}", path, e);
			return Lists.newArrayList();
		}
	}

	@Override
	public List<String> childrenNames(ZkPath path, PathFilter filter) {
		return childrenNames(path.getPath(), filter);
	}

	@Override
	public List<String> childrenNames(String path, PathFilter filter) {
		List<String> paths = childrenNames(path);
		List<String> results = Lists.newArrayList();

		for (String str : paths) {
			if (filter.accpet(str))
				results.add(str);
		}

		return paths;
	}

	@Override
	public byte[] loadData(ZkPath path) {
		try {
			byte[] bytes = client.getData().forPath(path.getPath());
			path.setData(bytes);
			return bytes;
		} catch (Exception e) {
			LOG.error("load bytes data from path error, {}, {}", path, e);
			return null;
		}
	}

	@Override
	public byte[] loadData(String path) {
		try {
			return client.getData().forPath(path);
		} catch (Exception e) {
			LOG.error("load bytes data from path error, path is: {}, {}", path, e);
			return null;
		}
	}

	@Override
	public <T> T loadJson(ZkPath path, Class<?> clazz) {
		try {
			byte[] data = client.getData().forPath(path.getPath());
			path.setData(data);

			return new Gson().fromJson(new String(data), clazz);
		} catch (Exception e) {
			LOG.error("load json data from path error, {}, {}", path, e);
			return null;
		}
	}

	@Override
	public <T> T loadJson(String path, Class<?> clazz) {
		try {
			return new Gson().fromJson(new String(client.getData().forPath(path)), clazz);
		} catch (Exception e) {
			LOG.error("load json data from path error, path is: {}, {}", path, e);
			return null;
		}
	}

	@Override
	public <T extends Serializable> T loadSerializeData(ZkPath path) {
		try {
			byte[] data = client.getData().forPath(path.getPath());
			path.setData(data);
			return Serializables.read(data);
		} catch (Exception e) {
			LOG.error("load serialize data from path error, {}, {}", path, e);
			return null;
		}
	}

	@Override
	public <T extends Serializable> T loadSerializeData(String path) {
		try {
			return Serializables.read(client.getData().forPath(path));
		} catch (Exception e) {
			LOG.error("load serialize data from path error, path is: {}, {}", path, e);
			return null;
		}
	}

	@Override
	public void persist(ZkPath path) {
		persist(path.getPath(), path.getData());
	}

	@Override
	public void persist(String path, byte[] data) {
		checkNotNull(data, "data must not be null");

		try {
			mkdirs(path);
			client.setData().forPath(path, data);
		} catch (Exception e) {
			LOG.error("persist data into path error, path is: {}, {}", path, e);
		}
	}

	@Override
	public void persist(String path, String json) {
		checkNotNull(json, "json must not be null");

		persist(path, json.getBytes());
	}

	@Override
	public void close() {
		if (client != null) {
			client.close();
		}
	}

}
