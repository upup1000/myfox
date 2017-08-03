package com.myfox.ftpcmd;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.config.FtpProxyConfigLoad;
import com.myfox.config.MsgText;
import com.myfox.nio.FTPDataAcceptHandler;
import com.myfox.nio.FTPDataNIOEventHandler;
import com.myfox.nio.FTPSession;
import com.myfox.util.FTPUtil;

import static com.myfox.nio.NIOEventHandler.CRLF;

public class FTPCMD_PORT extends FTPCMDLoggedPorxyHandler {

	@Override
	public void nextExec(FTPSession session, String cmd) throws IOException {
		if(!session.isLogin()){
			session.getC2pHandler().answerSocket(MsgText.msgNotLoggedIn+CRLF);
			return;
		}else{
			try {
				int port = FTPUtil.parsePort(cmd);
				InetSocketAddress address = new InetSocketAddress(session.getClientIp()
						,port);
				SocketChannel clintDataSocketChannel = SocketChannel.open();
				clintDataSocketChannel.configureBlocking(false);
				session.getC2pHandler().answerSocket(MsgText.msgPortSuccess + CRLF);
				setupServerConnection(session,clintDataSocketChannel);
			}catch (Exception e){
				session.getC2pHandler().answerSocket(MsgText.msgPortFailed+CRLF);
			}
		}
	}
	private void setupServerConnection(FTPSession session,SocketChannel clintDataSocketChannel) throws  Exception {
		ServerSocketChannel serverScoket = ServerSocketChannel.open();
		// 随机一个本地端口
		final InetSocketAddress isa2 = new InetSocketAddress("0.0.0.0", 0);
		serverScoket.bind(isa2);
		if(serverScoket!=null) {
			serverScoket.configureBlocking(false);
			SelectionKey selectionKey = session.getProcess().register(serverScoket, SelectionKey.OP_ACCEPT);
			session.clientDataServerSocket = serverScoket;
			FTPDataAcceptHandler handler = new FTPDataAcceptHandler(session);
			selectionKey.attach(handler);
			session.setDataAcceptHandler(handler);
			session.getP2sHandler().handFtpCmd("PORT " + isa2.getAddress().getHostAddress().replace(".", ",") + ',' + (int) (isa2.getPort() / 256) + ',' + (isa2.getPort() % 256));
			//client与server端开始数据传输
			FTPDataNIOEventHandler handler1 = new FTPDataNIOEventHandler(session);
			SelectionKey dataSelectionKey = session.getProcess().register(clintDataSocketChannel,SelectionKey.OP_ACCEPT | SelectionKey.OP_CONNECT);
			dataSelectionKey.attach(handler1);
			session.setProxyTransDataHandler(handler1);
		}else{
			session.getC2pHandler().answerSocket(MsgText.msgPortFailed+CRLF);
		}
	}


}
