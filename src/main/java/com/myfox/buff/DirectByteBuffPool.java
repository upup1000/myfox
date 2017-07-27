package com.myfox.buff;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import sun.nio.ch.DirectBuffer;

public class DirectByteBuffPool implements BuffPool {
	public static final String LOCAL_BUF_THREAD_PREX = "$_";
	private ByteBufferPage[] allPages;
	private final int chunkSize;
	private AtomicInteger prevAllocatePage;
	private final int pageSize;
	private final short pageCount;
	private final ConcurrentHashMap<Long, Long> memoryUsage=new ConcurrentHashMap<>();

	public DirectByteBuffPool(int chunkSize, int pageSize, short pageCount) {
		allPages = new ByteBufferPage[pageCount];
		this.chunkSize = chunkSize;
		this.pageSize = pageSize;
		this.pageCount = pageCount;
		prevAllocatePage = new AtomicInteger(0);
		for (int i = 0; i < pageCount; i++) {
			allPages[i] = new ByteBufferPage(ByteBuffer.allocateDirect(pageSize), chunkSize);
		}
	}

	public ByteBuffer expandBuffer(ByteBuffer buffer) {
		int oldCapcity = buffer.capacity();
		int newCapcity = oldCapcity << 1;
		ByteBuffer newBuffer = allocate(newCapcity);
		if (newBuffer != null) {
			int newPosition = buffer.position();
			buffer.flip();
			newBuffer.put(buffer);
			newBuffer.position(newPosition);
			recycle(buffer);
			return newBuffer;
		}
		return null;
	}

	private ByteBuffer allocateBuffer(int theChunkCount, int startPage, int endPage) {
		for (int i = startPage; i < endPage; i++) {
			ByteBuffer buffer = allPages[i].allockChunk(theChunkCount);
			if (buffer != null) {
				prevAllocatePage.getAndSet(i);
				return buffer;
			}
		}
		return null;
	}

	@Override
	public ByteBuffer allocate(int size) {
		int theChunkCount = size / chunkSize + (size % chunkSize == 0 ? 0 : 1);
		ByteBuffer byteBuf = allocateBuffer(theChunkCount, 0, allPages.length);
		final long threadId = Thread.currentThread().getId();
		if (byteBuf != null) {
			if (memoryUsage.containsKey(threadId)) {
				memoryUsage.put(threadId, memoryUsage.get(threadId) + byteBuf.capacity());
			} else {
				memoryUsage.put(threadId, (long) byteBuf.capacity());
			}
		}
		if (byteBuf == null) {
			byteBuf = ByteBuffer.allocate(size);
		}
		return byteBuf;
	}

	@Override
	public void recycle(ByteBuffer buffer) {
		if (buffer == null) {
			return;
		}
		if (!buffer.isDirect()) {
			buffer.clear();
			return;
		}
		int size = buffer.capacity();
		DirectBuffer thisNavBuf = (DirectBuffer) buffer;
		int chunkCount = buffer.capacity() / chunkSize;
		DirectBuffer parentBuf = (DirectBuffer) thisNavBuf.attachment();
		int startChunk = (int) (thisNavBuf.address() - parentBuf.address()) / chunkSize;
		for (int i = 0; i < allPages.length; i++) {
			if (allPages[i].recycleBuffer((ByteBuffer) parentBuf, startChunk, chunkCount) == true) {
				break;
			}
		}
		final long threadId = Thread.currentThread().getId();
		if (memoryUsage.containsKey(threadId)) {
			memoryUsage.put(threadId, memoryUsage.get(threadId) - size);
		}
	}

	public ConcurrentHashMap<Long, Long> getNetDirectMemoryUsage() {
		return memoryUsage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public short getPageCount() {
		return pageCount;
	}

	public long capacity() {
		return (long) pageSize * pageCount;
	}

	public long size() {
		long size = 0;
		for (int i = 0; i < pageCount; i++) {
			size += allPages[i].size();
		}
		return size;
	}

	// TODO
	public int getSharedOptsCount() {
		return 0;
	}

	public ByteBufferPage[] getAllPages() {
		return allPages;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public static void main(String[] args) {
		DirectByteBuffPool pool = new DirectByteBuffPool(512, 2048, (short) 10);

		for (int i = 0; i < 1000000; i++) {
			int o = 0;
		}
		long starttime = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			ByteBuffer.allocateDirect(512);
		}
		System.out.println(System.currentTimeMillis() - starttime);
		starttime = System.currentTimeMillis();
		Random random = new Random();
		for (int i = 0; i < 1000000; i++) {
			ByteBuffer buff = pool.allocate(random.nextInt(512));
			pool.recycle(buff);
		}

		System.out.println(System.currentTimeMillis() - starttime);
	}
}
