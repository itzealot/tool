package com.sky.projects.tool.factory;

import com.lmax.disruptor.EventFactory;
import com.sky.projects.tool.message.LongEvent;

/**
 * 声明一个 EventFactory 来实例化Event对象
 * 
 * @author zealot
 *
 */
public class LongEventFactory implements EventFactory<LongEvent> {

	@Override
	public LongEvent newInstance() {
		return new LongEvent();
	}

}