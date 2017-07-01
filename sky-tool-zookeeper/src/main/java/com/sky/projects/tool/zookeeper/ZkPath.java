package com.sky.projects.tool.zookeeper;

import java.io.Serializable;
import java.util.List;

/**
 * Zookeeper 目录
 *
 * @author zealot
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
	String getName();

	/**
	 * 判断是否根目录
	 * 
	 * @return
	 */
	boolean isRoot();

	/**
	 * 获取上级目录对象
	 * 
	 * @return
	 */
	ZkPath getParent();

	/**
	 * 获取上级目录的路径
	 * 
	 * @return
	 */
	String getParentPath();

	/**
	 * 获取当前目录的路径
	 * 
	 * @return
	 */
	String getPath();

	/**
	 * 检查当前目录是否存在
	 * 
	 * @return
	 */
	boolean exists();

	/**
	 * 删除当前目录
	 * 
	 * @return
	 */
	boolean delete();

	/**
	 * 递归删除当前目录及子目录
	 * 
	 * @return
	 */
	boolean deleteRecursive();

	/**
	 * 获取当前目录下的所有子目录
	 * 
	 * @return
	 */
	List<ZkPath> children();

	/**
	 * 按照目录名称过滤，返回过滤之后的所有子目录
	 * 
	 * @param filter
	 * @return
	 */
	List<ZkPath> children(PathFilter filter);

	/**
	 * 创建目录
	 *
	 * @return 创建成功则true，否则false
	 */
	boolean mkdir();

	/**
	 * 创建目录的同时创建父目录
	 *
	 * @return 创建成功则true，否则false
	 */
	boolean mkdirs();

	/**
	 * 在当前目录下创建子目录对象，只是创建对象
	 *
	 * @param path
	 * @return
	 */
	ZkPath create(String path);

	/**
	 * 基于当前的 ZkPath 对象创建一个带数据的目录节点
	 *
	 * @param path
	 * @param data
	 * @return
	 */
	ZkPath create(String path, byte[] data);

	/**
	 * 基于当前的 ZkPath 对象，如果isEphemeral 为 true 则创建临时目录节点；否则创建永久节点
	 *
	 * @param path
	 * @param isEphemeral
	 *            是否是临时的节点
	 * @return
	 */
	ZkPath create(String path, boolean isEphemeral);

	/**
	 * 基于当前的 ZkPath 对象， 如果 isEphemeral 为 true 则创建带数据的临时目录节点；否则创建永久目录节点
	 *
	 * @param path
	 * @param isEphemeral
	 * @param data
	 * @return
	 */
	ZkPath create(String path, boolean isEphemeral, byte[] data);

	ZkPath create(String path, boolean isEphemeral, byte[] data, ZkACL acl);

	/**
	 * 创建是否为持久化的带序列号节点
	 * 
	 * @param isEphemeral
	 *            为true则创建持久化的带序列号节点；否则创建带序列号的非持久化节点
	 * @return
	 */
	ZkPath createSequential(boolean isEphemeral);

	ZkPath createSequential(boolean isEphemeral, byte[] data);

	ZkPath createSequential(String path, boolean isEphemeral, byte[] data, ZkACL acl);

	/**
	 * 获取数据
	 *
	 * @param data
	 */
	byte[] getData();

	/**
	 * 设置数据
	 *
	 * @param data
	 */
	ZkPath setData(byte[] data);

	/**
	 * 从 Zookeeper 中加载数据
	 *
	 * @param data
	 */
	byte[] load();

	/**
	 * 从 Zookeeper 中加载数据
	 *
	 * @param data
	 */
	<T extends Serializable> T loadSerializeData();

	/**
	 * 从 Zookeeper 中加载数据
	 * 
	 * @param clazz
	 * @return
	 */
	String loadJson();

	/**
	 * 持久化当前对象到 Zookeeper
	 *
	 * @return
	 */
	void persist();

	/**
	 * 持久化数据到 Zookeeper
	 *
	 * @return
	 */
	void persist(byte[] data);

	/**
	 * 持久化json数据到 Zookeeper
	 *
	 * @return
	 */
	void persist(String json);

}
