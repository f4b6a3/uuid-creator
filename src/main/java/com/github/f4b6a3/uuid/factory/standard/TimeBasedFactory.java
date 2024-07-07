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

package com.github.f4b6a3.uuid.factory.standard;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstTimeBasedFactory;

/**
 * Concrete factory for creating time-based unique identifiers (UUIDv1).
 * 
 * @see AbstTimeBasedFactory
 */
public final class TimeBasedFactory extends AbstTimeBasedFactory {

	/**
	 * Default constructor.
	 */
	public TimeBasedFactory() {
		this(builder());
	}

	private TimeBasedFactory(Builder builder) {
		super(UuidVersion.VERSION_TIME_BASED, builder);
	}

	/**
	 * Returns a builder of time-based factory.
	 * 
	 * @return a builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Concrete builder for creating a time-based factory.
	 * 
	 * @see AbstTimeBasedFactory.Builder
	 */
	public static class Builder extends AbstTimeBasedFactory.Builder<TimeBasedFactory, Builder> {
		@Override
		public TimeBasedFactory build() {
			return new TimeBasedFactory(this);
		}
	}
}