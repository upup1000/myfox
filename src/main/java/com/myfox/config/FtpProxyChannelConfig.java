package com.myfox.config;

/**
 * ftp代理配置
 * @author zss
 */
public class FtpProxyChannelConfig {

	public static String CRLF = "\r\n";
	public static byte[] CRLF_BYTE = "\r\n".getBytes();
	public static int MAX_CMD_SIZE = 1024;
	public static String CHARSET = "UTF-8";
	/**
	 * ftp服务名
	 */
	private String ftpServerName;
	/**
	 * ftp代理 端口
	 */
	private int serverPort;
	/**
	 * 远程ftp服务器端口
	 */
	private int remortPort;
	/**
	 * 远程ftp服务器地址
	 */
	private String remortAddress;

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public int getRemortPort() {
		return remortPort;
	}

	public void setRemortPort(int remortPort) {
		this.remortPort = remortPort;
	}

	public String getRemortAddress() {
		return remortAddress;
	}

	public void setRemortAddress(String remortAddress) {
		this.remortAddress = remortAddress;
	}

	public String getFtpServerName() {
		return ftpServerName;
	}

	public void setFtpServerName(String ftpServerName) {
		this.ftpServerName = ftpServerName;
	}
}
