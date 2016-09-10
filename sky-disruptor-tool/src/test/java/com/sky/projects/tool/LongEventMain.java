package com.sky.projects.tool;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.lmax.disruptor.dsl.Disruptor;
import com.sky.projects.tool.factory.LongEventFactory;
import com.sky.projects.tool.handler.LongEventHandler;
import com.sky.projects.tool.message.LongEvent;
import com.sky.projects.tool.producer.LongEventProducer;

public class LongEventMain {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException {
		// Executor that will be used to construct new threads for consumers
		Executor executor = Executors.newCachedThreadPool();

		// The factory for the event
		LongEventFactory factory = new LongEventFactory();
		// Specify the size of the ring buffer, must be power of 2.
		int bufferSize = 1024;

		// Construct the Disruptor
		@SuppressWarnings("deprecation")
		Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(factory, bufferSize, executor);

		// Connect the handler
		disruptor.handleEventsWith(new LongEventHandler());

		// Start the Disruptor, starts all threads running
		disruptor.start();

		// Get the ring buffer from the Disruptor to be used for publishing.
		Publisher<ByteBuffer> producer = new LongEventProducer(disruptor.getRingBuffer());

		ByteBuffer buffer = ByteBuffer.allocate(8);
		for (long l = 0; true; l++) {
			buffer.putLong(0, l);
			producer.publish(buffer);
			Thread.sleep(1000);
		}
	}
}