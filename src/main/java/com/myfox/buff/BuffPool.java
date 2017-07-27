package com.myfox.buff;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

public interface BuffPool {

	ByteBuffer  allocate(int size);
	void recycle(ByteBuffer buffer);
	long capacity();
	long size();
	int getSharedOptsCount();
	int getChunkSize();
	ConcurrentHashMap<Long,Long> getNetDirectMemoryUsage();
}
