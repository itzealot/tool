package com.sky.projects.tool.producer;

import java.nio.ByteBuffer;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.sky.projects.tool.message.LongEvent;

/**
 * Disruptor 3.0提供了lambda式的API
 * 
 * @version JDK 1.8
 * @author zealot
 *
 */
public class LongEventProducerWithTranslator {
	private final RingBuffer<LongEvent> ringBuffer;

	// 一个translator可以看做一个事件初始化器，publicEvent方法会调用它
	// 填充Event
	private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR = new EventTranslatorOneArg<LongEvent, ByteBuffer>() {
		@Override
		public void translateTo(LongEvent event, long sequence, ByteBuffer buffer) {
			event.setValue(buffer.getLong(0));
		}
	};

	public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void onData(ByteBuffer buffer) {
		ringBuffer.publishEvent(TRANSLATOR, buffer);
	}
}