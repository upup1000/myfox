package com.myfox.ftpcmd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.nio.FTPDataAcceptHandler;
import com.myfox.nio.FTPSession;

public class FTPCMD_PASV extends FTPCMDLoggedPorxyHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPCMD_PASV.class);

	@Override
	public void nextExec(FTPSession session, String cmd) throws IOException {
		// 被动模式
		session.setActiveModel(false);
		int port;
		if (session.getDataAcceptHandler() == null) {
			ServerSocketChannel serverScoket = ServerSocketChannel.open();
			// 随机一个本地端口
			final InetSocketAddress isa2 = new InetSocketAddress("0.0.0.0", 0);
			serverScoket.bind(isa2);
			serverScoket.configureBlocking(false);
			SelectionKey selectionKey = session.getProcess().register(serverScoket, SelectionKey.OP_ACCEPT);
			FTPDataAcceptHandler handler = new FTPDataAcceptHandler(session, serverScoket, selectionKey);
			selectionKey.attach(handler);
			session.setDataAcceptHandler(handler);
			port = ((InetSocketAddress) serverScoket.getLocalAddress()).getPort();
			session.setDataAcceptPort(port);
		} else {
			port = session.getDataAcceptPort();
		}
		// 通知后端Server进入PASV模式,命令成功后，再告知前端
		session.getP2sHandler().answerSocket("PASV " + FtpProxyChannelConfig.CRLF);
		LOGGER.debug("P->C :PASV ");
		LOGGER.debug("ftp proxy data channel port:{}", port);
	}

}
