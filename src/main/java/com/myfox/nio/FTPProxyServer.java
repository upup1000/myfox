package com.myfox.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.nioprocess.NIOProcessGroup;
import com.myfox.nioprocess.NIOProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPProxyServer {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPProxyServer.class);
	protected NIOProcessGroup group;
	protected ServerSocketChannel serverChannel = null;
	protected SelectionKey selectKey;
	protected FtpProxyChannelConfig config;
    
	public FTPProxyServer(NIOProcessGroup group, FtpProxyChannelConfig config) {
		this.group = group;
		this.config = config;
	}

	public void startUp() throws IOException {
		NIOProcessor process = group.next();
		serverChannel = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress(config.getServerPort());
		serverChannel.socket().bind(isa);
		serverChannel.configureBlocking(false);
		selectKey = process.register(serverChannel, SelectionKey.OP_ACCEPT);
		selectKey.attach(new FTPCmdAcceptHandler(group, config));
		LOGGER.debug("ftpProxy listen on "+config.getServerPort());
	}

	public ServerSocketChannel getChannel() {
		return serverChannel;
	}

	public void close() {
		try {
			this.getChannel().socket().close();
			selectKey.cancel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
