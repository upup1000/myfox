package com.myfox.ftpcmd;

/**
 * ftp服务器向代理服务器发送的
 * @author zss
 */
public enum S2PFTPCMDEnum {
	S_P230("230", new FTPCMD_230()),
	S_P220("220", new FTPCMD_220()),
	S_P227("227", new FTPCMD_227()),
	;
	String key;
	public FTPCMDProxyHandler cmd;
	S2PFTPCMDEnum(String key, FTPCMDProxyHandler cmd) {
		this.cmd = cmd;
		this.key = key;
	}
	public static FTPCMDProxyHandler getCmdHandler(String key) {
		for (S2PFTPCMDEnum e : S2PFTPCMDEnum.values()) {
			if (e.key.equals(key)) {
				return e.cmd;
			}
		}
		return null;
	}
}
