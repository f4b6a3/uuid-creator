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

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.github.f4b6a3.commons.random.Xorshift128PlusRandom;
import com.github.f4b6a3.commons.random.XorshiftRandom;
import com.github.f4b6a3.commons.util.ByteUtil;
import com.github.f4b6a3.commons.util.FingerprintUtil;
import com.github.f4b6a3.commons.util.RandomUtil;
import com.github.f4b6a3.uuid.factory.abst.NoArgumentsUuidCreator;
import com.github.f4b6a3.uuid.timestamp.TimestampStrategy;
import com.github.f4b6a3.uuid.timestamp.UnixMillisecondsTimestampStretegy;

/**
 * Factory that creates COMB UUIDs.
 * 
 * The Cost of GUIDs as Primary Keys (COMB GUID inception):
 * http://www.informit.com/articles/article.aspx?p=25862
 * 
 */
public class CombGuidCreator implements NoArgumentsUuidCreator {

	protected Random random;

	protected boolean useThreadLocal = false;

	protected TimestampStrategy timestampStrategy;

	public CombGuidCreator() {
		this.timestampStrategy = new UnixMillisecondsTimestampStretegy();
	}

	/**
	 * Return a COMB GUID.
	 */
	@Override
	public synchronized UUID create() {

		final long timestamp = this.timestampStrategy.getTimestamp();

		final long msb;
		final long lsb;

		// Get random values
		if (useThreadLocal) {
			msb = ThreadLocalRandom.current().nextLong();
			lsb = ThreadLocalRandom.current().nextLong();
		} else if (random == null) {
			final byte[] bytes = new byte[10];
			RandomUtil.get().nextBytes(bytes);
			msb = ByteUtil.toNumber(bytes, 0, 8);
			lsb = (bytes[8] << 8) | (bytes[9] & 0xff);
		} else {
			if (this.random instanceof SecureRandom) {
				final byte[] bytes = new byte[10];
				this.random.nextBytes(bytes);
				msb = ByteUtil.toNumber(bytes, 0, 8);
				lsb = (bytes[8] << 8) | (bytes[9] & 0xff);
			} else {
				msb = this.random.nextLong();
				lsb = this.random.nextLong();
			}
		}

		return new UUID(msb, (lsb << 48) | (timestamp & 0x0000ffffffffffffL));
	}

	/**
	 * Used for changing the timestamp strategy.
	 * 
	 * @param timestampStrategy a timestamp strategy
	 * @return {@link CombGuidCreator}
	 */
	public synchronized CombGuidCreator withTimestampStrategy(TimestampStrategy timestampStrategy) {
		this.timestampStrategy = timestampStrategy;
		return this;
	}

	/**
	 * Replace the default random generator, in a fluent way, to another that
	 * extends {@link Random}.
	 * 
	 * The default random generator is {@link java.security.SecureRandom}.
	 * 
	 * For other faster pseudo-random generators, see {@link XorshiftRandom} and its
	 * variations.
	 * 
	 * See {@link Random}.
	 * 
	 * @param random a random generator
	 * @return {@link CombGuidCreator}
	 */
	public synchronized CombGuidCreator withRandomGenerator(Random random) {

		this.random = random;

		// disable thread local
		this.useThreadLocal = false;

		return this;
	}

	/**
	 * Replaces the default random generator with a faster one.
	 * 
	 * The host fingerprint is used to generate a seed for the random number
	 * generator.
	 * 
	 * See {@link Xorshift128PlusRandom} and
	 * {@link FingerprintUtil#getFingerprint()}
	 * 
	 * @return {@link CombGuidCreator}
	 */
	public synchronized CombGuidCreator withFastRandomGenerator() {

		final int salt = (int) FingerprintUtil.getFingerprint();
		this.random = new Xorshift128PlusRandom(salt);

		// disable thread local
		this.useThreadLocal = false;

		return this;
	}

	/**
	 * Replaces the default random generator with ThreadLocalRandom.
	 * 
	 * See {@link java.util.concurrent.ThreadLocalRandom}
	 * 
	 * @return {@link RandomUuidCreator}
	 */
	public synchronized CombGuidCreator withThreadLocalRandomGenerator() {

		this.useThreadLocal = true;

		// remove random instance
		this.random = null;

		return this;
	}
}
