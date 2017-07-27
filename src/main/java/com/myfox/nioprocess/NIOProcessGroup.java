package com.myfox.nioprocess;

import java.nio.channels.spi.SelectorProvider;
/**
 * @author zss
 */
public class NIOProcessGroup {

	private NIOProcessImpl[] process;

	private volatile int nextIndex;

	public NIOProcessGroup(int threadSize) {
		process = new NIOProcessImpl[threadSize];
		for (int i = 0; i < threadSize; i++) {
			process[i] = new NIOProcessImpl(SelectorProvider.provider());
		}
	}

	public void start() {
		for (int i = 0; i < process.length; i++) {
			process[i].start();
		}
	}

	public NIOProcessor next() {
		NIOProcessor nioprocess = process[nextIndex % process.length];
		nextIndex++;
		return nioprocess;
	}
}
