/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.f4b6a3.uuid.random;

import java.util.Random;

/**
 * A subclass of {@link java.util.Random} that implements the Xorshift random
 * number generator.
 * 
 * https://en.wikipedia.org/wiki/Xorshift
 * 
 * Reference:
 * 
 * George Marsaglia. 2003. Xorshift RNGs. Journal of Statistical Software 8, 14
 * (2003), 1–6. https://www.jstatsoft.org/article/view/v008i14
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
	
	@Override
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