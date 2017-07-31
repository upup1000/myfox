package com.myfox.ftpcmd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myfox.nio.FTPDataNIOEventHandler;
import com.myfox.nio.FTPSession;
import com.myfox.util.FTPUtil;

public class FTPCMD_227 extends FTPCMDLoggedPorxyHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPCMD_227.class);

	@Override
	public void nextExec(FTPSession session, String cmd) throws IOException {
		int serverDataPort = FTPUtil.parsePort(cmd);
		InetSocketAddress serverAddress = new InetSocketAddress(session.getServerIp(), serverDataPort);
		final SocketChannel remoteServerChannel = SocketChannel.open();
		remoteServerChannel.configureBlocking(false);
		remoteServerChannel.connect(serverAddress);
		session.serverDataSocket=remoteServerChannel;
//		FTPDataTransNIOHandler hanlder = session.getDataChannelHandler();
		SelectionKey key = session.getProcess().register(remoteServerChannel, SelectionKey.OP_CONNECT);
		key.attach(session.proxyTransDataHandler);
		LOGGER.info("ftpProxy Connecting to  ftp server for data trans" + session.getServerIp() + ":" + serverDataPort);
	}

}
