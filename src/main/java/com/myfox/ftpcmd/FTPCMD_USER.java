package com.myfox.ftpcmd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.myfox.nio.FTPCmdNIOEventHandlerP2S;
import com.myfox.nio.FTPSession;

/**
 * USER 命令
 * @author zss
 */
public class FTPCMD_USER implements FTPCMDProxyHandler {
	@Override
	public void exec(FTPSession session, String cmd) throws IOException {
		String userString = cmd.substring(5);
		session.setUname(userString);
		if (session.getP2sHandler() == null) {
			// 尝试连接Remote Server
			InetSocketAddress serverAddress = new InetSocketAddress(session.getServerIp(), session.getServerPort());
			final SocketChannel remoteServerChannel = SocketChannel.open();
			remoteServerChannel.configureBlocking(false);
			remoteServerChannel.connect(serverAddress);
			SelectionKey selectKey = session.getProcess().register(remoteServerChannel, SelectionKey.OP_CONNECT);
			FTPCmdNIOEventHandlerP2S backCmdNIOHandler = new FTPCmdNIOEventHandlerP2S(remoteServerChannel, session,
					selectKey, cmd);
			session.setP2sHandler(backCmdNIOHandler);
			selectKey.attach(backCmdNIOHandler);
		}
	}
}
