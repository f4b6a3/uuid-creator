package com.github.f4b6a3.uuid.util;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil {

	private static final Random random = new SecureRandom();
	
	private RandomUtil() {
	}
	
	public static long nextLong() {
		return random.nextLong();
	}
	
	public static int nextInt(int max) {
		return random.nextInt(max);
	}
	
	public static int nextInt() {
		return random.nextInt();
	}
	
	public static String nextLongHexadecimal() {
		return ByteUtil.toHexadecimal(random.nextLong());
	}
}
