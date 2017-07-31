package com.myfox.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * @author zss
 */
public interface NIOAcceptHandler {
    /**
     * 新连接到来
     * @param key
     * @throws IOException
     */
	void onAccept(SelectionKey key) throws IOException;
}
