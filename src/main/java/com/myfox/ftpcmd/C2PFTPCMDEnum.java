package com.myfox.ftpcmd;
/**
 * 客户端向代理服务器发送的
 * @author zss
 */
public enum C2PFTPCMDEnum {
	C_P_USER("USER", new FTPCMD_USER()),
	C_P_FEAT("FEAT", new FTPCMD_FEAT()),
	C_P_EPRT("EPRT", new FTPCMD_EPRT()),
	C_P_EPSV("EPSV", new FTPCMD_EPSV());
	String key;
	public FTPCMDProxyHandler cmd;

	C2PFTPCMDEnum(String key, FTPCMDProxyHandler cmd) {
		this.cmd = cmd;
		this.key = key;
	}

	public static FTPCMDProxyHandler getCmdHandler(String key) {
		for (C2PFTPCMDEnum e : C2PFTPCMDEnum.values()) {
			if (e.key.equals(key)) {
				return e.cmd;
			}
		}
		return null;
	}
}
