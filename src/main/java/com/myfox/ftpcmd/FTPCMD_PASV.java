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
		if(session.serverDataSocket!=null&&session.serverDataSocket.isOpen())
		{
			session.serverDataSocket.close();
			LOGGER.debug("pasv 关闭之前的端口监听!!");
		}
		ServerSocketChannel serverScoket = ServerSocketChannel.open();
		// 随机一个本地端口
		final InetSocketAddress isa2 = new InetSocketAddress("0.0.0.0", 0);
		serverScoket.bind(isa2);
		serverScoket.configureBlocking(false);
		SelectionKey selectionKey = session.getProcess().register(serverScoket, SelectionKey.OP_ACCEPT);
		session.clientDataServerSocket = serverScoket;
		FTPDataAcceptHandler handler = new FTPDataAcceptHandler(session);
		selectionKey.attach(handler);
		session.setDataAcceptHandler(handler);
		// session.setDataAcceptPort(port);
		// 通知后端Server进入PASV模式,命令成功后，再告知前端
		session.getP2sHandler().answerSocket("PASV " + FtpProxyChannelConfig.CRLF);
		LOGGER.debug("P->S :PASV ");
		 LOGGER.debug("ftp proxy data channel listener on port:{}", ((InetSocketAddress) serverScoket.getLocalAddress()).getPort());
	}

}
