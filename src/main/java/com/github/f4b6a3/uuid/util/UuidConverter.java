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

import com.github.f4b6a3.uuid.exception.InvalidUuidException;

/**
 * Utility that converts UUIDs to and from strings, byte arrays or other UUID
 * types.
 */
public final class UuidConverter {

	private static final String URN_PREFIX = "urn:uuid:";

	private static final char[] HEX_CHARS = //
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static final long[] HEX_VALUES = new long[128];
	static {

		HEX_VALUES['0'] = 0x0;
		HEX_VALUES['1'] = 0x1;
		HEX_VALUES['2'] = 0x2;
		HEX_VALUES['3'] = 0x3;
		HEX_VALUES['4'] = 0x4;
		HEX_VALUES['5'] = 0x5;
		HEX_VALUES['6'] = 0x6;
		HEX_VALUES['7'] = 0x7;
		HEX_VALUES['8'] = 0x8;
		HEX_VALUES['9'] = 0x9;

		HEX_VALUES['a'] = 0xa;
		HEX_VALUES['b'] = 0xb;
		HEX_VALUES['c'] = 0xc;
		HEX_VALUES['d'] = 0xd;
		HEX_VALUES['e'] = 0xe;
		HEX_VALUES['f'] = 0xf;

		HEX_VALUES['A'] = 0xa;
		HEX_VALUES['B'] = 0xb;
		HEX_VALUES['C'] = 0xc;
		HEX_VALUES['D'] = 0xd;
		HEX_VALUES['E'] = 0xe;
		HEX_VALUES['F'] = 0xf;
	}

	private UuidConverter() {
	}

	/**
	 * Get the array of bytes from a UUID.
	 * 
	 * @param uuid a UUID
	 * @return an array of bytes
	 */
	public static byte[] toBytes(final UUID uuid) {

		final byte[] bytes = new byte[16];
		final long msb = uuid.getMostSignificantBits();
		final long lsb = uuid.getLeastSignificantBits();

		bytes[0x0] = (byte) (msb >>> 56);
		bytes[0x1] = (byte) (msb >>> 48);
		bytes[0x2] = (byte) (msb >>> 40);
		bytes[0x3] = (byte) (msb >>> 32);
		bytes[0x4] = (byte) (msb >>> 24);
		bytes[0x5] = (byte) (msb >>> 16);
		bytes[0x6] = (byte) (msb >>> 8);
		bytes[0x7] = (byte) (msb);

		bytes[0x8] = (byte) (lsb >>> 56);
		bytes[0x9] = (byte) (lsb >>> 48);
		bytes[0xa] = (byte) (lsb >>> 40);
		bytes[0xb] = (byte) (lsb >>> 32);
		bytes[0xc] = (byte) (lsb >>> 24);
		bytes[0xd] = (byte) (lsb >>> 16);
		bytes[0xe] = (byte) (lsb >>> 8);
		bytes[0xf] = (byte) (lsb);

		return bytes;
	}

	/**
	 * Get a UUID from an array of bytes.
	 * 
	 * @param bytes an array of bytes
	 * @return a UUID
	 * @throws InvalidUuidException if invalid
	 */
	public static UUID fromBytes(final byte[] bytes) {

		UuidValidator.validate(bytes);

		long msb = 0;
		long lsb = 0;

		msb |= (bytes[0x0] & 0xffL) << 56;
		msb |= (bytes[0x1] & 0xffL) << 48;
		msb |= (bytes[0x2] & 0xffL) << 40;
		msb |= (bytes[0x3] & 0xffL) << 32;
		msb |= (bytes[0x4] & 0xffL) << 24;
		msb |= (bytes[0x5] & 0xffL) << 16;
		msb |= (bytes[0x6] & 0xffL) << 8;
		msb |= (bytes[0x7] & 0xffL);

		lsb |= (bytes[0x8] & 0xffL) << 56;
		lsb |= (bytes[0x9] & 0xffL) << 48;
		lsb |= (bytes[0xa] & 0xffL) << 40;
		lsb |= (bytes[0xb] & 0xffL) << 32;
		lsb |= (bytes[0xc] & 0xffL) << 24;
		lsb |= (bytes[0xd] & 0xffL) << 16;
		lsb |= (bytes[0xe] & 0xffL) << 8;
		lsb |= (bytes[0xf] & 0xffL);

		return new UUID(msb, lsb);
	}

