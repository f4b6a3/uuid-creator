/*
 * MIT License
 * 
 * Copyright (c) 2018-2020 Fabio Lima
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

package com.github.f4b6a3.uuid.creator.nonstandard;

import java.util.UUID;

import com.github.f4b6a3.uuid.creator.AbstractRandomBasedUuidCreator;
import com.github.f4b6a3.uuid.enums.UuidVersion;
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
public final class ShortPrefixCombCreator extends AbstractRandomBasedUuidCreator {

	protected static final int ONE_MINUTE = 60_000;

	public ShortPrefixCombCreator() {
		super(UuidVersion.VERSION_RANDOM_BASED);
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

		final long timestamp = System.currentTimeMillis() / ONE_MINUTE;

		// Get random values for MSB and LSB
		final byte[] bytes = new byte[14];
		this.randomStrategy.nextBytes(bytes);
		long msb = ByteUtil.toNumber(bytes, 8, 14);
		long lsb = ByteUtil.toNumber(bytes, 0, 8);

		// Insert the short prefix in the MSB
		msb = (msb & 0x0000ffffffffffffL) | ((timestamp & 0x000000000000ffffL) << 48);

		// Set the version and variant bits
		return getUuid(msb, lsb);
	}
}
