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

package com.github.f4b6a3.uuid.util;

import java.util.UUID;

import com.github.f4b6a3.uuid.exception.IllegalUuidException;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

/**
 * Utility that converts UUIDs to and from strings, byte arrays or other UUID
 * types.
 */
public final class UuidConverter {

	private UuidConverter() {
	}

	/**
	 * Get the array of bytes from a UUID.
	 * 
	 * @param uuid a UUID
	 * @return an array of bytes
	 */
	public static byte[] toBytes(UUID uuid) {
		return ByteUtil.toBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}

	/**
	 * Get a UUID from an array of bytes.
	 * 
	 * @param bytes an array of bytes
	 * @return a UUID
	 * @throws InvalidUuidException if invalid
	 */
	public static UUID fromBytes(byte[] bytes) {
		UuidValidator.validate(bytes);
		long msb = ByteUtil.toNumber(bytes, 0, 8);
		long lsb = ByteUtil.toNumber(bytes, 8, 16);
		return new UUID(msb, lsb);
	}

	/**
	 * Get a string from a UUID.
	 * 
	 * It's an alternative to {@link java.util.UUID#toString()}.
	 * 
	 * @param uuid a UUID
	 * @return a UUID string
	 */
	public static String toString(UUID uuid) {
		char[] input = ByteUtil.toHexadecimalChars(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
		char[] output = new char[36];

		System.arraycopy(input, 0, output, 0, 8);
		System.arraycopy(input, 8, output, 9, 4);
		System.arraycopy(input, 12, output, 14, 4);
		System.arraycopy(input, 16, output, 19, 4);
		System.arraycopy(input, 20, output, 24, 12);

		output[8] = '-';
		output[13] = '-';
		output[18] = '-';
		output[23] = '-';

		return new String(output);
	}

	/**
	 * Get a UUID from a string.
	 * 
	 * It also accepts UUID strings without hyphens.
	 * 
	 * It's an alternative to {@link java.util.UUID#fromString(String)}.
	 * 
	 * @param string a UUID string
	 * @return a UUID
	 * @throws InvalidUuidException if invalid
	 */
	public static UUID fromString(String string) {
		UuidValidator.validate(string);
		char[] input = string.toCharArray();
		char[] output = UuidUtil.removeHyphens(input);
		return fromBytes(ByteUtil.toBytes(output));
	}
	
	/**
	 * Convert a time-ordered UUID to a time-based UUID.
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 * @throws IllegalUuidException if the input is not a time-ordered UUID
	 */
	public static UUID toTimeBasedUuid(UUID uuid) {

		if (!UuidUtil.isTimeOrdered(uuid)) {
			throw new IllegalUuidException(String.format("Not a time-ordered UUID: %s.", uuid.toString()));
		}

		long timestamp = UuidUtil.extractTimestamp(uuid);

		long msb = ((timestamp & 0x0fff_0000_00000000L) >>> 48) //
				| ((timestamp & 0x0000_ffff_00000000L) >>> 16) //
				| ((timestamp & 0x0000_0000_ffffffffL) << 32) //
				| 0x0000000000001000L; // set version 1

		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	/**
	 * Convert a time-based UUID to a time-ordered UUID.
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 * @throws IllegalUuidException if the input is not a time-based UUID
	 */
	public static UUID toTimeOrderedUuid(UUID uuid) {

		if (!UuidUtil.isTimeBased(uuid)) {
			throw new IllegalUuidException(String.format("Not a time-based UUID: %s.", uuid.toString()));
		}

		long timestamp = UuidUtil.extractTimestamp(uuid);

		long msb = ((timestamp & 0x0ffffffffffff000L) << 4) //
				| (timestamp & 0x0000000000000fffL) //
				| 0x0000000000006000L; // set version 6

		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	/**
	 * Convert a UUID to and from a MS GUID.
	 * 
	 * This method is only useful for MS SQL Server.
	 * 
	 * It rearranges the most significant bytes from big-endian to little-endian,
	 * and vice-versa.
	 * 
	 * The Microsoft GUID format stores the most significant bytes as little-endian,
	 * while the least significant bytes are stored as big-endian (network order).
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 */
	public static UUID toAndFromMsGuid(UUID uuid) {

		long msb = toAndFromMsGuidMostSignificantBits(uuid.getMostSignificantBits());
		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	/**
	 * Convert to and from MS GUID most significant bits.
	 * 
	 * ### References
	 * 
	 * * How to Generate Sequential GUIDs for SQL Server in .NET
	 * 
	 * https://blogs.msdn.microsoft.com/dbrowne/2012/07/03/how-to-generate-sequential-guids-for-sql-server-in-net/
	 * 
	 * * UUID Binary encoding
	 * 
	 * https://en.wikipedia.org/wiki/Universally_unique_identifier#Encoding
	 * 
	 * * Newsequentialid (Histrory/Benefits and Implementation)
	 * 
	 * https://blogs.msdn.microsoft.com/sqlprogrammability/2006/03/23/newsequentialid-histrorybenefits-and-implementation/
	 * 
	 * * NEWSEQUENTIALID (Transact-SQL)
	 * 
	 * https://docs.microsoft.com/en-us/sql/t-sql/functions/newsequentialid-transact-sql?view=sql-server-2017
	 * 
	 * * How are GUIDs sorted by SQL Server?
	 * 
	 * http://sqlblog.com/blogs/alberto_ferrari/archive/2007/08/31/how-are-guids-sorted-by-sql-server.aspx
	 * 
	 * * SqlGuid.CompareTo Method
	 * 
	 * https://docs.microsoft.com/en-us/dotnet/api/system.data.sqltypes.sqlguid.compareto?view=netframework-4.7.2#System_Data_SqlTypes_SqlGuid_CompareTo_System_Data_SqlTypes_SqlGuid_
	 * 
	 * * Comparing GUID and uniqueidentifier Values
	 * 
	 * https://docs.microsoft.com/pt-br/dotnet/framework/data/adonet/sql/comparing-guid-and-uniqueidentifier-values
	 * 
	 * @param msb the most significant bits
	 * @return the MSB
	 */
	private static long toAndFromMsGuidMostSignificantBits(final long msb) {

		long bits = 0x0000000000000000L;
		// high bits
		bits |= (msb & 0xff000000_0000_0000L) >>> 24;
		bits |= (msb & 0x00ff0000_0000_0000L) >>> 8;
		bits |= (msb & 0x0000ff00_0000_0000L) << 8;
		bits |= (msb & 0x000000ff_0000_0000L) << 24;
		// mid bits
		bits |= (msb & 0x00000000_ff00_0000L) >>> 8;
		bits |= (msb & 0x00000000_00ff_0000L) << 8;
		// low bits
		bits |= (msb & 0x00000000_0000_ff00L) >>> 8;
		bits |= (msb & 0x00000000_0000_00ffL) << 8;

		return bits;
	}
}