	/**
	 * Get a string from a UUID.
	 * 
	 * It is much faster than {@link UUID#toString()} in JDK 8.
	 * 
	 * In JDK9+ prefer {@link UUID#toString()}.
	 * 
	 * @param uuid a UUID
	 * @return a UUID string
	 */
	public static String toString(UUID uuid) {

		final char[] chars = new char[36];
		final long msb = uuid.getMostSignificantBits();
		final long lsb = uuid.getLeastSignificantBits();

		chars[0x00] = HEX_CHARS[(int) (msb >>> 0x3c & 0xf)];
		chars[0x01] = HEX_CHARS[(int) (msb >>> 0x38 & 0xf)];
		chars[0x02] = HEX_CHARS[(int) (msb >>> 0x34 & 0xf)];
		chars[0x03] = HEX_CHARS[(int) (msb >>> 0x30 & 0xf)];
		chars[0x04] = HEX_CHARS[(int) (msb >>> 0x2c & 0xf)];
		chars[0x05] = HEX_CHARS[(int) (msb >>> 0x28 & 0xf)];
		chars[0x06] = HEX_CHARS[(int) (msb >>> 0x24 & 0xf)];
		chars[0x07] = HEX_CHARS[(int) (msb >>> 0x20 & 0xf)];
		chars[0x08] = '-'; // 8
		chars[0x09] = HEX_CHARS[(int) (msb >>> 0x1c & 0xf)];
		chars[0x0a] = HEX_CHARS[(int) (msb >>> 0x18 & 0xf)];
		chars[0x0b] = HEX_CHARS[(int) (msb >>> 0x14 & 0xf)];
		chars[0x0c] = HEX_CHARS[(int) (msb >>> 0x10 & 0xf)];
		chars[0x0d] = '-'; // 13
		chars[0x0e] = HEX_CHARS[(int) (msb >>> 0x0c & 0xf)];
		chars[0x0f] = HEX_CHARS[(int) (msb >>> 0x08 & 0xf)];
		chars[0x10] = HEX_CHARS[(int) (msb >>> 0x04 & 0xf)];
		chars[0x11] = HEX_CHARS[(int) (msb & 0xf)];
		chars[0x12] = '-'; // 18
		chars[0x13] = HEX_CHARS[(int) (lsb >>> 0x3c & 0xf)];
		chars[0x14] = HEX_CHARS[(int) (lsb >>> 0x38 & 0xf)];
		chars[0x15] = HEX_CHARS[(int) (lsb >>> 0x34 & 0xf)];
		chars[0x16] = HEX_CHARS[(int) (lsb >>> 0x30 & 0xf)];
		chars[0x17] = '-'; // 23
		chars[0x18] = HEX_CHARS[(int) (lsb >>> 0x2c & 0xf)];
		chars[0x19] = HEX_CHARS[(int) (lsb >>> 0x28 & 0xf)];
		chars[0x1a] = HEX_CHARS[(int) (lsb >>> 0x24 & 0xf)];
		chars[0x1b] = HEX_CHARS[(int) (lsb >>> 0x20 & 0xf)];
		chars[0x1c] = HEX_CHARS[(int) (lsb >>> 0x1c & 0xf)];
		chars[0x1d] = HEX_CHARS[(int) (lsb >>> 0x18 & 0xf)];
		chars[0x1e] = HEX_CHARS[(int) (lsb >>> 0x14 & 0xf)];
		chars[0x1f] = HEX_CHARS[(int) (lsb >>> 0x10 & 0xf)];
		chars[0x20] = HEX_CHARS[(int) (lsb >>> 0x0c & 0xf)];
		chars[0x21] = HEX_CHARS[(int) (lsb >>> 0x08 & 0xf)];
		chars[0x22] = HEX_CHARS[(int) (lsb >>> 0x04 & 0xf)];
		chars[0x23] = HEX_CHARS[(int) (lsb & 0xf)];

		return new String(chars);
	}

