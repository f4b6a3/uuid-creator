/*
 * MIT License
 * 
 * Copyright (c) 2018-2022 Fabio Lima
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

package com.github.f4b6a3.uuid.factory.nonstandard;

import java.time.Clock;
import java.util.Random;
import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstCombFactory;
import com.github.f4b6a3.uuid.factory.function.RandomFunction;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

/**
 * Factory that creates Prefix COMB GUIDs.
 * 
 * A Prefix COMB GUID is a UUID that combines a creation time with random bits.
 * 
 * The creation minute is a 2 bytes PREFIX at the MOST significant bits.
 * 
 * The prefix wraps around every ~45 days (2^16/60/24 = ~45).
 * 
 * Read: Sequential UUID Generators
 * https://www.2ndquadrant.com/en/blog/sequential-uuid-generators/
 * 
 */
public final class ShortPrefixCombFactory extends AbstCombFactory {

	// interval in milliseconds
	protected final int interval;
	protected static final int DEFAULT_INTERVAL = 60_000;

	public ShortPrefixCombFactory() {
		this(builder());
	}

	public ShortPrefixCombFactory(Clock clock) {
		this(builder().withClock(clock));
	}

	public ShortPrefixCombFactory(Random random) {
		this(builder().withRandom(random));
	}

	public ShortPrefixCombFactory(Random random, Clock clock) {
		this(builder().withRandom(random).withClock(clock));
	}

	public ShortPrefixCombFactory(RandomFunction randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	public ShortPrefixCombFactory(RandomFunction randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	private ShortPrefixCombFactory(Builder builder) {
		super(UuidVersion.VERSION_RANDOM_BASED, builder);
		this.interval = builder.getInterval();
	}

	public static class Builder extends AbstCombFactory.Builder<ShortPrefixCombFactory> {

		private Integer interval;

		protected int getInterval() {
			if (this.interval == null) {
				this.interval = DEFAULT_INTERVAL;
			}
			return this.interval;
		}

		@Override
		public Builder withClock(Clock clock) {
			return (Builder) super.withClock(clock);
		}

		@Override
		public Builder withRandom(Random random) {
			return (Builder) super.withRandom(random);
		}

		@Override
		public Builder withRandomFunction(RandomFunction randomFunction) {
			return (Builder) super.withRandomFunction(randomFunction);
		}

		public Builder withInterval(int interval) {
			this.interval = interval;
			return this;
		}

		@Override
		public ShortPrefixCombFactory build() {
			return new ShortPrefixCombFactory(this);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Returns a Prefix COMB GUID.
	 * 
	 * It combines creation time with random bits.
	 * 
	 * The creation minute is a 2 bytes PREFIX at the MOST significant bits.
	 * 
	 * The prefix wraps around every ~45 days (2^16/60/24 = ~45).
	 */
	@Override
	public UUID create() {

		// Get random values for MSB and LSB
		final byte[] bytes = this.randomFunction.apply(14);
		long msb = ByteUtil.toNumber(bytes, 8, 14);
		long lsb = ByteUtil.toNumber(bytes, 0, 8);

		// Insert the short prefix in the MSB
		final long timestamp = clock.millis() / interval;
		msb |= ((timestamp & 0x000000000000ffffL) << 48);

		// Set the version and variant bits
		return getUuid(msb, lsb);
	}
}
