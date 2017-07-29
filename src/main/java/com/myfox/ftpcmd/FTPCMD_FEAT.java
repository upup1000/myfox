package com.myfox.ftpcmd;

import java.io.IOException;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.nio.FTPSession;
/**
 * @author zss
 */
public class FTPCMD_FEAT extends FTPCMDLoggedInPorxyHandler {
	String resp = "211-no-features" + FtpProxyChannelConfig.CRLF + "211 End" + FtpProxyChannelConfig.CRLF;

	@Override
	public void nextExec(FTPSession session, String cmd) throws IOException {
		session.getC2pHandler().answerSocket(resp);
	}
}
