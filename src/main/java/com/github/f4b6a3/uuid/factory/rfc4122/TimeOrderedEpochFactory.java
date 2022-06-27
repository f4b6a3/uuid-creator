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
 * is like a Monotonic ULID. It can be 20x faster than the first type, but it
 * generates an easy-to-guess sequence of identifiers within the same
 * millisecond interval.
 * 
 * * Plus N:
 * 
 * The UUID is divided in 2 components: time and monotonic random. The monotonic
 * random component is incremented by a random positive integer between 1 and
 * 2^32-1 when the time repeats. This type of UUID is also like a Monotonic
 * ULID. It can be faster than the first type as it consumes less entropy.
 * Furthermore, the sequence of identifiers is impossible to guess.
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

	private static final int INCREMENT_TYPE_DEFAULT = 0; // add 2^48-1 to `rand_b`
	private static final int INCREMENT_TYPE_PLUS_1 = 1; // add 1 to `rand_b`
	private static final int INCREMENT_TYPE_PLUS_N = 2; // add n to `rand_b`, where 1 <= n <= 2^32-1

	// Used to preserve monotonicity when the system clock is
	// adjusted by NTP after a small clock drift or when the
	// system clock jumps back by 1 second due to leap second.
	protected static final int CLOCK_DRIFT_TOLERANCE = 10_000;

	// Number of random bytes to be generated
	public static final int RANDOM_BYTES = 10;

	// 0xffffffffffffffffL + 1 = 0x0000000000000000L
	private static final long INCREMENT_OVERFLOW = 0x0000000000000000L;

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
			return this;
		}

		public Builder withIncrementPlusN() {
			this.incrementType = INCREMENT_TYPE_PLUS_N;
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
				return () -> {
					// add n to rand_b, where 1 <= n <= 2^32-1
					byte[] bytes = super.randomFunction.apply(4);
					return (ByteUtil.toNumber(bytes, 0, 4) + 1) & 0x7fffffffL;
				};
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

	private UUID increment(UUID uuid) {

		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits() + incrementSupplier.getAsLong();

		// if the LSB increment overflows,
		if (lsb == INCREMENT_OVERFLOW) {
			msb += 1; // increment the MSB.
		}

		if (INCREMENT_TYPE_DEFAULT == this.incrementType) {
			// randomize lower 48 bits
			lsb &= 0xffff000000000000L; // clear before randomize
			lsb |= ByteUtil.toNumber(this.randomFunction.apply(6));
		}

		return getUuid(msb, lsb);
	}

	private UUID make(long time, byte[] random) {

		long msb = 0;
		long lsb = 0;

		msb |= time << 16;
		msb |= (long) (random[0x0] & 0xff) << 8;
		msb |= (long) (random[0x1] & 0xff);

		lsb |= (long) (random[0x2] & 0xff) << 56;
		lsb |= (long) (random[0x3] & 0xff) << 48;
		lsb |= (long) (random[0x4] & 0xff) << 40;
		lsb |= (long) (random[0x5] & 0xff) << 32;
		lsb |= (long) (random[0x6] & 0xff) << 24;
		lsb |= (long) (random[0x7] & 0xff) << 16;
		lsb |= (long) (random[0x8] & 0xff) << 8;
		lsb |= (long) (random[0x9] & 0xff);

		return getUuid(msb, lsb);
	}

	private UUID copy(UUID uuid) {
		return getUuid(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}
}
