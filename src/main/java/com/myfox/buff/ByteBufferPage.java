package com.myfox.buff;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class ByteBufferPage {
	private final ByteBuffer buf;
	private final int chunkSize;
	private final int chunkCount;
	private final BitSet chunkAllocteTrack;
	private AtomicBoolean allocLockStatus = new AtomicBoolean(false);

	// private final long startAddress;
	public ByteBufferPage(ByteBuffer buf, int chunkSize) {
		this.chunkSize = chunkSize;
		chunkCount = buf.capacity() / chunkSize;
		chunkAllocteTrack = new BitSet(chunkCount);
		this.buf = buf;
	}

	public ByteBuffer allockChunk(int needChunkCount) {
//		if (!allocLockStatus.compareAndSet(false,true)) {
//			return null;
//		}
		int startChunk = -1;
		int continueCount = 0;
		try {
			for (int i = 0; i < chunkCount; i++) {
				if (chunkAllocteTrack.get(i) == false) {
					if (startChunk == -1) {
						startChunk = i;
						continueCount = 1;
						if (needChunkCount == 1) {
							break;
						}
					} else {
						if (++continueCount == needChunkCount) {
							break;
						}
					}
				} else {
					startChunk = -1;
					continueCount = 0;
				}
			}
			if (continueCount == needChunkCount) {
				int offsetStart = startChunk * chunkSize;
				int offsetEnd = offsetStart + needChunkCount * chunkSize;
				buf.limit(offsetEnd);
				buf.position(offsetStart);
				ByteBuffer newBuf = buf.slice();
				makeChunksUsed(startChunk, needChunkCount);
				return newBuf;
			} else {
				return null;
			}
		} finally {
//			allocLockStatus.set(false);
		}
	}

	public boolean recycleBuffer(ByteBuffer parent, int startChunk, int theChunkCount) {
		if (parent == this.buf) {
			try {
				makeChunksUnUsed(startChunk, theChunkCount);
			} finally {
//				this.allocLockStatus.set(false);
			}
			return true;
		}
		return false;
	}

	private void makeChunksUsed(int startChunk, int theChunkCount) {
		chunkAllocteTrack.set(startChunk, startChunk + theChunkCount);
	}

	private void makeChunksUnUsed(int startChunk, int theChunkCount) {
		chunkAllocteTrack.clear(startChunk, startChunk + theChunkCount);
	}
	public int size() {
		return chunkAllocteTrack.cardinality() * chunkSize;
	}
	public static void main(String[] args) {
		 AtomicBoolean allocLockStatus = new AtomicBoolean(false);
		 allocLockStatus.compareAndSet(false, true);
		 System.out.println(allocLockStatus.get());
	}
}
