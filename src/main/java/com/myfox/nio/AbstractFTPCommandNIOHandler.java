package com.myfox.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.myfox.buff.DirectByteBuffPool;
import com.myfox.buff.ThreadLocalByteBuffPool;
import com.myfox.config.FtpProxyChannelConfig;
/**
 * @author zss
 */
public abstract class AbstractFTPCommandNIOHandler implements NIOHandler {
	protected SocketChannel channel;
	protected SelectionKey selectKey;
	protected FTPSession ftpSession;
	protected FTPCmdByteBuffer cmdByteBuff;
	protected ByteBuffer readBuff;
	protected Queue<ByteBuffer> cmds = new ConcurrentLinkedQueue<ByteBuffer>();

	public AbstractFTPCommandNIOHandler(SocketChannel channel, SelectionKey selectKey) {
		this.channel = channel;
		this.selectKey = selectKey;
		this.cmdByteBuff = new FTPCmdByteBuffer(FtpProxyChannelConfig.MAX_CMD_SIZE);
	}

	@Override
	public void onConnected(SelectionKey key) throws IOException {
		if (channel.isConnectionPending()) {
			channel.finishConnect();
		}
		key.interestOps(SelectionKey.OP_READ);
	}

	@Override
	public void onRead(SelectionKey key) throws IOException {
		int readLength = cmdByteBuff.read(channel);
		if (readLength < 0) {
			close();
		} else {
			while (true) {
				int linePos = cmdByteBuff.findBytesTwo(FtpProxyChannelConfig.CRLF_BYTE);
				if (linePos == -1) {
					break;
				} else {
					String line = cmdByteBuff.getString(0, linePos);
					cmdByteBuff.setStartPos(cmdByteBuff.getStartPos() + linePos + FtpProxyChannelConfig.CRLF_BYTE.length);
					handFtpCmd(line);
				}
			}
		}
	}

	@Override
	public void onWrite(SelectionKey key) throws IOException {
		ByteBuffer writeBuffer;
		while (!cmds.isEmpty()) {
			writeBuffer = cmds.peek();
			channel.write(writeBuffer);
			if (writeBuffer.hasRemaining()) {
				selectKey.interestOps(selectKey.interestOps() | SelectionKey.OP_WRITE);
				break;
			} else {
				cmds.poll();
				ThreadLocalByteBuffPool.byteBuffPool.get().recycle(writeBuffer);
			}
		}
		// 写完 不在监听 写事件
		selectKey.interestOps(selectKey.interestOps() & ~SelectionKey.OP_WRITE);
	}

	public void answerSocket(String msg) throws IOException {
		byte data[] = msg.getBytes(FtpProxyChannelConfig.CHARSET);
		ByteBuffer byteBuff = ThreadLocalByteBuffPool.byteBuffPool.get().allocate(data.length);
		byteBuff.put(data);
		byteBuff.flip();
		cmds.add(byteBuff);
		onWrite(selectKey);
	}

	/**
	 * 处理FTP命令
	 * 
	 * @param cmd
	 */
	public abstract void handFtpCmd(String cmd) throws IOException ;

	public void close() {
		selectKey.cancel();
	}

	public static void main(String[] args) {
		DirectByteBuffPool dirbyte = new DirectByteBuffPool(512, 512, (short) 1);
		ByteBuffer b = dirbyte.allocate(512);
		for (int i = 0; i < 512; i++) {
			b.put((byte) 1);
		}
		dirbyte.recycle(b);
		b = dirbyte.allocate(512);
	}
}
