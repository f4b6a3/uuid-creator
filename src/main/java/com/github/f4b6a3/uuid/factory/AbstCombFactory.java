/*
 * MIT License
 * 
 * Copyright (c) 2018-2024 Fabio Lima
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
import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.enums.UuidVersion;

/**
 * Abstract Factory for creating COMB GUIDs.
 * <p>
 * COMB GUIDs combine a creation time and random bytes.
 */
public abstract class AbstCombFactory extends AbstRandomBasedFactory {

	/**
	 * The time function.
	 */
	protected LongSupplier timeFunction;

	/**
	 * Constructor whith a version number and a builder.
	 * 
	 * @param version a version number
	 * @param builder a builder
	 */
	protected AbstCombFactory(UuidVersion version, Builder<?, ?> builder) {
		super(version, builder);
		this.timeFunction = builder.getTimeFunction();
	}

	/**
	 * Abstract builder for creating a COMB factory.
	 *
	 * @param <T> factory type
	 * @param <B> builder type
	 * @see AbstRandomBasedFactory.Builder
	 */
	public abstract static class Builder<T, B extends Builder<T, B>> extends AbstRandomBasedFactory.Builder<T, B> {

		/**
		 * The time function.
		 */
		protected LongSupplier timeFunction;

		/**
		 * Get the time function.
		 * 
		 * @return the builder
		 */
		protected LongSupplier getTimeFunction() {
			if (this.timeFunction == null) {
				this.timeFunction = () -> System.currentTimeMillis();
			}
			return this.timeFunction;
		}

		/**
		 * Set the clock.
		 * 
		 * @param clock a clock
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withClock(Clock clock) {
			if (clock != null) {
				this.timeFunction = () -> clock.millis();
			}
			return (B) this;
		}

		/**
		 * Set the time function.
		 * 
		 * @param timeFunction a function
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withTimeFunction(LongSupplier timeFunction) {
			this.timeFunction = timeFunction;
			return (B) this;
		}
	}
}
