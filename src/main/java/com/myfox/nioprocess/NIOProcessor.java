package com.myfox.nioprocess;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * 连接处理器
 * @author zss
 */
public interface NIOProcessor {
	/**
	 * 是否关闭 
	 * @return
	 */
	public boolean isShutDown();
	/**
	 * 关闭
	 * @return
	 */
	public boolean shutDown();
	
	/**
	 * 注册
	 * @param channel
	 */
	public SelectionKey register(SelectableChannel channel, int option);
	
	public Selector getSelector();
}