	/**
	 * Get a UUID from a string.
	 * 
	 * It accepts strings:
	 * 
	 * - With URN prefix: "urn:uuid:";
	 * 
	 * - With curly braces: '{' and '}';
	 * 
	 * - With upper or lower case;
	 * 
	 * - With or without hyphens.
	 * 
	 * It is much faster than {@link UUID#fromString(String)} in JDK 8.
	 * 
	 * @param string a UUID string
	 * @return a UUID
	 * @throws InvalidUuidException if invalid
	 */
	public static UUID fromString(String string) {

		char[] chars = string == null ? new char[0] : string.toCharArray();

		if (chars[0] == 'u' && string != null && string.indexOf(URN_PREFIX) == 0) {
			// Remove URN prefix: "urn:uuid:"
			char[] substring = new char[chars.length - 9];
			System.arraycopy(chars, 9, substring, 0, substring.length);
			chars = substring;
		} else if (chars[0] == '{' && chars[chars.length - 1] == '}') {
			// Remove curly braces: '{' and '}'
			char[] substring = new char[chars.length - 2];
			System.arraycopy(chars, 1, substring, 0, substring.length);
			chars = substring;
		}

		UuidValidator.validate(chars);

		long msb = 0;
		long lsb = 0;
		
		if (chars.length == 32) {
			// UUID string WITHOUT hyphen
			msb |= HEX_VALUES[chars[0x00]] << 60;
			msb |= HEX_VALUES[chars[0x01]] << 56;
			msb |= HEX_VALUES[chars[0x02]] << 52;
			msb |= HEX_VALUES[chars[0x03]] << 48;
			msb |= HEX_VALUES[chars[0x04]] << 44;
			msb |= HEX_VALUES[chars[0x05]] << 40;
			msb |= HEX_VALUES[chars[0x06]] << 36;
			msb |= HEX_VALUES[chars[0x07]] << 32;
			msb |= HEX_VALUES[chars[0x08]] << 28;
			msb |= HEX_VALUES[chars[0x09]] << 24;
			msb |= HEX_VALUES[chars[0x0a]] << 20;
			msb |= HEX_VALUES[chars[0x0b]] << 16;
			msb |= HEX_VALUES[chars[0x0c]] << 12;
			msb |= HEX_VALUES[chars[0x0d]] << 8;
			msb |= HEX_VALUES[chars[0x0e]] << 4;
			msb |= HEX_VALUES[chars[0x0f]];

			lsb |= HEX_VALUES[chars[0x10]] << 60;
			lsb |= HEX_VALUES[chars[0x11]] << 56;
			lsb |= HEX_VALUES[chars[0x12]] << 52;
			lsb |= HEX_VALUES[chars[0x13]] << 48;
			lsb |= HEX_VALUES[chars[0x14]] << 44;
			lsb |= HEX_VALUES[chars[0x15]] << 40;
			lsb |= HEX_VALUES[chars[0x16]] << 36;
			lsb |= HEX_VALUES[chars[0x17]] << 32;
			lsb |= HEX_VALUES[chars[0x18]] << 28;
			lsb |= HEX_VALUES[chars[0x19]] << 24;
			lsb |= HEX_VALUES[chars[0x1a]] << 20;
			lsb |= HEX_VALUES[chars[0x1b]] << 16;
			lsb |= HEX_VALUES[chars[0x1c]] << 12;
			lsb |= HEX_VALUES[chars[0x1d]] << 8;
			lsb |= HEX_VALUES[chars[0x1e]] << 4;
			lsb |= HEX_VALUES[chars[0x1f]];
		} else {
			// UUID string WITH hyphen
			msb |= HEX_VALUES[chars[0x00]] << 60;
			msb |= HEX_VALUES[chars[0x01]] << 56;
			msb |= HEX_VALUES[chars[0x02]] << 52;
			msb |= HEX_VALUES[chars[0x03]] << 48;
			msb |= HEX_VALUES[chars[0x04]] << 44;
			msb |= HEX_VALUES[chars[0x05]] << 40;
			msb |= HEX_VALUES[chars[0x06]] << 36;
			msb |= HEX_VALUES[chars[0x07]] << 32;
			// input[8] = '-'
			msb |= HEX_VALUES[chars[0x09]] << 28;
			msb |= HEX_VALUES[chars[0x0a]] << 24;
			msb |= HEX_VALUES[chars[0x0b]] << 20;
			msb |= HEX_VALUES[chars[0x0c]] << 16;
			// input[13] = '-'
			msb |= HEX_VALUES[chars[0x0e]] << 12;
			msb |= HEX_VALUES[chars[0x0f]] << 8;
			msb |= HEX_VALUES[chars[0x10]] << 4;
			msb |= HEX_VALUES[chars[0x11]];
			// input[18] = '-'
			lsb |= HEX_VALUES[chars[0x13]] << 60;
			lsb |= HEX_VALUES[chars[0x14]] << 56;
			lsb |= HEX_VALUES[chars[0x15]] << 52;
			lsb |= HEX_VALUES[chars[0x16]] << 48;
			// input[23] = '-'
			lsb |= HEX_VALUES[chars[0x18]] << 44;
			lsb |= HEX_VALUES[chars[0x19]] << 40;
			lsb |= HEX_VALUES[chars[0x1a]] << 36;
			lsb |= HEX_VALUES[chars[0x1b]] << 32;
			lsb |= HEX_VALUES[chars[0x1c]] << 28;
			lsb |= HEX_VALUES[chars[0x1d]] << 24;
			lsb |= HEX_VALUES[chars[0x1e]] << 20;
			lsb |= HEX_VALUES[chars[0x1f]] << 16;
			lsb |= HEX_VALUES[chars[0x20]] << 12;
			lsb |= HEX_VALUES[chars[0x21]] << 8;
			lsb |= HEX_VALUES[chars[0x22]] << 4;
			lsb |= HEX_VALUES[chars[0x23]];
		}

		return new UUID(msb, lsb);
	}

	/**
	 * Convert a time-ordered UUID to a time-based UUID.
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 * @throws IllegalArgumentException if the input is not a time-ordered UUID
	 */
	public static UUID toTimeBasedUuid(UUID uuid) {

		if (!UuidUtil.isTimeOrdered(uuid)) {
			throw new IllegalArgumentException(String.format("Not a time-ordered UUID: %s.", uuid.toString()));
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
	 * @throws IllegalArgumentException if the input is not a time-based UUID
	 */
	public static UUID toTimeOrderedUuid(UUID uuid) {

		if (!UuidUtil.isTimeBased(uuid)) {
			throw new IllegalArgumentException(String.format("Not a time-based UUID: %s.", uuid.toString()));
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
