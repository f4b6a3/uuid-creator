package com.github.f4b6a3.uuid.util;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil {
	
	private RandomUtil() {
	}
	
	public static void nextBytes(byte[] bytes) {
		SecureRandomLazyHolder.INSTANCE.nextBytes(bytes);
	}
	
	public static long nextLong() {
		byte[] bytes = new byte[8];
		SecureRandomLazyHolder.INSTANCE.nextBytes(bytes);
		return ByteUtil.toNumber(bytes);
	}
	
	public static int nextInt(int max) {
		return SecureRandomLazyHolder.INSTANCE.nextInt(max);
	}
	
	public static int nextInt() {
		return SecureRandomLazyHolder.INSTANCE.nextInt();
	}
	
	public static String nextLongHexadecimal() {
		return ByteUtil.toHexadecimal(SecureRandomLazyHolder.INSTANCE.nextLong());
	}
	
	private static class SecureRandomLazyHolder {
		static final Random INSTANCE = new SecureRandom();
	}
}
