/**
 * Copyright 2018 Fabio Lima <br/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); <br/>
 * you may not use this file except in compliance with the License. <br/>
 * You may obtain a copy of the License at <br/>
 *
 * http://www.apache.org/licenses/LICENSE-2.0 <br/>
 *
 * Unless required by applicable law or agreed to in writing, software <br/>
 * distributed under the License is distributed on an "AS IS" BASIS, <br/>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br/>
 * See the License for the specific language governing permissions and <br/>
 * limitations under the License. <br/>
 *
 */

package com.github.f4b6a3.uuid.random;

import java.util.Random;

/**
 * A subclass of {@link java.util.Random} that implements the Xorshift 128 Plus random
 * number generator.
 * 
 * https://en.wikipedia.org/wiki/Xorshift
 * 
 */
public class Xorshift128PlusRandom extends Random {
	
	private static final long serialVersionUID = -7271232011767476928L;
	
	long[] seed = new long[2];

	public Xorshift128PlusRandom() {
		long nanotime = System.nanoTime();
		long hashcode = (long) this.hashCode();
		this.seed[0] = (nanotime << 32) ^ (nanotime);
		this.seed[1] = (hashcode << 32) ^ (hashcode);
	}
	
	public Xorshift128PlusRandom(long[] seed) {
		this.seed = seed;
	}

	@Override
	protected int next(int bits) {
		return (int) (nextLong() >>> (64 - bits));
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