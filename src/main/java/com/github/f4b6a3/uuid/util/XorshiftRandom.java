package com.github.f4b6a3.uuid.util;

import java.util.Random;

/**
 * A subclass of java.util.Random that implements the Xorshift random number
 * generator.
 * 
 * Faster and better than java.util.Random. But it's not a "cryptographic
 * quality" random generator.
 * 
 * Reference:
 * 
 * George Marsaglia. 2003. Xorshift RNGs. Journal of Statistical Software 8, 14
 * (2003), 1–6. {@link https://www.jstatsoft.org/article/view/v008i14}
 * 
 * Links:
 * 
 * {@link https://en.wikipedia.org/wiki/Xorshift}
 * {@link https://www.javamex.com/tutorials/random_numbers/xorshift.shtml#.W04CEBjQ_0o}
 * {@link https://www.javamex.com/tutorials/random_numbers/java_util_random_subclassing.shtml}
 * 
 */
public class XorshiftRandom extends Random {

	private static final long serialVersionUID = 5084310156945573858L;

	private long seed = System.nanoTime();

	public XorshiftRandom() {
	}

	@Override
	protected int next(int nbits) {
		long x = this.seed;
		x ^= (x << 13);
		x ^= (x >> 17);
		x ^= (x << 5);
		this.seed = x;
		x &= ((1L << nbits) - 1);
		return (int) x;
	}

	@Override
	public long nextLong() {
		long x = this.seed;
		x ^= (x << 13);
		x ^= (x >> 7);
		x ^= (x << 17);
		this.seed = x;
		return x;
	}
}