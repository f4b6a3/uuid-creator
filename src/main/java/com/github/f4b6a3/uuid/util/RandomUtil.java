package com.github.f4b6a3.uuid.util;

import java.util.Random;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;

public class RandomUtil {

	protected static final Random random = new Xorshift128PlusRandom();
	
	public static byte[] nextLongBytes() {
		return ByteUtil.toBytes(random.nextLong());
	}
	
	public static String nextLongHexadecimal() {
		return ByteUtil.toHexadecimal(random.nextLong());
	}
	
	public static long nextLong(int max) {
		return random.nextLong();
	}
	
	public static int nextInt(int max) {
		return random.nextInt(max);
	}
}
