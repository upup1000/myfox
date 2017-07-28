package com.myfox.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myfox.ftpcmd.FTPCMDProxyHandler;
import com.myfox.ftpcmd.S2PFTPCMDEnum;

/**
 * 处理 代理服务器->ftp服务器的网络事件
 * 
 * @author zss
 */
public class FTPCmdNIOEventHandlerP2S extends AbstractFTPCommandNIOHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPCmdNIOEventHandlerP2S.class);
	public static final int ST_INIT = 0;
	public static final int ST_AUTHING = 1;
	protected int status = ST_INIT;

	public FTPCmdNIOEventHandlerP2S(SocketChannel channel, FTPSession session, SelectionKey selectKey, String cmd) {
		super(channel, selectKey);
		this.ftpSession = session;
	}

	@Override
	public void onConnected(SelectionKey key) throws IOException {
		if (channel.isConnectionPending()) {
			channel.finishConnect();
		}
		key.interestOps(SelectionKey.OP_READ);
		this.answerSocket("USER " + this.ftpSession.getUname() + CRLF);
		LOGGER.debug("P->S:{}", "USER " + this.ftpSession.getUname() + CRLF);
		status = ST_AUTHING;
	}

	@Override
	public void handFtpCmd(String fromServer) throws IOException {
		if (status == ST_INIT) {
			this.answerSocket("USER " + this.ftpSession.getUname() + CRLF);
			status = ST_AUTHING;
			return;
		}
		String res = fromServer.substring(0, 3);
		int response = 0;
		try {
			response = Integer.parseInt(res);
		} catch (NumberFormatException e) {
			LOGGER.debug("S->C:{}", fromServer);
			ftpSession.getC2pHandler().answerSocket(fromServer + CRLF);
			return;
		}
		FTPCMDProxyHandler handler = S2PFTPCMDEnum.getCmdHandler(response + "");
		if (handler != null) {
			handler.exec(ftpSession, fromServer);
			LOGGER.debug("S->P:{}", fromServer);
		} else {
			ftpSession.getC2pHandler().answerSocket(fromServer + CRLF);
			LOGGER.debug("S->C:{}", fromServer);
		}
	}
}
