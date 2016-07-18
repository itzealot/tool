package com.sky.projects.tool.message;

/**
 * 生产者传递一个long类型的值给消费者，而消费者消费这个数据的方式仅仅是把它打印出来。
 * 
 * @author zealot
 *
 */
public class LongEvent {
	private long value;

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}