package com.sky.projects.tool.zookeeper;

import java.io.Serializable;
import java.util.List;

import com.sky.projects.tool.zookeeper.ZkPath.PathFilter;

/**
 * 负责维护并持有 Zeokeeper 会话(连接)
 * 
 * @author zt
 *
 */
public interface ZkConnection extends AutoCloseable {

	/**
	 * 创建根目录"/"对象(可以创建多个)，只是创建根目录对象，目录并没有创建。使用 {@link #mkdir(ZkPath)} 创建目录
	 * 
	 * @return 返回创建后的根目录
	 */
	public ZkRoot createRoot();

	/**
	 * 创建根目录对象(可以创建多个)，只是创建根目录对象，目录并没有创建。使用 {@link #mkdir(ZkPath)} 创建目录
	 * 
	 * @param path
	 *            root's path
	 * @return 返回创建后的根目录
	 */
	public ZkRoot createRoot(String path);

	/**
	 * 创建根目录对象(可以创建多个)，只是创建根目录对象，目录并没有创建。使用 {@link #mkdir(ZkPath)} 创建目录
	 * 
	 * @param path
	 *            root's path
	 * @param data
	 *            root's data
	 * @return 返回创建后的根目录
	 */
	public ZkRoot createRoot(String path, byte[] data);

	/**
	 * 目录是否存在
	 * 
	 * @param path
	 * @return 存在返回 true； 否则返回 false
	 */
	public boolean exists(ZkPath path);

	/**
	 * 目录是否存在
	 * 
	 * @param path
	 * @return 存在返回 true； 否则返回 false
	 */
	public boolean exists(String path);

	/**
	 * 创建单个目录，只支持单级目录创建，多级目录创建使用 {@link #mkdirs(ZkPath)}
	 * 
	 * @param path
	 * @return 创建成功返回 true； 否则返回 false
	 */
	public boolean mkdir(ZkPath path);

	/**
	 * 创建单个目录，只支持单级目录创建，多级目录创建使用 {@link #mkdirs(String)}
	 * 
	 * @param path
	 * @return 创建成功返回 true； 否则返回 false
	 */
	public boolean mkdir(String path);

	/**
	 * 创建单个目录，只支持单级目录创建，多级目录创建使用 {@link #mkdirs(String, byte[])}
	 * 
	 * @param path
	 * @return 创建成功返回 true； 否则返回 false
	 */
	public boolean mkdir(String path, byte[] data);

	/**
	 * 创建多级目录
	 * 
	 * @param path
	 * @return 创建成功返回 true； 否则返回 false
	 */
	public boolean mkdirs(ZkPath path);

	/**
	 * 创建多级目录
	 * 
	 * @param path
	 * @return 创建成功返回 true； 否则返回 false
	 */
	public boolean mkdirs(String path);

	/**
	 * 创建多级目录
	 * 
	 * @param path
	 * @return 创建成功返回 true； 否则返回 false
	 */
	public boolean mkdirs(String path, byte[] data);

	/**
	 * 删除单个目录，有子目录则删除失败
	 * 
	 * @param path
	 * @return 删除成功返回 true； 否则返回 false
	 */
	public boolean delete(ZkPath path);

	/**
	 * 删除单个目录，有子目录则删除失败
	 * 
	 * @param path
	 * @return 删除成功返回 true； 否则返回 false
	 */
	public boolean delete(String path);

	/**
	 * 递归删除目录
	 * 
	 * @param path
	 * @return 删除成功返回 true； 否则返回 false
	 */
	public boolean deleteRecursive(ZkPath path);

	/**
	 * 递归删除目录
	 * 
	 * @param path
	 * @return 删除成功返回 true； 否则返回 false
	 */
	public boolean deleteRecursive(String path);

	/**
	 * 获取所有子目录对象
	 * 
	 * @param path
	 * @return
	 */
	public List<ZkPath> children(ZkPath path);

	/**
	 * 获取所有子目录对象
	 * 
	 * @param path
	 * @return
	 */
	public List<ZkPath> children(String path);

	/**
	 * 根据 PathFilter过滤子目录对象，返回 过滤后的子目录对象
	 * 
	 * @param path
	 * @param filter
	 * @return
	 */
	public List<ZkPath> children(ZkPath path, PathFilter filter);

	/**
	 * 根据 PathFilter过滤子目录对象，返回 过滤后的子目录对象
	 * 
	 * @param path
	 * @param filter
	 * @return
	 */
	public List<ZkPath> children(String path, PathFilter filter);

	/**
	 * 获取所有子目录名称
	 * 
	 * @param path
	 * @return
	 */
	public List<String> childrenNames(ZkPath path);

	/**
	 * 获取所有子目录名称
	 * 
	 * @param path
	 * @return
	 */
	public List<String> childrenNames(String path);

	/**
	 * 根据 PathFilter过滤子目录名称，返回 过滤后的子目录名称
	 * 
	 * @param path
	 * @param filter
	 * @return
	 */
	public List<String> childrenNames(ZkPath path, PathFilter filter);

	/**
	 * 根据 PathFilter过滤子目录名称，返回 过滤后的子目录名称
	 * 
	 * @param path
	 * @param filter
	 * @return
	 */
	public List<String> childrenNames(String path, PathFilter filter);

	/**
	 * 根据目录对象从Zookeeper 加载数据并保存到到目录对象中，并返回加载的数据
	 * 
	 * @param path
	 * @return 返回加载的数据；加载失败返回 null
	 */
	public byte[] loadData(ZkPath path);

	/**
	 * 根据目录对象从Zookeeper 加载数据并返回
	 * 
	 * @param path
	 * @return 返回加载的数据；加载失败返回 null
	 */
	public byte[] loadData(String path);

	/**
	 * 根据目录对象从Zookeeper 加载数据保存到到目录对象中，并返回Json数据
	 * 
	 * @param path
	 * @return 返回加载的数据；加载失败返回 null
	 */
	public <T> T loadJson(ZkPath path, Class<?> clazz);

	/**
	 * 根据目录对象从Zookeeper 加载Json数据并返回
	 * 
	 * @param path
	 * @return 返回加载的数据；加载失败返回 null
	 */
	public <T> T loadJson(String path, Class<?> clazz);

	/**
	 * 根据目录对象从Zookeeper 加载数据保存到到目录对象中，并返回序列化数据
	 * 
	 * @param path
	 * @return 返回加载的数据；加载失败返回 null
	 */
	public <T extends Serializable> T loadSerializeData(ZkPath path);

	/**
	 * 根据目录对象从Zookeeper 加载序列化数据并返回
	 * 
	 * @param path
	 * @return 返回加载的数据；加载失败返回 null
	 */
	public <T extends Serializable> T loadSerializeData(String path);

	/**
	 * 根据当前目录对象持久化数据到 Zookeeper
	 * 
	 * @param path
	 * @return 返回
	 */
	public void persist(ZkPath path);

	/**
	 * 根据当前目录对象持久化数据到 Zookeeper
	 * 
	 * @param path
	 * @param data
	 */
	public void persist(String path, byte[] data);

	/**
	 * 根据当前目录对象持久化数据到 Zookeeper
	 * 
	 * @param path
	 * @return 返回
	 */
	public void persist(String path, String json);
}
