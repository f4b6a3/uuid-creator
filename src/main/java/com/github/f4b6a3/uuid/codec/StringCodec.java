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

package com.github.f4b6a3.uuid.codec;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.base.Base16Codec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidValidator;

/**
 * Codec for UUID string representation as defined in the RFC-4122.
 * 
 * The string representation, also referred as canonical textual representation,
 * is a string of 32 hexadecimal (base-16) digits, displayed in five groups
 * separated by hyphens, in the form 8-4-4-4-12 for a total of 36 characters (32
 * hexadecimal characters and 4 hyphens).
 * 
 * This codec decodes (parses) strings in these formats, with/without hyphens:
 * 
 * - 00000000-0000-V000-0000-000000000000 (canonical string)
 * 
 * - {00000000-0000-V000-0000-000000000000} (MS GUID string)
 * 
 * - urn:uuid:00000000-0000-V000-0000-000000000000 (URN UUID string)
 * 
 * The encoding and decoding processes may be much faster (5 to 7x) than
 * {@link UUID#toString()} and {@link UUID#fromString(String)} in JDK 8.
 * 
 * If you prefer a string representation without hyphens, use the
 * {@link Base16Codec} instead of {@link StringCodec}. This other codec may be
 * much faster (10x) than doing
 * <code>uuid.toString().replaceAll("-", "")`</code>.
 *
 * Read: https://tools.ietf.org/html/rfc4122
 * 
 * Read also: https://en.wikipedia.org/wiki/Universally_unique_identifier#Format
 */
public class StringCodec implements UuidCodec<String> {

	protected static final String URN_PREFIX = "urn:uuid:";

	protected static final char[] ALPHABET = //
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	protected static final long[] MAP = new long[128];
	static {
		// Initialize with -1
		for (int i = 0; i < MAP.length; i++) {
			MAP[i] = -1;
		}
		// Numbers
		MAP['0'] = 0x0;
		MAP['1'] = 0x1;
		MAP['2'] = 0x2;
		MAP['3'] = 0x3;
		MAP['4'] = 0x4;
		MAP['5'] = 0x5;
		MAP['6'] = 0x6;
		MAP['7'] = 0x7;
		MAP['8'] = 0x8;
		MAP['9'] = 0x9;
		// Lower case
		MAP['a'] = 0xa;
		MAP['b'] = 0xb;
		MAP['c'] = 0xc;
		MAP['d'] = 0xd;
		MAP['e'] = 0xe;
		MAP['f'] = 0xf;
		// Upper case
		MAP['A'] = 0xa;
		MAP['B'] = 0xb;
		MAP['C'] = 0xc;
		MAP['D'] = 0xd;
		MAP['E'] = 0xe;
		MAP['F'] = 0xf;
	}

	private static final boolean JAVA_VERSION_GREATER_THAN_8 = getJavaVersion() > 8;

