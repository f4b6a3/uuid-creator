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
 * Codec for time-based .Net Guids.
 */
public class DotNetGuid1Codec implements UuidCodec<UUID> {

	/**
	 * A shared immutable instance.
	 */
	public static final DotNetGuid1Codec INSTANCE = new DotNetGuid1Codec();

	/**
	 * Get a .Ned Guid from a time-based UUID (v1).
	 * 
	 * This codec converts a time-based UUID (v1) to a .Net Guid.
	 * 
	 * It rearranges the most significant bytes from big-endian to little-endian,
	 * and vice-versa.
	 * 
	 * The .Net Guid stores the most significant bytes as little-endian, while the
	 * least significant bytes are stored as big-endian (network order).
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public UUID encode(UUID uuid) {
		UuidValidator.validate(uuid);
		if (!UuidUtil.isTimeBased(uuid)) {
			throw new InvalidUuidException(String.format("Not a time-based UUID: %s.", uuid.toString()));
		}
		return toAndFromDotNetGuid(uuid);
	}

	/**
	 * Get a time-based UUID (v4) from a .Net Guid.
	 * 
	 * It rearranges the most significant bytes from big-endian to little-endian,
	 * and vice-versa.
	 * 
	 * The .Net Guid stores the most significant bytes as little-endian, while the
	 * least significant bytes are stored as big-endian (network order).
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public UUID decode(UUID uuid) {
		UuidValidator.validate(uuid);
		UUID uuidv1 = toAndFromDotNetGuid(uuid);
		if (!UuidUtil.isTimeBased(uuidv1)) {
			throw new InvalidUuidException(String.format("Not a time-based UUID: %s.", uuidv1.toString()));
		}
		return uuidv1;
	}

	/**
	 * Convert a UUID to and from a .Net Guid.
	 * 
	 * It rearranges the most significant bytes from big-endian to little-endian,
	 * and vice-versa.
	 * 
	 * The .Net Guid stores the most significant bytes as little-endian, while the
	 * least significant bytes are stored as big-endian (network order).
	 * 
	 * ### References
	 * 
	 * * How to Generate Sequential GUIDs for SQL Server in .NET
	 * 
	 * https://blogs.msdn.microsoft.com/dbrowne/2012/07/03/how-to-generate-sequential-guids-for-sql-server-in-net/
	 * 
	 * * How are GUIDs sorted by SQL Server?
	 * 
	 * http://sqlblog.com/blogs/alberto_ferrari/archive/2007/08/31/how-are-guids-sorted-by-sql-server.aspx
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 */
	protected static UUID toAndFromDotNetGuid(UUID uuid) {

		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();

		long newMsb = 0x0000000000000000L;
		// high bits
		newMsb |= (msb & 0xff000000_0000_0000L) >>> 24;
		newMsb |= (msb & 0x00ff0000_0000_0000L) >>> 8;
		newMsb |= (msb & 0x0000ff00_0000_0000L) << 8;
		newMsb |= (msb & 0x000000ff_0000_0000L) << 24;
		// mid bits
		newMsb |= (msb & 0x00000000_ff00_0000L) >>> 8;
		newMsb |= (msb & 0x00000000_00ff_0000L) << 8;
		// low bits
		newMsb |= (msb & 0x00000000_0000_ff00L) >>> 8;
		newMsb |= (msb & 0x00000000_0000_00ffL) << 8;

		return new UUID(newMsb, lsb);
	}
}
