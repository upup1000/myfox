import java.io.IOException;
import java.util.Collection;

import com.myfox.config.FtpProxyChannelConfig;
import com.myfox.config.FtpProxyConfigLoad;
import com.myfox.nio.FTPProxyServer;
import com.myfox.nioprocess.NIOProcessGroup;
/**
 * @author zss
 *
 */
public class Main {

	public static void main(String[] args) throws IOException {
		
		Collection<FtpProxyChannelConfig> ftpProxyServers = FtpProxyConfigLoad.readConfiguration();
		NIOProcessGroup group=new NIOProcessGroup(1);
		group.start();
		for (FtpProxyChannelConfig config : ftpProxyServers) {
			FTPProxyServer chan=new FTPProxyServer(group,config);
			chan.startUp();
		}
	}
}
