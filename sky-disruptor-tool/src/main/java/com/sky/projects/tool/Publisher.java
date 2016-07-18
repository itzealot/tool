package com.sky.projects.tool;

/**
 * 发布事件接口
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface Publisher<T> {

	/**
	 * 根据数据源发布事件
	 * 
	 * @param source
	 */
	public void publish(T source);

}
