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
 * A subclass of {@link java.util.Random} that implements the Xoroshiro 128 Plus random
 * number generator.
 * 
 * https://en.wikipedia.org/wiki/Xoroshiro128%2B
 * 
 */
public class Xoroshiro128PlusRandom extends Random {

	private static final long serialVersionUID = -7444349550311614229L;
	long[] seed = new long[2];

	public Xoroshiro128PlusRandom() {
		long nanotime = System.nanoTime();
		long hashcode = (long) this.hashCode();
		this.seed[0] = (nanotime << 32) ^ (nanotime);
		this.seed[1] = (hashcode << 32) ^ (hashcode);
	}
	
	public Xoroshiro128PlusRandom(long[] seed) {
		this.seed = seed;
	}

	@Override
	protected int next(int bits) {
		return (int) (nextLong() >>> (64 - bits));
	}

	@Override
	public long nextLong() {
		final long  s0 = seed[0];
		long s1 = seed[1];
		final long result = s0 + s1;
		
		s1 ^= s0;
		seed[0] = rotateLeft(s0, 24) ^ s1 ^ (s1 << 16); // a, b
		seed[1] = rotateLeft(s1, 37); // c

		return result;
	}
	
	private static long rotateLeft(long x, int k) {
		return (x << k) | (x >>> (64 - k));
	}
}