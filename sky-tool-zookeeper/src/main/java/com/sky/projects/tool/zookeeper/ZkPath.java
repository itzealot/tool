package com.sky.projects.tool.zookeeper;

import java.io.Serializable;
import java.util.List;

/**
 * Zookeeper 目录
 *
 * @author zt
 *
 */
public interface ZkPath {

	@FunctionalInterface
	public interface PathFilter {
		boolean accpet(String path);
	}

	@FunctionalInterface
	public interface ZkWatch {
		void accpet(ZkEventType type, ZkPath path);
	}

	public enum ZkEventType {
		None, NodeCreated, NodeDeleted, NodeDataChanged, NodeChildrenChanged;
	}

	public enum ZkACL {
		OPEN_ACL_UNSAFE, CREATOR_ALL_ACL, READ_ACL_UNSAFE;
	}

	/**
	 * 获取目录名称
	 *
	 * @return
	 */
	public String getName();

	/**
	 * 判断是否根目录
	 * 
	 * @return
	 */
	public boolean isRoot();

	/**
	 * 获取上级目录对象
	 * 
	 * @return
	 */
	public ZkPath getParent();

	/**
	 * 获取上级目录的路径
	 * 
	 * @return
	 */
	public String getParentPath();

	/**
	 * 获取当前目录的路径
	 * 
	 * @return
	 */
	public String getPath();

	/**
	 * 检查当前目录是否存在
	 * 
	 * @return
	 */
	public boolean exists();

	/**
	 * 删除当前目录
	 * 
	 * @return
	 */
	public boolean delete();

	/**
	 * 递归删除当前目录及子目录
	 * 
	 * @return
	 */
	public boolean deleteRecursive();

	/**
	 * 获取当前目录下的所有子目录
	 * 
	 * @return
	 */
	public List<ZkPath> children();

	/**
	 * 按照目录名称过滤，返回过滤之后的所有子目录
	 * 
	 * @param filter
	 * @return
	 */
	public List<ZkPath> children(PathFilter filter);

	/**
	 * 创建目录
	 *
	 * @return 创建成功则true，否则false
	 */
	public boolean mkdir();

	/**
	 * 创建目录的同时创建父目录
	 *
	 * @return 创建成功则true，否则false
	 */
	public boolean mkdirs();

	/**
	 * 在当前目录下创建子目录对象，只是创建对象
	 *
	 * @param path
	 * @return
	 */
	public ZkPath create(String path);

	/**
	 * 基于当前的 ZkPath 对象创建一个带数据的目录节点
	 *
	 * @param path
	 * @param data
	 * @return
	 */
	public ZkPath create(String path, byte[] data);

	/**
	 * 基于当前的 ZkPath 对象，如果isEphemeral 为 true 则创建临时目录节点；否则创建永久节点
	 *
	 * @param path
	 * @param isEphemeral
	 *            是否是临时的节点
	 * @return
	 */
	public ZkPath create(String path, boolean isEphemeral);

	/**
	 * 基于当前的 ZkPath 对象， 如果 isEphemeral 为 true 则创建带数据的临时目录节点；否则创建永久目录节点
	 *
	 * @param path
	 * @param isEphemeral
	 * @param data
	 * @return
	 */
	public ZkPath create(String path, boolean isEphemeral, byte[] data);

	public ZkPath create(String path, boolean isEphemeral, byte[] data, ZkACL acl);

	/**
	 * 创建是否为持久化的带序列号节点
	 * 
	 * @param isEphemeral
	 *            为true则创建持久化的带序列号节点；否则创建带序列号的非持久化节点
	 * @return
	 */
	public ZkPath createSequential(boolean isEphemeral);

	public ZkPath createSequential(boolean isEphemeral, byte[] data);

	public ZkPath createSequential(String path, boolean isEphemeral, byte[] data, ZkACL acl);

	/**
	 * 获取数据
	 *
	 * @param data
	 */
	public byte[] getData();

	/**
	 * 设置数据
	 *
	 * @param data
	 */
	public void setData(byte[] data);

	/**
	 * 从 Zookeeper 中加载数据
	 *
	 * @param data
	 */
	public byte[] load();

	/**
	 * 从 Zookeeper 中加载数据
	 *
	 * @param data
	 */
	public <T extends Serializable> T loadSerializeData();

	/**
	 * 从 Zookeeper 中加载数据
	 * 
	 * @param clazz
	 * @return
	 */
	public String loadJson(Class<?> clazz);

	/**
	 * 持久化当前对象到 Zookeeper
	 *
	 * @return
	 */
	public void persist();

	/**
	 * 持久化数据到 Zookeeper
	 *
	 * @return
	 */
	public void persist(byte[] data);

	/**
	 * 持久化json数据到 Zookeeper
	 *
	 * @return
	 */
	public void persist(String json);

}
