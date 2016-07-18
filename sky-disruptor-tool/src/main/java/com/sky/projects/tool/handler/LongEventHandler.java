package com.sky.projects.tool.handler;

import com.lmax.disruptor.EventHandler;
import com.sky.projects.tool.message.LongEvent;

/**
 * 事件消费者，也就是一个事件处理器。这个事件处理器简单地把事件中存储的数据打印到终端
 * 
 * @author zealot
 *
 */
public class LongEventHandler implements EventHandler<LongEvent> {

	@Override
	public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
		System.out.println("message: " + event.getValue());
	}
}