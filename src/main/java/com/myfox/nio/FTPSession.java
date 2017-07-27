package com.myfox.nio;

import java.io.IOException;
import java.nio.channels.Channel;

import com.myfox.nioprocess.NIOProcessor;

/**
 * ftp session
 * 
 * @author zss
 */
public class FTPSession {
	private String uname;
	private String serverIp;
	private int serverPort;
	private String clientIp;
	private boolean isLogin;
	private NIOProcessor process;
	private FTPCmdNIOEventHandlerC2P c2pHandler;
	private FTPCmdNIOEventHandlerP2S p2sHandler;

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public NIOProcessor getProcess() {
		return process;
	}

	public void setProcess(NIOProcessor process) {
		this.process = process;
	}

	public FTPCmdNIOEventHandlerC2P getC2pHandler() {
		return c2pHandler;
	}

	public void setC2pHandler(FTPCmdNIOEventHandlerC2P c2pHandler) {
		this.c2pHandler = c2pHandler;
	}

	public FTPCmdNIOEventHandlerP2S getP2sHandler() {
		return p2sHandler;
	}

	public void setP2sHandler(FTPCmdNIOEventHandlerP2S p2sHandler) {
		this.p2sHandler = p2sHandler;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

}
