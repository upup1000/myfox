package com.myfox.ftpcmd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myfox.nio.FTPDataTransNIOHandler;
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
		FTPDataTransNIOHandler hanlder = session.getDataChannelHandler();
		hanlder.setServerChannel(remoteServerChannel);
		SelectionKey key = session.getProcess().register(remoteServerChannel, SelectionKey.OP_CONNECT);
		key.attach(hanlder);
		LOGGER.info("ftpProxy Connecting to  ftp server for data trans" + session.getServerIp() + ":" + serverDataPort);
	}

}