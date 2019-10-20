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
 * A subclass of {@link java.util.Random} that implements the Xorshift Star random
 * number generator.
 * 
 * https://en.wikipedia.org/wiki/Xorshift
 * 
 */
public class XorshiftStarRandom extends Random {
	
	private static final long serialVersionUID = -55477194093772899L;
	
	private long seed;
	private static int count;

	public XorshiftStarRandom() {
		this((int) System.nanoTime());
	}
	
	public XorshiftStarRandom(int salt) {
		long time = System.currentTimeMillis() + count++;
		this.seed = (((long) salt) << 32) ^ (time & 0x00000000ffffffffL);
	}

	public XorshiftStarRandom(long seed) {
		this.seed = seed;
	}

	@Override
	protected int next(int bits) {
		return (int) (nextLong() >>> (64 - bits));
	}

	@Override
	public long nextLong() {
		long x = this.seed;
		x ^= (x >>> 12);
		x ^= (x << 25);
		x ^= (x >>> 27);
		this.seed = x;
		return x * 0x2545F4914F6CDD1DL;
	}
}