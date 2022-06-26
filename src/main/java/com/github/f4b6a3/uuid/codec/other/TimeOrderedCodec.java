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

package com.github.f4b6a3.uuid.codec.other;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidUtil;
import com.github.f4b6a3.uuid.util.UuidValidator;

/**
 * Codec for time-ordered UUIDs
 * 
 * This codec converts time-based UUIDs (v1) to time-ordered UUIDs (v6).
 */
public class TimeOrderedCodec implements UuidCodec<UUID> {

	/**
	 * A shared immutable instance.
	 */
	public static final TimeOrderedCodec INSTANCE = new TimeOrderedCodec();

	/**
	 * Get a time-ordered UUID from a time-based UUID.
	 * 
	 * @param a time-based UUID
	 * @return a time-ordered UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public UUID encode(UUID uuid) {

		UuidValidator.validate(uuid);

		if (!UuidUtil.isTimeBased(uuid)) {
			throw new InvalidUuidException("Not a time-based UUID: " + uuid);
		}

		long timestamp = UuidUtil.getTimestamp(uuid);

		long msb = ((timestamp & 0x0ffffffffffff000L) << 4) //
				| (timestamp & 0x0000000000000fffL) //
				| 0x0000000000006000L; // set version 6

		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	/**
	 * Get a time-based UUID from a time-ordered UUID.
	 * 
	 * @param a time-ordered UUID
	 * @return a time-based UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public UUID decode(UUID uuid) {

		UuidValidator.validate(uuid);

		if (!UuidUtil.isTimeOrdered(uuid)) {
			throw new InvalidUuidException("Not a time-ordered UUID: " + uuid);
		}

		long timestamp = UuidUtil.getTimestamp(uuid);

		long msb = ((timestamp & 0x0fff_0000_00000000L) >>> 48) //
				| ((timestamp & 0x0000_ffff_00000000L) >>> 16) //
				| ((timestamp & 0x0000_0000_ffffffffL) << 32) //
				| 0x0000000000001000L; // set version 1

		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}
}
