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

package com.github.f4b6a3.uuid.factory;

import java.time.Clock;
import java.util.Random;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.function.RandomFunction;

/**
 * Abstract Factory for COMB UUIDs.
 * 
 * COMB UUIDs combine a creation time and random bytes.
 */
public abstract class AbstCombFactory extends AbstRandomBasedFactory {

	protected Clock clock;
	protected static final Clock DEFAULT_CLOCK = Clock.systemUTC();

	protected AbstCombFactory(UuidVersion version, Builder<?> builder) {
		super(version, builder.getRandomFunction());
		this.clock = builder.getClock();
	}

	public abstract static class Builder<T> {

		protected Clock clock;
		protected RandomFunction randomFunction;

		protected Clock getClock() {
			if (this.clock == null) {
				this.clock = DEFAULT_CLOCK;
			}
			return this.clock;
		}

		protected RandomFunction getRandomFunction() {
			if (this.randomFunction == null) {
				this.randomFunction = newRandomFunction(null);
			}
			return this.randomFunction;
		}

		public Builder<T> withClock(Clock clock) {
			this.clock = clock;
			return this;
		}

		public Builder<T> withRandom(Random random) {
			this.randomFunction = newRandomFunction(random);
			return this;
		}

		public Builder<T> withRandomFunction(RandomFunction randomFunction) {
			this.randomFunction = randomFunction;
			return this;
		}

		public abstract T build();
	}
}
