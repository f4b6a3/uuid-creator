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

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstTimeBasedFactory;

/**
 * Concrete factory for creating time-ordered unique identifiers (UUIDv6).
 * <p>
 * UUIDv6 is a new UUID version proposed by Peabody and Davis.
 * <p>
 * <b>Warning:</b> this can change in the future.
 * 
 * @see AbstTimeBasedFactory
 * @see <a href=
 *      "https://tools.ietf.org/html/draft-peabody-dispatch-new-uuid-format">New
 *      UUID formats</a>
 * @see <a href="https://datatracker.ietf.org/wg/uuidrev/documents/">Revise
 *      Universally Unique Identifier Definitions (uuidrev)</a>
 */
public final class TimeOrderedFactory extends AbstTimeBasedFactory {

	public TimeOrderedFactory() {
		this(builder());
	}

	private TimeOrderedFactory(Builder builder) {
		super(UuidVersion.VERSION_TIME_ORDERED, builder);
	}

	/**
	 * Returns the most significant bits of the UUID.
	 * <p>
	 * It implements the algorithm for generating UUIDv6.
	 * 
	 * @param timestamp the number of 100-nanoseconds since 1970-01-01 (Unix epoch)
	 * @return the MSB
	 */
	@Override
	protected long formatMostSignificantBits(final long timestamp) {
		return ((timestamp & 0x0ffffffffffff000L) << 4) //
				| (timestamp & 0x0000000000000fffL) //
				| 0x0000000000006000L; // apply version 6
	}

	/**
	 * Returns a builder of random-ordered factory.
	 * 
	 * @return a builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Concrete builder for creating a time-ordered factory.
	 * 
	 * @see AbstTimeBasedFactory.Builder
	 */
	public static class Builder extends AbstTimeBasedFactory.Builder<TimeOrderedFactory, Builder> {
		@Override
		public TimeOrderedFactory build() {
			return new TimeOrderedFactory(this);
		}
	}
}
