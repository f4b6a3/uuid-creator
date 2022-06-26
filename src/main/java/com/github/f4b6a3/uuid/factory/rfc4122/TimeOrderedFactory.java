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
 * Factory that creates ordered gregorian time-based UUIDs.
 * 
 * RFC-4122 version: 6.
 * 
 * IETF Draft:
 * https://tools.ietf.org/html/draft-peabody-dispatch-new-uuid-format
 * 
 */
public final class TimeOrderedFactory extends AbstTimeBasedFactory {

	public TimeOrderedFactory() {
		this(builder());
	}

	private TimeOrderedFactory(Builder builder) {
		super(UuidVersion.VERSION_TIME_ORDERED, builder);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends AbstTimeBasedFactory.Builder<TimeOrderedFactory> {
		@Override
		public TimeOrderedFactory build() {
			return new TimeOrderedFactory(this);
		}
	}

	/**
	 * Returns the timestamp bits of the UUID version 6, keeping its byte order
	 * unchanged.
	 * 
	 * @param timestamp a timestamp
	 * @return the MSB
	 */
	@Override
	protected long formatMostSignificantBits(final long timestamp) {
		return ((timestamp & 0x0ffffffffffff000L) << 4) //
				| (timestamp & 0x0000000000000fffL) //
				| 0x0000000000006000L; // apply version 6
	}
}
