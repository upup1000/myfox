package com.myfox.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * ftp代理配置加载
 * 
 * @author zss
 */
public class FtpProxyConfigLoad {
	private final static String CONFIGURATION_FILE = "ftpprxy.properties";
	private final static String LOCAL_PORT_PROPERTY = ".localPort";
	private final static String REMOTE_PORT_PROPERTY = ".remotePort";
	private final static String REMOTE_HOST_PROPERTY = ".remoteHost";
	private final static String LOCAL_HOST_IP = ".localIp";
	public static Collection<FtpProxyChannelConfig> readConfiguration() throws IOException {
		final Collection<FtpProxyChannelConfig> proxyTaskSettingses = new ArrayList<FtpProxyChannelConfig>();
		final Properties properties = loadProperties();
		final Collection<String> proxyNames = collectProxyNames(properties);
		for (String proxyName : proxyNames) {
			proxyTaskSettingses.add(createProxyTask(properties, proxyName));
		}
		return proxyTaskSettingses;
	}

	private static Collection<String> collectProxyNames(final Properties prop) {
		final Set<String> propertiesName = new HashSet<String>();
		for (String name : prop.stringPropertyNames()) {
			propertiesName.add(name.substring(0, name.indexOf(".")));
		}
		return propertiesName;
	}

	private static Properties loadProperties() throws IOException {
		final Properties properties = new Properties();
		String url=FtpProxyConfigLoad.class.getClassLoader().getResource("").getPath();
		final InputStream stream =new FileInputStream(url+"/"+CONFIGURATION_FILE);
		properties.load(stream);
		return properties;
	}

	private static FtpProxyChannelConfig createProxyTask(final Properties properties, final String proxyName) {
		final String hostName = properties.getProperty(proxyName + REMOTE_HOST_PROPERTY);
		final int localPort = Integer.parseInt(properties.getProperty(proxyName + LOCAL_PORT_PROPERTY));
		final int remotePort = Integer.parseInt(properties.getProperty(proxyName + REMOTE_PORT_PROPERTY));
		String bind_address=properties.getProperty(proxyName + LOCAL_HOST_IP);
		bind_address=!bind_address.equals("0.0.0.0")?bind_address:"127.0.0.1";
		bind_address =bind_address.replace('.', ',');
		FtpProxyChannelConfig config = new FtpProxyChannelConfig();
		config.setFtpServerName(proxyName);
		config.setServerPort(localPort);
		config.setRemortAddress(hostName);
		config.setRemortPort(remotePort);
		config.setBind_address(bind_address);
		return config;
	}
	public static void main(String[] args) {
		try {
			FtpProxyConfigLoad.loadProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
