package com.github.f4b6a3.uuid.random;

import java.util.Random;

/**
 * A subclass of {@link java.util.Random} that implements a LCG Lehmer random
 * number generator proposed by Stephen Park and Keith Miller.
 * 
 * {@link https://en.wikipedia.org/wiki/Lehmer_random_number_generator}
 * 
 * Reference:
 * 
 * Stephen K. Park; Keith W. Miller (1988). "Random Number Generators: Good Ones
 * Are Hard To Find" (PDF). Communications of the ACM. 31 (10): 1192–1201.
 * {@link http://www.firstpr.com.au/dsp/rand31/p1192-park.pdf}
 * 
 * Stephen K. Park; Keith W. Miller; Paul K. Stockmeyer (1988). "Technical
 * Correspondence: Response" (PDF). Communications of the ACM. 36 (7): 108–110.
 * {@link http://www.firstpr.com.au/dsp/rand31/p105-crawford.pdf#page=4}
 */
public class LCGParkMillerRandom extends Random {

	private static final long serialVersionUID = 5084310156945573858L;

	private long seed = System.nanoTime();

	// Proposed by Park and Miller in response to Marsaglia's criticism.
	private static final int G = 48271;

	// Mersenne prime: (2^31 - 1) or 2,147,483,647
	private static final int N = 0x7fffffff;

	public LCGParkMillerRandom() {
		this(System.nanoTime());
	}
	
	public LCGParkMillerRandom(long seed) {
		// 0 < seed < N
		this.seed = seed % N;
	}

	protected int next(int bits) {
		return (int) (nextInt() >>> (32 - bits));
	}

	@Override
	public int nextInt() {
		this.seed = (this.seed * G) % N;
		return (int) this.seed;
	}
}