package com.myfox.ftpcmd;

import java.io.IOException;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.nio.FTPSession;
/**
 * @author zss
 */
public class FTPCMD_EPRT extends FTPCMDLoggedPorxyHandler {
	String resp = "500 not supported command EPRT" + FtpProxyChannelConfig.CRLF;

	@Override
	public void nextExec(FTPSession session, String cmd) throws IOException {
		session.getC2pHandler().answerSocket(resp);
	}


}
