package com.github.f4b6a3.uuid.util;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil {

	private static final Random RANDOM = new SecureRandom();
	
	private RandomUtil() {
	}
	
	public static long nextLong() {
		return RANDOM.nextLong();
	}
	
	public static int nextInt(int max) {
		return RANDOM.nextInt(max);
	}
	
	public static int nextInt() {
		return RANDOM.nextInt();
	}
	
	public static String nextLongHexadecimal() {
		return ByteUtil.toHexadecimal(RANDOM.nextLong());
	}
}
