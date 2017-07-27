package com.myfox.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.myfox.ftpcmd.FTPCMDProxyHandler;
import com.myfox.ftpcmd.S2PFTPCMDEnum;

/**
 * 处理 代理服务器->ftp服务器的网络事件
 * 
 * @author zss
 */
public class FTPCmdNIOEventHandlerP2S extends AbstractFTPCommandNIOHandler {
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
		status = ST_AUTHING;
	}

	@Override
	public void handFtpCmd(String fromServer) throws IOException {
		System.out.println("S->P:" + fromServer);
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
			ftpSession.getC2pHandler().answerSocket(fromServer + CRLF);
			return;
		}
		FTPCMDProxyHandler handler = S2PFTPCMDEnum.getCmdHandler(response + "");
		if (handler != null) {
			handler.exec(ftpSession, fromServer);
		} else {
			// proxy to client
			ftpSession.getC2pHandler().answerSocket(fromServer + CRLF);
		}
	}
}
