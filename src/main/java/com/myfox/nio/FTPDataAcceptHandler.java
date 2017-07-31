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
public class FTPDataAcceptHandler implements NIOAcceptHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPCmdAcceptHandler.class);
	private FTPSession session;

	public FTPDataAcceptHandler(FTPSession session) {
		this.session = session;
	}

	@Override
	public void onAccept(SelectionKey key) throws IOException {
		ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = serverSocket.accept();
		socketChannel.configureBlocking(false);
		NIOProcessor process = session.getProcess();
		SelectionKey selectKey = process.register(socketChannel, SelectionKey.OP_READ);
//		FTPDataTransNIOHandler dataTransHandler = session.getDataChannelHandler();
		selectKey.attach(session.proxyTransDataHandler);
		if (session.isActiveModel()) {
//			dataTransHandler.setServerChannel(socketChannel);
//			dataTransHandler.setServerKey(selectKey);
			LOGGER.debug("active model accept ftpserver connecion:{}", socketChannel.getLocalAddress());
		} else {
//			dataTransHandler.setClientChannel(socketChannel);
//			dataTransHandler.setClientkey(selectKey);
			session.clientDataSocket=socketChannel;
			LOGGER.debug("passive model accept ftpclient connecion:{}", socketChannel.getLocalAddress());
		}
	}
    
}
