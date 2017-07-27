package com.myfox.ftpcmd;

import java.io.IOException;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.nio.FTPSession;
/**
 * S->P 230命令 
 * @author zss
 */
public class FTPCMD_230 implements FTPCMDProxyHandler{
    String res="230 Login successful"+FtpProxyChannelConfig.CRLF;
	@Override
	public void exec(FTPSession session, String cmd) throws IOException {
		session.setLogin(true);
		session.getC2pHandler().answerSocket(res);
	}

}
