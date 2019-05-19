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