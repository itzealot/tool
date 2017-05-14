package com.sky.projects.tool;

import java.io.Serializable;

/**
 * 发布事件接口
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface Publisher<T> extends Serializable {

	/**
	 * 根据数据源发布事件
	 * 
	 * @param source
	 */
	public void publish(T source);

}
