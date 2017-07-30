package com.myfox.nioprocess;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

import com.myfox.nio.NIOHandler;
import com.myfox.nio.NioServerHandler;
/**
 * @author zss
 */
public class NIOProcessImpl extends Thread implements NIOProcessor {
	private Selector selector;
	private long timeOutTime = 1000;
	private boolean isRuning = true;

	public NIOProcessImpl(SelectorProvider selectorProvider) {
		try {
			selector = selectorProvider.openSelector();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SelectionKey register(SelectableChannel channel, int option) {
		try {
			return channel.register(selector, option);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void run() {
		try {
			while (isRuning) {
				selector.select(timeOutTime);
				Set<SelectionKey> keys = selector.selectedKeys();
				for (SelectionKey k : keys) {
					if (!k.isValid()) {
						continue;
					}
					int readyOps = k.readyOps();
					if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
						NIOHandler channel = (NIOHandler) k.attachment();
						channel.onConnected(k);
					}
					if ((readyOps & SelectionKey.OP_WRITE) != 0) {
						NIOHandler channel = (NIOHandler) k.attachment();
						channel.onWrite(k);
					}
					if ((readyOps & SelectionKey.OP_READ) != 0) {
						NIOHandler channel = (NIOHandler) k.attachment();
						channel.onRead(k);
					}
					if ((readyOps & SelectionKey.OP_ACCEPT) != 0) {
						NioServerHandler channel = (NioServerHandler) k.attachment();
						channel.onAccept(k);
					}
				}
				keys.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isShutDown() {
		return !isRuning;
	}

	public boolean shutDown() {
		// TODO Auto-generated method stub
		return isRuning = false;
	}

	public static void main(String[] args) {
		System.out.println(Integer.toBinaryString(SelectionKey.OP_CONNECT));
		int ops = SelectionKey.OP_CONNECT;
		ops &= ~SelectionKey.OP_CONNECT;
		System.err.println(Integer.toBinaryString(ops));
	}

	@Override
	public Selector getSelector() {
		return selector;
	}

}
