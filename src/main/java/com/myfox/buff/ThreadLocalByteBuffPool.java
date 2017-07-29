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
	
	public static ThreadLocal<DirectByteBuffPool> bigDatabyteBuffPool = new ThreadLocal<DirectByteBuffPool>() {
		public DirectByteBuffPool initialValue() {
			return new DirectByteBuffPool(2048,2048*2, (short) 10);
		}
	};
}
