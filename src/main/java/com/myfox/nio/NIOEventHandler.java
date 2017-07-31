package com.myfox.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface NIOEventHandler {
	public static String CRLF = "\r\n";
	void onConnected(SelectionKey key) throws IOException;
	void onRead(SelectionKey key) throws IOException;
	void onWrite(SelectionKey key) throws IOException;
}
