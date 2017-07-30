package com.myfox.ftpcmd;

import java.io.IOException;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.nio.FTPSession;
/**
 *  feat命令是用来请求FTP服务器列出它的所有的扩展命令与扩展功能的。属于主动模式命令！
 * @author zss
 */
public class FTPCMD_FEAT extends FTPCMDLoggedPorxyHandler {
	String resp = "211-no-features" + FtpProxyChannelConfig.CRLF + "211 End" + FtpProxyChannelConfig.CRLF;

	@Override
	public void nextExec(FTPSession session, String cmd) throws IOException {
		session.getC2pHandler().answerSocket(resp);
	}
}
