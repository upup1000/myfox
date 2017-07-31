package com.myfox.nio;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myfox.buff.DirectByteBuffPool;
import com.myfox.buff.ThreadLocalByteBuffPool;

/**
 * ftpProxy 数据通道 数据传输 处理
 * 
 * @author zss
 */
public class FTPDataNIOEventHandler implements NIOEventHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPDataNIOEventHandler.class);
	protected Queue<ByteBuffer> clientWriteBuffer = new ConcurrentLinkedQueue<ByteBuffer>();
	protected Queue<ByteBuffer> serverWriteBuffer = new ConcurrentLinkedQueue<ByteBuffer>();
	private FTPSession session;

	public FTPDataNIOEventHandler(FTPSession session) {
		this.session = session;
	}

	@Override
	public void onConnected(SelectionKey key) throws IOException {
		SocketChannel curChannel = (SocketChannel) key.channel();
		// 如果正在连接，则完成连接
		if (curChannel.isConnectionPending()) {
			curChannel.finishConnect();
		}
		// 连接成功后，注册接收服务器消息的事件
		curChannel.register(session.getProcess().getSelector(), SelectionKey.OP_READ, this);
		// 通知前端ＰＡＡＳＩＶＥ模式建立成功
		int dataServerPort = ((InetSocketAddress) session.clientDataServerSocket.getLocalAddress()).getPort();
		String toClient = "227 Entering Passive Mode (" + "127,0,0,1" + "," + (int) (dataServerPort / 256) + ","
				+ (dataServerPort % 256) + ")";
		session.getC2pHandler().answerSocket(toClient + CRLF);
		LOGGER.info("P->C:" + toClient);
		LOGGER.info("ftpProxy Connecting to  ftp server for data trans" + session.getServerIp() + "success!");
	}

	@Override
	public void onRead(SelectionKey selectionKey) throws IOException {
		if (!selectionKey.channel().isOpen() || !selectionKey.isReadable()) {
			LOGGER.info("CancelledKeyException!!!!!!!!");
			return;
		}
		SocketChannel curChannel = (SocketChannel) selectionKey.channel();
		DirectByteBuffPool buffPool = ThreadLocalByteBuffPool.bigDatabyteBuffPool.get();
		ByteBuffer byteBuff = buffPool.allocate(buffPool.getChunkSize());
		int read = curChannel.read(byteBuff);
		byteBuff.flip();
		if (read == -1) {
			closeQuietly(curChannel);
			if (curChannel == session.clientDataSocket) {
				if (this.serverWriteBuffer.isEmpty()) {
					closeQuietly(session.serverDataSocket);
				}
			} else {
				if (this.clientWriteBuffer.isEmpty()) {
					closeQuietly(session.clientDataSocket);
				}
			}
			return;
		} else {
			if (curChannel == session.clientDataSocket) {
				serverWriteBuffer.add(byteBuff);
			} else {
				clientWriteBuffer.add(byteBuff);
			}
			register(selectionKey);
		}
	}

	@Override
	public void onWrite(SelectionKey selectionKey) throws IOException {
		SocketChannel curChannel = (SocketChannel) selectionKey.channel();
		Queue<ByteBuffer> wirtebuffs;
		SocketChannel otherChannel;
		if (curChannel == session.clientDataSocket) {
			wirtebuffs = this.clientWriteBuffer;
			otherChannel = session.serverDataSocket;
		} else {
			wirtebuffs = this.serverWriteBuffer;
			otherChannel = session.clientDataSocket;
		}
		if (wirtebuffs.isEmpty()) {
			register(selectionKey);
			return;
		}
		ByteBuffer buffer = wirtebuffs.peek();
		curChannel.write(buffer);
		if (buffer.remaining() == 0) {
			ThreadLocalByteBuffPool.bigDatabyteBuffPool.get().recycle(buffer);
			wirtebuffs.poll();
			if (wirtebuffs.isEmpty()) {
				if (!otherChannel.isOpen()) {
					closeQuietly(curChannel);
					return;
				}
				register(selectionKey);
			}
		} else {
			register(selectionKey);
		}
	}

	public void close() {

	}

	private void register(SelectionKey selectionKey) throws IOException {
		int clientOps = 0 | SelectionKey.OP_READ;
		if (!this.clientWriteBuffer.isEmpty())
			clientOps |= SelectionKey.OP_WRITE;
		session.clientDataSocket.register(session.getProcess().getSelector(), clientOps, this);
		int serverOps = 0 | SelectionKey.OP_READ;
		if (!this.serverWriteBuffer.isEmpty())
			serverOps |= SelectionKey.OP_WRITE;
		session.serverDataSocket.register(session.getProcess().getSelector(), serverOps, this);
	}

	public static void closeQuietly(final Closeable closeable) {
		try {
			if (closeable != null) {
//				LOGGER.info("closeQuietly close" + closeable);
				closeable.close();
			}
		} catch (IOException e) {
		}
	}

}
