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
import java.util.function.IntFunction;
import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstCombFactory;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

/**
 * Concrete factory for creating Prefix COMB GUIDs.
 * <p>
 * A Prefix COMB GUID is a UUID that combines a creation time with random bits.
 * <p>
 * The creation millisecond is a 6 bytes PREFIX at the MOST significant bits.
 * <p>
 * The created UUID is a UUIDv4 for compatibility with RFC-4122.
 * 
 * @see <a href="http://www.informit.com/articles/article.aspx?p=25862">The Cost
 *      of GUIDs as Primary Keys</a>
 */
public final class PrefixCombFactory extends AbstCombFactory {

	public PrefixCombFactory() {
		this(builder());
	}

	public PrefixCombFactory(Clock clock) {
		this(builder().withClock(clock));
	}

	public PrefixCombFactory(Random random) {
		this(builder().withRandom(random));
	}

	public PrefixCombFactory(Random random, Clock clock) {
		this(builder().withRandom(random).withClock(clock));
	}

	public PrefixCombFactory(LongSupplier randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	public PrefixCombFactory(IntFunction<byte[]> randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	public PrefixCombFactory(LongSupplier randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	public PrefixCombFactory(IntFunction<byte[]> randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	private PrefixCombFactory(Builder builder) {
		super(UuidVersion.VERSION_RANDOM_BASED, builder);
	}

	public static class Builder extends AbstCombFactory.Builder<PrefixCombFactory, Builder> {
		@Override
		public PrefixCombFactory build() {
			return new PrefixCombFactory(this);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Returns a Prefix COMB GUID.
	 * 
	 * @return a UUIDv4
	 */
	@Override
	public synchronized UUID create() {

		final long time = timeFunction.getAsLong();

		if (this.random instanceof ByteRandom) {
			final byte[] bytes = this.random.nextBytes(10);
			final long long1 = ByteUtil.toNumber(bytes, 0, 2);
			final long long2 = ByteUtil.toNumber(bytes, 2, 10);
			return make(time, long1, long2);
		} else {
			final long long1 = this.random.nextLong();
			final long long2 = this.random.nextLong();
			return make(time, long1, long2);
		}
	}

	private UUID make(final long time, final long long1, final long long2) {
		return toUuid((time << 16) | (long1 & 0x000000000000ffffL), long2);
	}
}
