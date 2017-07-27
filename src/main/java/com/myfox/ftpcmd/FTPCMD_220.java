package com.myfox.ftpcmd;

import java.io.IOException;

import com.myfox.nio.FTPSession;
/**
 * S->P 接受 ftp服务器器返回的欢迎消息
 * @author zss
 *
 */
public class FTPCMD_220 implements FTPCMDProxyHandler{
	@Override
	public void exec(FTPSession session, String cmd) throws IOException {
		
	}

}
