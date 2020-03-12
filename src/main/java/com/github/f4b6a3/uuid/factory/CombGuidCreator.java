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

package com.github.f4b6a3.uuid.factory;

import java.util.UUID;

/**
 * Factory that creates COMB UUIDs.
 * 
 * This implementation is derived from the ULID specification. The only
 * difference is that the millisecond bits are moved to the end of the GUID. See
 * the {@link UlidBasedGuidCreator}.
 * 
 * The Cost of GUIDs as Primary Keys (COMB GUID inception):
 * http://www.informit.com/articles/article.aspx?p=25862
 * 
 * ULID specification: https://github.com/ulid/spec
 * 
 */
public class CombGuidCreator extends UlidBasedGuidCreator {

	/**
	 * Return a COMB GUID.
	 * 
	 * See {@link UlidBasedGuidCreator#create()}
	 */
	@Override
	public synchronized UUID create() {

		final long timestamp = this.getTimestamp();

		final long randomHi = truncate(randomMsb);
		final long randomLo = truncate(randomLsb);

		final long msb = (randomHi << 24) | (randomLo >>> 16);
		final long lsb = (randomLo << 48) | timestamp;

		return new UUID(msb, lsb);
	}

	/**
	 * For unit tests
	 */
	@Override
	protected long extractRandomLsb(UUID uuid) {
		return ((uuid.getMostSignificantBits() & 0xffffffL) << 16) | (uuid.getLeastSignificantBits() >>> 48);
	}

	/**
	 * For unit tests
	 */
	@Override
	protected long extractRandomMsb(UUID uuid) {
		return (uuid.getMostSignificantBits() >>> 24);
	}
}
