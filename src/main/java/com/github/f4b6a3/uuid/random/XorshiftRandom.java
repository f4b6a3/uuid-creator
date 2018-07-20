package com.github.f4b6a3.uuid.random;

import java.util.Random;

/**
 * A subclass of {@link java.util.Random} that implements the Xorshift random number
 * generator.
 * 
 * {@link https://en.wikipedia.org/wiki/Xorshift}
 * 
 * Reference:
 * 
 * George Marsaglia. 2003. Xorshift RNGs. Journal of Statistical Software 8, 14
 * (2003), 1–6. {@link https://www.jstatsoft.org/article/view/v008i14}
 * 
 */
public class XorshiftRandom extends Random {

	private static final long serialVersionUID = 5084310156945573858L;

	private long seed = System.nanoTime();

	public XorshiftRandom() {
		long nanotime = System.nanoTime();
		this.seed = (nanotime << 32) ^ (nanotime);
	}
	
	public XorshiftRandom(long seed) {
		this.seed = seed;
	}
	
	protected int next(int bits) {
		return (int) (nextLong() >>> (64 - bits));
	}

	@Override
	public long nextLong() {
		long x = this.seed;
		x ^= (x << 13);
		x ^= (x >>> 7);
		x ^= (x << 17);
		this.seed = x;
		return x;
	}
}