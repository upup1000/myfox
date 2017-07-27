package com.myfox.ftpcmd;

import java.io.IOException;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.nio.FTPSession;
/**
 * @author zss
 */
public class FTPCMD_FEAT implements FTPCMDProxyHandler {
	String resp = "211-no-features" + FtpProxyChannelConfig.CRLF + "211 End" + FtpProxyChannelConfig.CRLF;

	@Override
	public void exec(FTPSession session, String cmd) throws IOException {
		session.getC2pHandler().answerSocket(resp);
	}
}
