package com.myfox.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.nioprocess.NIOProcessGroup;
import com.myfox.nioprocess.NIOProcessor;
/**
 * 接收ftp命令通道 连接处理
 * @author zss
 */
public class FTPCmdAcceptHandler implements NIOAcceptHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPCmdAcceptHandler.class);
	private NIOProcessGroup group;
	private FtpProxyChannelConfig config;

	public FTPCmdAcceptHandler(NIOProcessGroup group, FtpProxyChannelConfig config) {
		this.group = group;
		this.config = config;
	}

	@Override
	public void onAccept(SelectionKey key) throws IOException {
		ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
		final SocketChannel socketChannel = serverSocket.accept();
		socketChannel.configureBlocking(false);
		NIOProcessor process = group.next();
		SelectionKey socketKey = process.register(socketChannel, SelectionKey.OP_READ);
		FTPCmdNIOEventHandlerC2P c2p = new FTPCmdNIOEventHandlerC2P(socketChannel, socketKey, config, process);
		socketKey.attach(c2p);
		LOGGER.debug("client connection from:{}",socketChannel.getRemoteAddress());
	}

}