	/**
	 * Get a string from a UUID.
	 * 
	 * It may be much faster than {@link UUID#toString()} in JDK 8.
	 * 
	 * In JDK9+ it uses {@link UUID#toString()}.
	 * 
	 * @param uuid a UUID
	 * @return a UUID string
	 */
	@Override
	public String encode(UUID uuid) {

		if (JAVA_VERSION_GREATER_THAN_8) {
			return uuid.toString();
		}

		final char[] chars = new char[36];
		final long msb = uuid.getMostSignificantBits();
		final long lsb = uuid.getLeastSignificantBits();

		chars[0x00] = ALPHABET[(int) (msb >>> 0x3c & 0xf)];
		chars[0x01] = ALPHABET[(int) (msb >>> 0x38 & 0xf)];
		chars[0x02] = ALPHABET[(int) (msb >>> 0x34 & 0xf)];
		chars[0x03] = ALPHABET[(int) (msb >>> 0x30 & 0xf)];
		chars[0x04] = ALPHABET[(int) (msb >>> 0x2c & 0xf)];
		chars[0x05] = ALPHABET[(int) (msb >>> 0x28 & 0xf)];
		chars[0x06] = ALPHABET[(int) (msb >>> 0x24 & 0xf)];
		chars[0x07] = ALPHABET[(int) (msb >>> 0x20 & 0xf)];
		chars[0x08] = '-'; // 8
		chars[0x09] = ALPHABET[(int) (msb >>> 0x1c & 0xf)];
		chars[0x0a] = ALPHABET[(int) (msb >>> 0x18 & 0xf)];
		chars[0x0b] = ALPHABET[(int) (msb >>> 0x14 & 0xf)];
		chars[0x0c] = ALPHABET[(int) (msb >>> 0x10 & 0xf)];
		chars[0x0d] = '-'; // 13
		chars[0x0e] = ALPHABET[(int) (msb >>> 0x0c & 0xf)];
		chars[0x0f] = ALPHABET[(int) (msb >>> 0x08 & 0xf)];
		chars[0x10] = ALPHABET[(int) (msb >>> 0x04 & 0xf)];
		chars[0x11] = ALPHABET[(int) (msb & 0xf)];
		chars[0x12] = '-'; // 18
		chars[0x13] = ALPHABET[(int) (lsb >>> 0x3c & 0xf)];
		chars[0x14] = ALPHABET[(int) (lsb >>> 0x38 & 0xf)];
		chars[0x15] = ALPHABET[(int) (lsb >>> 0x34 & 0xf)];
		chars[0x16] = ALPHABET[(int) (lsb >>> 0x30 & 0xf)];
		chars[0x17] = '-'; // 23
		chars[0x18] = ALPHABET[(int) (lsb >>> 0x2c & 0xf)];
		chars[0x19] = ALPHABET[(int) (lsb >>> 0x28 & 0xf)];
		chars[0x1a] = ALPHABET[(int) (lsb >>> 0x24 & 0xf)];
		chars[0x1b] = ALPHABET[(int) (lsb >>> 0x20 & 0xf)];
		chars[0x1c] = ALPHABET[(int) (lsb >>> 0x1c & 0xf)];
		chars[0x1d] = ALPHABET[(int) (lsb >>> 0x18 & 0xf)];
		chars[0x1e] = ALPHABET[(int) (lsb >>> 0x14 & 0xf)];
		chars[0x1f] = ALPHABET[(int) (lsb >>> 0x10 & 0xf)];
		chars[0x20] = ALPHABET[(int) (lsb >>> 0x0c & 0xf)];
		chars[0x21] = ALPHABET[(int) (lsb >>> 0x08 & 0xf)];
		chars[0x22] = ALPHABET[(int) (lsb >>> 0x04 & 0xf)];
		chars[0x23] = ALPHABET[(int) (lsb & 0xf)];

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
	 * It may be much faster than {@link UUID#fromString(String)} in JDK 8.
	 * 
	 * In JDK9+ it may be slightly faster.
	 * 
	 * @param string a UUID string
	 * @return a UUID
	 * @throws InvalidUuidException if invalid
	 */
	@Override
	public UUID decode(String string) {

		if (string == null) {
			throw new InvalidUuidException("Invalid UUID: null");
		}

		char[] chars = string.toCharArray();

		if (chars.length != 32 && chars.length != 36) {
			if (string.startsWith(URN_PREFIX)) {
				// Remove URN prefix: "urn:uuid:"
				char[] substring = new char[chars.length - 9];
				System.arraycopy(chars, 9, substring, 0, substring.length);
				chars = substring;
			} else if (chars.length > 0 && chars[0] == '{' && chars[chars.length - 1] == '}') {
				// Remove curly braces: '{' and '}'
				char[] substring = new char[chars.length - 2];
				System.arraycopy(chars, 1, substring, 0, substring.length);
				chars = substring;
			}
		}

		UuidValidator.validate(chars);

		long msb = 0;
		long lsb = 0;

		if (chars.length == 32) {
			// UUID string WITHOUT hyphen
			msb |= MAP[chars[0x00]] << 60;
			msb |= MAP[chars[0x01]] << 56;
			msb |= MAP[chars[0x02]] << 52;
			msb |= MAP[chars[0x03]] << 48;
			msb |= MAP[chars[0x04]] << 44;
			msb |= MAP[chars[0x05]] << 40;
			msb |= MAP[chars[0x06]] << 36;
			msb |= MAP[chars[0x07]] << 32;
			msb |= MAP[chars[0x08]] << 28;
			msb |= MAP[chars[0x09]] << 24;
			msb |= MAP[chars[0x0a]] << 20;
			msb |= MAP[chars[0x0b]] << 16;
			msb |= MAP[chars[0x0c]] << 12;
			msb |= MAP[chars[0x0d]] << 8;
			msb |= MAP[chars[0x0e]] << 4;
			msb |= MAP[chars[0x0f]];

			lsb |= MAP[chars[0x10]] << 60;
			lsb |= MAP[chars[0x11]] << 56;
			lsb |= MAP[chars[0x12]] << 52;
			lsb |= MAP[chars[0x13]] << 48;
			lsb |= MAP[chars[0x14]] << 44;
			lsb |= MAP[chars[0x15]] << 40;
			lsb |= MAP[chars[0x16]] << 36;
			lsb |= MAP[chars[0x17]] << 32;
			lsb |= MAP[chars[0x18]] << 28;
			lsb |= MAP[chars[0x19]] << 24;
			lsb |= MAP[chars[0x1a]] << 20;
			lsb |= MAP[chars[0x1b]] << 16;
			lsb |= MAP[chars[0x1c]] << 12;
			lsb |= MAP[chars[0x1d]] << 8;
			lsb |= MAP[chars[0x1e]] << 4;
			lsb |= MAP[chars[0x1f]];
		} else {
			// UUID string WITH hyphen
			msb |= MAP[chars[0x00]] << 60;
			msb |= MAP[chars[0x01]] << 56;
			msb |= MAP[chars[0x02]] << 52;
			msb |= MAP[chars[0x03]] << 48;
			msb |= MAP[chars[0x04]] << 44;
			msb |= MAP[chars[0x05]] << 40;
			msb |= MAP[chars[0x06]] << 36;
			msb |= MAP[chars[0x07]] << 32;
			// input[8] = '-'
			msb |= MAP[chars[0x09]] << 28;
			msb |= MAP[chars[0x0a]] << 24;
			msb |= MAP[chars[0x0b]] << 20;
			msb |= MAP[chars[0x0c]] << 16;
			// input[13] = '-'
			msb |= MAP[chars[0x0e]] << 12;
			msb |= MAP[chars[0x0f]] << 8;
			msb |= MAP[chars[0x10]] << 4;
			msb |= MAP[chars[0x11]];
			// input[18] = '-'
			lsb |= MAP[chars[0x13]] << 60;
			lsb |= MAP[chars[0x14]] << 56;
			lsb |= MAP[chars[0x15]] << 52;
			lsb |= MAP[chars[0x16]] << 48;
			// input[23] = '-'
			lsb |= MAP[chars[0x18]] << 44;
			lsb |= MAP[chars[0x19]] << 40;
			lsb |= MAP[chars[0x1a]] << 36;
			lsb |= MAP[chars[0x1b]] << 32;
			lsb |= MAP[chars[0x1c]] << 28;
			lsb |= MAP[chars[0x1d]] << 24;
			lsb |= MAP[chars[0x1e]] << 20;
			lsb |= MAP[chars[0x1f]] << 16;
			lsb |= MAP[chars[0x20]] << 12;
			lsb |= MAP[chars[0x21]] << 8;
			lsb |= MAP[chars[0x22]] << 4;
			lsb |= MAP[chars[0x23]];
		}

		return new UUID(msb, lsb);
	}

	/**
	 * Returns the java major version number.
	 * 
	 * See: https://www.oracle.com/java/technologies/javase/naming-and-versions.html
	 * 
	 * @return major version number
	 */
	protected static int getJavaVersion() {
		try {
			String[] version = System.getProperty("java.version").split("\\.");
			if (version[0].equals("1")) {
				return Integer.parseInt(version[1]);
			} else {
				return Integer.parseInt(version[0]);
			}
		} catch (NullPointerException | NumberFormatException | IndexOutOfBoundsException e) {
			return 8;
		}
	}
}
