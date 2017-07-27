package com.myfox.ftpcmd;

import java.io.IOException;

import com.myfox.nio.FTPSession;
/**
 * @author zss
 */
public interface FTPCMDProxyHandler {
	public void exec(FTPSession session,String cmd) throws IOException;
}
