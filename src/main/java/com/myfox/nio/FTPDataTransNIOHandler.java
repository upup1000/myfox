package com.myfox.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myfox.buff.DirectByteBuffPool;
import com.myfox.buff.ThreadLocalByteBuffPool;
import com.myfox.config.MsgText;

/**
 * ftpProxy 数据通道 数据传输 处理
 * 
 * @author zss
 */
public class FTPDataTransNIOHandler implements NIOHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(FTPDataTransNIOHandler.class);
	private SocketChannel clientChannel;
	private SocketChannel serverChannel;
	private SelectionKey clientkey;
	private SelectionKey serverKey;
	private FTPSession session;
	protected Queue<ByteBuffer> clientWriteBuffer = new ConcurrentLinkedQueue<ByteBuffer>();
	protected Queue<ByteBuffer> serverWriteBuffer = new ConcurrentLinkedQueue<ByteBuffer>();

	public FTPDataTransNIOHandler(FTPSession session) {
		this.session = session;
	}

	@Override
	public void onConnected(SelectionKey key) throws IOException {
		SocketChannel curChannel = (SocketChannel) key.channel();
		// 如果正在连接，则完成连接
		if (curChannel.isConnectionPending()) {
			curChannel.finishConnect();
		}
		int dataServerPort = session.getDataAcceptPort();
		if (session.isActiveModel()) {
			clientChannel = curChannel;
			clientkey = key;
			LOGGER.debug("Server DataSocket connected ");
		} else {
			serverChannel = curChannel;
			serverKey = session.getProcess().register(serverChannel,SelectionKey.OP_READ);
			serverKey.attach(this);
			LOGGER.debug("ftpProxy Connecting to  ftp server for datatrans " + curChannel.getRemoteAddress() + " success");
			int begin = (int) (dataServerPort / 256);
			int end = (int) (dataServerPort % 256);
			String toClient = java.text.MessageFormat.format(MsgText.msgPassiveMode, "192,168,1,111", begin, end);
			LOGGER.info("P->C:" + toClient);
			session.getC2pHandler().answerSocket(toClient + CRLF);
		}
	}

	@Override
	public void onRead(SelectionKey key) throws IOException {
		SocketChannel curChannel = (SocketChannel) key.channel();
		DirectByteBuffPool buffPool = ThreadLocalByteBuffPool.bigDatabyteBuffPool.get();
		ByteBuffer byteBuff = buffPool.allocate(buffPool.getChunkSize());
		System.out.println("======="+byteBuff.limit());
		int read = curChannel.read(byteBuff);
		byteBuff.flip();
		if (read == -1) {
//			close1(curChannel);
			return;
		}
		if (curChannel == this.clientChannel) {
			serverWriteBuffer.add(byteBuff);
			serverKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			LOGGER.debug("client send to server data:" + byteBuff.limit());
		} else {
			clientWriteBuffer.add(byteBuff);
			clientkey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			LOGGER.debug("sever send to client data:" + byteBuff.limit());
		}
	}

	@Override
	public void onWrite(SelectionKey key) throws IOException {
		SocketChannel curChannel = (SocketChannel) key.channel();
		if (curChannel == this.clientChannel) {
			write(curChannel, clientWriteBuffer, this.clientkey);
		} else {
			write(curChannel, serverWriteBuffer, this.serverKey);
		}
	}

	private void write(SocketChannel curChannel, Queue<ByteBuffer> buffers, SelectionKey key) throws IOException {
		if (buffers.isEmpty()) {
			key.interestOps(SelectionKey.OP_READ);
			return;
		}
		ByteBuffer wirteBuff = buffers.peek();
		curChannel.write(wirteBuff);
		int wirteSize = wirteBuff.limit();
		if (curChannel == this.clientChannel) {
			LOGGER.debug("server write to client data:" + wirteSize);
		} else {
			LOGGER.debug("client write to server data:" + wirteSize);
		}
		if (!wirteBuff.hasRemaining()) {
			ThreadLocalByteBuffPool.bigDatabyteBuffPool.get().recycle(buffers.poll());
		}
		if (!buffers.isEmpty()) {
			key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			return;
		} else {
			key.interestOps(SelectionKey.OP_READ);
			if (curChannel == this.clientChannel) {
				if (serverChannel != null && this.serverChannel.isConnected()) {
					close(curChannel, key, "关闭C->P 数据通道");
				}
			} else {
				if (clientChannel != null && this.clientChannel.isConnected()) {
					close(curChannel, key, "关闭P->S 数据通道");
				}
			}
		}
	}

	public SocketChannel getClientChannel() {
		return clientChannel;
	}

	public void setClientChannel(SocketChannel clientChannel) {
		this.clientChannel = clientChannel;
	}

	public SocketChannel getServerChannel() {
		return serverChannel;
	}

	public void setServerChannel(SocketChannel serverChannel) {
		this.serverChannel = serverChannel;
	}

	public SelectionKey getClientkey() {
		return clientkey;
	}

	public void setClientkey(SelectionKey clientkey) {
		this.clientkey = clientkey;
	}

	public SelectionKey getServerKey() {
		return serverKey;
	}

	public void setServerKey(SelectionKey serverKey) {
		this.serverKey = serverKey;
	}

	public void close1(SocketChannel curChannel) {
		if (curChannel == this.clientChannel) {
			if (this.clientWriteBuffer.isEmpty()) {
				close(clientChannel, clientkey, "关闭C->P 数据通道");
			}
		} else {
			if (this.serverWriteBuffer.isEmpty()) {
				close(serverChannel, serverKey, "关闭P->S 数据通道");
			}
		}
	}

	public void close() {
		close(clientChannel, clientkey, "关闭C->P 数据通道22222");
		close(serverChannel, serverKey, "关闭P->S 数据通道22222");
	}

	private void close(SocketChannel channel, SelectionKey key, String info) {
		try {
			if (key != null) {
				key.cancel();
			}
			if (channel != null && channel.isConnected()) {
				channel.close();
				LOGGER.debug(info);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
