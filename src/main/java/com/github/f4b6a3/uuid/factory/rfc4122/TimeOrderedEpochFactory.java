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

package com.github.f4b6a3.uuid.factory.rfc4122;

import java.time.Clock;
import java.util.Random;
import java.util.UUID;
import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstCombFactory;
import com.github.f4b6a3.uuid.factory.function.RandomFunction;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

/**
 * Factory that creates Unix Epoch Time-based UUIDs.
 * 
 * A Unix Epoch Time-based UUID is a proposed UUID version that is similar to a
 * Prefix COMB GUID.
 * 
 * This library implements 3 types:
 * 
 * * Default:
 * 
 * The UUID is divided in 3 components: time, counter and random. The counter
 * component is incremented by 1 when the time repeats. The random part is
 * always randomized.
 * 
 * * Plus 1:
 * 
 * The UUID is divided in 2 components: time and monotonic random. The monotonic
 * random component is incremented by 1 when the time repeats. This type of UUID
 * is like a Monotonic ULID. It can be much faster than the other types.
 * 
 * * Plus N:
 * 
 * The UUID is divided in 2 components: time and monotonic random. The monotonic
 * random component is incremented by a random positive integer between 1 and
 * MAX when the time repeats. If the value of MAX is not specified, MAX is 2^32.
 * This type of UUID is also like a Monotonic ULID.
 * 
 * RFC-4122 version: 7 (proposed).
 * 
 * IETF Draft:
 * https://tools.ietf.org/html/draft-peabody-dispatch-new-uuid-format
 * 
 */
public final class TimeOrderedEpochFactory extends AbstCombFactory {

	private long lastTime;
	private UUID lastUuid;

	private final int incrementType;
	private final LongSupplier incrementSupplier;

	private static final int INCREMENT_TYPE_DEFAULT = 0; // add 2^48 to `rand_b`
	private static final int INCREMENT_TYPE_PLUS_1 = 1; // add 1 to `rand_b`
	private static final int INCREMENT_TYPE_PLUS_N = 2; // add n to `rand_b`, where 1 <= n <= 2^32-1

	// Used to preserve monotonicity when the system clock is
	// adjusted by NTP after a small clock drift or when the
	// system clock jumps back by 1 second due to leap second.
	protected static final int CLOCK_DRIFT_TOLERANCE = 10_000;

	// Number of random bytes to be generated
	public static final int RANDOM_BYTES = 10;

	public TimeOrderedEpochFactory() {
		this(builder());
	}

	public TimeOrderedEpochFactory(Clock clock) {
		this(builder().withClock(clock));
	}

	public TimeOrderedEpochFactory(Random random) {
		this(builder().withRandom(random));
	}

	public TimeOrderedEpochFactory(Random random, Clock clock) {
		this(builder().withRandom(random).withClock(clock));
	}

