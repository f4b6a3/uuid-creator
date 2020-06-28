/*
 * MIT License
 * 
 * Copyright (c) 2018-2020 Fabio Lima
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

package com.github.f4b6a3.uuid.util.random;

import java.util.Random;

/**
 * [Deprecated] A subclass of {@link java.util.Random} that implements the
 * Xorshift 128 Plus random number generator.
 * 
 * @deprecated this class will be removed in the next major version.
 * 
 * https://en.wikipedia.org/wiki/Xorshift
 * 
 */
@Deprecated
public final class Xorshift128PlusRandom extends Random {

	private static final long serialVersionUID = -7271232011767476928L;

	long[] seed = new long[2];
	private static int count;

	public Xorshift128PlusRandom() {
		this((int) System.nanoTime());
	}

	/**
	 * Constructor that receives an integer as 'salt'. This value is combined with
	 * the current milliseconds and the object hash code to generate two seeds.
	 * 
	 * @param salt a number used to generate two seeds.
	 */
	public Xorshift128PlusRandom(int salt) {
		int time = (int) System.currentTimeMillis() + count++;
		int hash = this.hashCode();
		this.seed[0] = (((long) salt) << 32) | ((long) time);
		this.seed[1] = (((long) salt) << 32) | ((long) hash);
	}

	public Xorshift128PlusRandom(long[] seed) {
		this.seed = seed;
	}

	@Override
	protected int next(int bits) {
		return nextInt() >>> (32 - bits);
	}

	@Override
	public int nextInt() {
		return (int) nextLong();
	}

	@Override
	public long nextLong() {
		long x = seed[0];
		final long y = seed[1];
		seed[0] = y;
		x ^= x << 23; // a
		seed[1] = x ^ y ^ (x >>> 17) ^ (y >>> 26); // b, c
		return seed[1] + y;
	}
}