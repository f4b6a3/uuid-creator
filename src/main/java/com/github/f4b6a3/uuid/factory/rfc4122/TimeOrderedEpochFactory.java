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
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntFunction;
import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.PrefixCombFactory;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

/**
 * Concrete factory for creating Unix epoch time-ordered unique identifiers
 * (UUIDv7).
 * <p>
 * UUIDv7 is a new UUID version proposed by Peabody and Davis. It is similar to
 * Prefix COMB GUID and ULID.
 * <p>
 * This factory creates 3 types:
 * <p>
 * <ul>
 * <li><b>Type 1 (default)</b>: this type is divided in 3 components, namely
 * time, counter and random. The counter component is incremented by 1 when the
 * time repeats. The random component is always randomized.
 * <li><b>Type 2 (plus 1)</b>: this type is divided in 2 components, namely time
 * and monotonic random. The monotonic random component is incremented by 1 when
 * the time repeats. This type of UUID is like a Monotonic ULID. It can be much
 * faster than the other types.
 * <li><b>Type 3 (plus n)</b>: this type is also divided in 2 components, namely
 * time and monotonic random. The monotonic random component is incremented by a
 * random positive integer between 1 and MAX when the time repeats. If the value
 * of MAX is not specified, MAX is 2^32. This type of UUID is also like a
 * Monotonic ULID.
 * </ul>
 * <p>
 * <b>Warning:</b> this can change in the future.
 * 
 * @since 5.0.0
 * @see PrefixCombFactory
 * @see <a href="https://github.com/ulid/spec">ULID Specification</a>
 * @see <a href=
 *      "https://tools.ietf.org/html/draft-peabody-dispatch-new-uuid-format">New
 *      UUID formats</a>
 * @see <a href="https://datatracker.ietf.org/wg/uuidrev/documents/">Revise
 *      Universally Unique Identifier Definitions (uuidrev)</a>
 */
public final class TimeOrderedEpochFactory extends AbstCombFactory {

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
	
	
	
	
	
	
	
	// Test `ReentrantLock` vs `synchronized`
	// See: https://github.com/f4b6a3/uuid-creator/issues/92
	private ReentrantLock lock = new ReentrantLock();
	
	
	
	
	
	
	
	
	

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

	public TimeOrderedEpochFactory(LongSupplier randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	public TimeOrderedEpochFactory(IntFunction<byte[]> randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	public TimeOrderedEpochFactory(LongSupplier randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	public TimeOrderedEpochFactory(IntFunction<byte[]> randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	private TimeOrderedEpochFactory(Builder builder) {
		super(UuidVersion.VERSION_TIME_ORDERED_EPOCH, builder);
		this.incrementType = builder.getIncrementType();
		this.incrementSupplier = builder.getIncrementSupplier();

		// initialize internal state
		this.lastUuid = make(clock.millis(), random.nextLong(), random.nextLong());
	}

	/**
	 * Concrete builder for creating a Unix epoch time-ordered factory.
	 *
	 * @see AbstCombFactory.Builder
	 */
	public static class Builder extends AbstCombFactory.Builder<TimeOrderedEpochFactory, Builder> {

		private Integer incrementType;
		private Long incrementMax;

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

		protected int getIncrementType() {
			if (this.incrementType == null) {
				this.incrementType = INCREMENT_TYPE_DEFAULT;
			}
			return this.incrementType;
		}

		protected LongSupplier getIncrementSupplier() {
			switch (getIncrementType()) {
			case INCREMENT_TYPE_PLUS_1:
				// add 1 to rand_b
				return () -> 1L;
			case INCREMENT_TYPE_PLUS_N:
				if (incrementMax == null) {
					if (random instanceof ByteRandom) {
						return () -> {
							// add n to rand_b, where 1 <= n <= 2^32
							final byte[] bytes = this.random.nextBytes(4);
							return ByteUtil.toNumber(bytes, 0, 4) + 1;
						};
					} else {
						return () -> {
							// add n to rand_b, where 1 <= n <= 2^32
							return (this.random.nextLong() >>> 32) + 1;
						};
					}
				} else {
					final long positive = 0x7fffffffffffffffL;
					if (random instanceof ByteRandom) {
						// the minimum number of bits and bytes for incrementMax
						final int bits = (int) Math.ceil(Math.log(incrementMax) / Math.log(2));
						final int size = ((bits - 1) / Byte.SIZE) + 1;
						return () -> {
							// add n to rand_b, where 1 <= n <= incrementMax
							final byte[] bytes = this.random.nextBytes(size);
							final long random = ByteUtil.toNumber(bytes, 0, size);
							return ((random & positive) % incrementMax) + 1;
						};
					} else {
						return () -> {
							// add n to rand_b, where 1 <= n <= incrementMax
							return ((this.random.nextLong() & positive) % incrementMax) + 1;
						};
					}
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

	/**
	 * Returns a builder of Unix epoch time-ordered factory.
	 * 
	 * @return a builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Returns a time-ordered unique identifier (UUIDv7).
	 * 
	 * @return a UUIDv7
	 */
	@Override
	public UUID create() {
		
		lock.lock();
		
		try {

		final long time = clock.millis();
		final long lastTime = lastUuid.getMostSignificantBits() >>> 16;

		// Check if the current time is the same as the previous time or has moved
		// backwards after a small system clock adjustment or after a leap second.
		// Drift tolerance = (previous_time - 10s) < current_time <= previous_time
		if ((time > lastTime - CLOCK_DRIFT_TOLERANCE) && (time <= lastTime)) {
			this.lastUuid = increment(this.lastUuid);
		} else {
			if (this.random instanceof ByteRandom) {
				final byte[] bytes = this.random.nextBytes(10);
				final long long1 = ByteUtil.toNumber(bytes, 0, 2);
				final long long2 = ByteUtil.toNumber(bytes, 2, 10);
				this.lastUuid = make(time, long1, long2);
			} else {
				final long long1 = this.random.nextLong();
				final long long2 = this.random.nextLong();
				this.lastUuid = make(time, long1, long2);
			}
		}

		return copy(this.lastUuid);
		
		} finally {
			lock.unlock();
		}
	}

	private UUID increment(UUID uuid) {

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
			lsb |= ByteUtil.toNumber(this.random.nextBytes(6));

		} else {
			// If the 62 bits of the monotonic random overflow,
			if (lsb == overflow) {
				msb += 1; // increment the MSB.
			}
		}

		return toUuid(msb, lsb);
	}

	private UUID make(final long time, final long long1, final long long2) {
		return toUuid((time << 16) | (long1 & 0x000000000000ffffL), long2);
	}

	private UUID copy(UUID uuid) {
		return toUuid(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}
}