	public TimeOrderedEpochFactory(RandomFunction randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	public TimeOrderedEpochFactory(RandomFunction randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	private TimeOrderedEpochFactory(Builder builder) {
		super(UuidVersion.VERSION_TIME_ORDERED_EPOCH, builder);
		this.incrementType = builder.getIncrementType();
		this.incrementSupplier = builder.getIncrementSupplier();
	}

	public static class Builder extends AbstCombFactory.Builder<TimeOrderedEpochFactory> {

		private Integer incrementType;
		private Long incrementMax;

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

		public Builder withIncrementPlus1() {
			this.incrementType = INCREMENT_TYPE_PLUS_1;
			this.incrementMax = null;
			return this;
		}

		public Builder withIncrementPlusN() {
			this.incrementType = INCREMENT_TYPE_PLUS_N;
			this.incrementMax = null;
			return this;
		}

		public Builder withIncrementPlusN(long incrementMax) {
			this.incrementType = INCREMENT_TYPE_PLUS_N;
			this.incrementMax = incrementMax;
			return this;
		}

		public int getIncrementType() {
			if (this.incrementType == null) {
				this.incrementType = INCREMENT_TYPE_DEFAULT;
			}
			return this.incrementType;
		}

		public LongSupplier getIncrementSupplier() {
			switch (getIncrementType()) {
			case INCREMENT_TYPE_PLUS_1:
				// add 1 to rand_b
				return () -> 1;
			case INCREMENT_TYPE_PLUS_N:
				if (incrementMax == null) {
					return () -> {
						// add n to rand_b, where 1 <= n <= 2^32
						final byte[] bytes = super.randomFunction.apply(4);
						return ByteUtil.toNumber(bytes, 0, 4) + 1;
					};
				} else {

					// the minimum number of bits and bytes for incrementMax
					final int bits = (int) Math.ceil(Math.log(incrementMax) / Math.log(2));
					final int size = ((bits - 1) / Byte.SIZE) + 1;

					return () -> {
						// add n to rand_b, where 1 <= n <= incrementMax
						final byte[] bytes = super.randomFunction.apply(size);
						final long random = ByteUtil.toNumber(bytes, 0, size);
						return (random % incrementMax) + 1;
					};
				}
			case INCREMENT_TYPE_DEFAULT:
			default:
				// add 2^48 to rand_b
				return () -> (1L << 48);
			}
		}

		@Override
		public TimeOrderedEpochFactory build() {
			return new TimeOrderedEpochFactory(this);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public synchronized UUID create() {

		// get the current time
		long time = clock.millis();

		// Check if the current time is the same as the previous time or has moved
		// backwards after a small system clock adjustment or after a leap second.
		// Drift tolerance = (previous_time - 10s) < current_time <= previous_time
		if ((time > this.lastTime - CLOCK_DRIFT_TOLERANCE) && (time <= this.lastTime)) {
			this.lastUuid = increment(this.lastUuid);
		} else {
			this.lastTime = time;
			this.lastUuid = make(time, this.randomFunction.apply(RANDOM_BYTES));
		}

		return copy(this.lastUuid);
	}

	private synchronized UUID increment(UUID uuid) {

		// Used to check if an overflow occurred.
		final long overflow = 0x0000000000000000L;

		// Used to propagate increments through bits.
		final long versionMask = 0x000000000000f000L;
		final long variantMask = 0xc000000000000000L;

		long msb = (uuid.getMostSignificantBits() | versionMask);
		long lsb = (uuid.getLeastSignificantBits() | variantMask) + incrementSupplier.getAsLong();

		if (INCREMENT_TYPE_DEFAULT == this.incrementType) {

			// Used to clear the random component bits.
			final long clearMask = 0xffff000000000000L;

			// If the counter's 14 bits overflow,
			if ((lsb & clearMask) == overflow) {
				msb += 1; // increment the MSB.
			}

			// And finally, randomize the lower 48 bits of the LSB.
			lsb &= clearMask; // Clear the random before randomize.
			lsb |= ByteUtil.toNumber(this.randomFunction.apply(6));

		} else {
			// If the 62 bits of the monotonic random overflow,
			if (lsb == overflow) {
				msb += 1; // increment the MSB.
			}
		}

		return toUuid(msb, lsb);
	}

	private synchronized UUID make(long time, byte[] random) {

		long msb = 0;
		long lsb = 0;

		msb |= time << 16;

		msb |= (random[0x0] & 0xffL) << 8;
		msb |= (random[0x1] & 0xffL);

		lsb |= (random[0x2] & 0xffL) << 56;
		lsb |= (random[0x3] & 0xffL) << 48;
		lsb |= (random[0x4] & 0xffL) << 40;
		lsb |= (random[0x5] & 0xffL) << 32;
		lsb |= (random[0x6] & 0xffL) << 24;
		lsb |= (random[0x7] & 0xffL) << 16;
		lsb |= (random[0x8] & 0xffL) << 8;
		lsb |= (random[0x9] & 0xffL);

		return toUuid(msb, lsb);
	}

	private synchronized UUID copy(UUID uuid) {
		return toUuid(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}
}
