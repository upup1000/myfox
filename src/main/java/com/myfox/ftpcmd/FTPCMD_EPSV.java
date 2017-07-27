package com.myfox.ftpcmd;

import java.io.IOException;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.nio.FTPSession;
/**
 * @author zss
 */
public class FTPCMD_EPSV implements FTPCMDProxyHandler{
	String resp = "500 not supported command EPSV" + FtpProxyChannelConfig.CRLF;
	@Override
	public void exec(FTPSession session, String cmd) throws IOException {
		session.getC2pHandler().answerSocket(resp);
	}

}
