package com.myfox.buff;
/**
 * @author zss
 */
public class ThreadLocalByteBuffPool {
	public static ThreadLocal<DirectByteBuffPool> byteBuffPool = new ThreadLocal<DirectByteBuffPool>() {
		public DirectByteBuffPool initialValue() {
			return new DirectByteBuffPool(512, 2048, (short) 10);
		}
	};
}
