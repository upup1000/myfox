package com.myfox.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myfox.nioprocess.NIOProcessor;

/**
 * ftpproxy 接收客户端 或 服务器 数据通道连接 处理
 * 
 * @author zss
 */
public class FTPDataAcceptHandler implements NioServerHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPCmdAcceptHandler.class);
	private FTPSession session;
	private ServerSocketChannel serverScoket;
	private SelectionKey selectionKey;

	public FTPDataAcceptHandler(FTPSession session, ServerSocketChannel serverScoket, SelectionKey selectionKey) {
		this.serverScoket = serverScoket;
		this.session = session;
		this.selectionKey = selectionKey;
	}

	@Override
	public void onAccept(SelectionKey key) throws IOException {
		ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = serverSocket.accept();
		socketChannel.configureBlocking(false);
		NIOProcessor process = session.getProcess();
		SelectionKey selectKey = process.register(socketChannel, SelectionKey.OP_READ);
		FTPDataTransNIOHandler dataTransHandler = session.getDataChannelHandler();
		selectKey.attach(dataTransHandler);
		if (session.isActiveModel()) {
			dataTransHandler.setServerChannel(socketChannel);
			dataTransHandler.setServerKey(selectKey);
			LOGGER.debug("active model accept ftpserver connecion:{}", socketChannel.getLocalAddress());
		} else {
			dataTransHandler.setClientChannel(socketChannel);
			dataTransHandler.setClientkey(selectKey);
			LOGGER.debug("passive model accept ftpclient connecion:{}", socketChannel.getLocalAddress());
		}
	}
    
	public void close() {
		if (selectionKey != null) {
			selectionKey.cancel();
		}
		if (serverScoket.isOpen()) {
			try {
				serverScoket.close();
				LOGGER.debug("关闭数据代理通道 监听!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}