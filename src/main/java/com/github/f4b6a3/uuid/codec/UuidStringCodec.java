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

import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidValidator;

public class UuidStringCodec implements UuidCodec<String> {

	protected static final String URN_PREFIX = "urn:uuid:";

	protected static final char[] BASE_16_CHARS = //
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	protected static final long[] BASE_16_VALUES = new long[128];
	static {
		// Initiate with -1
		for (int i = 0; i < BASE_16_VALUES.length; i++) {
			BASE_16_VALUES[i] = -1;
		}
		// Numbers
		BASE_16_VALUES['0'] = 0x0;
		BASE_16_VALUES['1'] = 0x1;
		BASE_16_VALUES['2'] = 0x2;
		BASE_16_VALUES['3'] = 0x3;
		BASE_16_VALUES['4'] = 0x4;
		BASE_16_VALUES['5'] = 0x5;
		BASE_16_VALUES['6'] = 0x6;
		BASE_16_VALUES['7'] = 0x7;
		BASE_16_VALUES['8'] = 0x8;
		BASE_16_VALUES['9'] = 0x9;
		// Lower case
		BASE_16_VALUES['a'] = 0xa;
		BASE_16_VALUES['b'] = 0xb;
		BASE_16_VALUES['c'] = 0xc;
		BASE_16_VALUES['d'] = 0xd;
		BASE_16_VALUES['e'] = 0xe;
		BASE_16_VALUES['f'] = 0xf;
		// Upper case
		BASE_16_VALUES['A'] = 0xa;
		BASE_16_VALUES['B'] = 0xb;
		BASE_16_VALUES['C'] = 0xc;
		BASE_16_VALUES['D'] = 0xd;
		BASE_16_VALUES['E'] = 0xe;
		BASE_16_VALUES['F'] = 0xf;
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

		chars[0x00] = BASE_16_CHARS[(int) (msb >>> 0x3c & 0xf)];
		chars[0x01] = BASE_16_CHARS[(int) (msb >>> 0x38 & 0xf)];
		chars[0x02] = BASE_16_CHARS[(int) (msb >>> 0x34 & 0xf)];
		chars[0x03] = BASE_16_CHARS[(int) (msb >>> 0x30 & 0xf)];
		chars[0x04] = BASE_16_CHARS[(int) (msb >>> 0x2c & 0xf)];
		chars[0x05] = BASE_16_CHARS[(int) (msb >>> 0x28 & 0xf)];
		chars[0x06] = BASE_16_CHARS[(int) (msb >>> 0x24 & 0xf)];
		chars[0x07] = BASE_16_CHARS[(int) (msb >>> 0x20 & 0xf)];
		chars[0x08] = '-'; // 8
		chars[0x09] = BASE_16_CHARS[(int) (msb >>> 0x1c & 0xf)];
		chars[0x0a] = BASE_16_CHARS[(int) (msb >>> 0x18 & 0xf)];
		chars[0x0b] = BASE_16_CHARS[(int) (msb >>> 0x14 & 0xf)];
		chars[0x0c] = BASE_16_CHARS[(int) (msb >>> 0x10 & 0xf)];
		chars[0x0d] = '-'; // 13
		chars[0x0e] = BASE_16_CHARS[(int) (msb >>> 0x0c & 0xf)];
		chars[0x0f] = BASE_16_CHARS[(int) (msb >>> 0x08 & 0xf)];
		chars[0x10] = BASE_16_CHARS[(int) (msb >>> 0x04 & 0xf)];
		chars[0x11] = BASE_16_CHARS[(int) (msb & 0xf)];
		chars[0x12] = '-'; // 18
		chars[0x13] = BASE_16_CHARS[(int) (lsb >>> 0x3c & 0xf)];
		chars[0x14] = BASE_16_CHARS[(int) (lsb >>> 0x38 & 0xf)];
		chars[0x15] = BASE_16_CHARS[(int) (lsb >>> 0x34 & 0xf)];
		chars[0x16] = BASE_16_CHARS[(int) (lsb >>> 0x30 & 0xf)];
		chars[0x17] = '-'; // 23
		chars[0x18] = BASE_16_CHARS[(int) (lsb >>> 0x2c & 0xf)];
		chars[0x19] = BASE_16_CHARS[(int) (lsb >>> 0x28 & 0xf)];
		chars[0x1a] = BASE_16_CHARS[(int) (lsb >>> 0x24 & 0xf)];
		chars[0x1b] = BASE_16_CHARS[(int) (lsb >>> 0x20 & 0xf)];
		chars[0x1c] = BASE_16_CHARS[(int) (lsb >>> 0x1c & 0xf)];
		chars[0x1d] = BASE_16_CHARS[(int) (lsb >>> 0x18 & 0xf)];
		chars[0x1e] = BASE_16_CHARS[(int) (lsb >>> 0x14 & 0xf)];
		chars[0x1f] = BASE_16_CHARS[(int) (lsb >>> 0x10 & 0xf)];
		chars[0x20] = BASE_16_CHARS[(int) (lsb >>> 0x0c & 0xf)];
		chars[0x21] = BASE_16_CHARS[(int) (lsb >>> 0x08 & 0xf)];
		chars[0x22] = BASE_16_CHARS[(int) (lsb >>> 0x04 & 0xf)];
		chars[0x23] = BASE_16_CHARS[(int) (lsb & 0xf)];

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

		char[] chars = string == null ? new char[0] : string.toCharArray();

		if (chars.length != 32 && chars.length != 36) {
			if (string != null && string.startsWith(URN_PREFIX)) {
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
		}

		UuidValidator.validate(chars);

		long msb = 0;
		long lsb = 0;

		if (chars.length == 32) {
			// UUID string WITHOUT hyphen
			msb |= BASE_16_VALUES[chars[0x00]] << 60;
			msb |= BASE_16_VALUES[chars[0x01]] << 56;
			msb |= BASE_16_VALUES[chars[0x02]] << 52;
			msb |= BASE_16_VALUES[chars[0x03]] << 48;
			msb |= BASE_16_VALUES[chars[0x04]] << 44;
			msb |= BASE_16_VALUES[chars[0x05]] << 40;
			msb |= BASE_16_VALUES[chars[0x06]] << 36;
			msb |= BASE_16_VALUES[chars[0x07]] << 32;
			msb |= BASE_16_VALUES[chars[0x08]] << 28;
			msb |= BASE_16_VALUES[chars[0x09]] << 24;
			msb |= BASE_16_VALUES[chars[0x0a]] << 20;
			msb |= BASE_16_VALUES[chars[0x0b]] << 16;
			msb |= BASE_16_VALUES[chars[0x0c]] << 12;
			msb |= BASE_16_VALUES[chars[0x0d]] << 8;
			msb |= BASE_16_VALUES[chars[0x0e]] << 4;
			msb |= BASE_16_VALUES[chars[0x0f]];

			lsb |= BASE_16_VALUES[chars[0x10]] << 60;
			lsb |= BASE_16_VALUES[chars[0x11]] << 56;
			lsb |= BASE_16_VALUES[chars[0x12]] << 52;
			lsb |= BASE_16_VALUES[chars[0x13]] << 48;
			lsb |= BASE_16_VALUES[chars[0x14]] << 44;
			lsb |= BASE_16_VALUES[chars[0x15]] << 40;
			lsb |= BASE_16_VALUES[chars[0x16]] << 36;
			lsb |= BASE_16_VALUES[chars[0x17]] << 32;
			lsb |= BASE_16_VALUES[chars[0x18]] << 28;
			lsb |= BASE_16_VALUES[chars[0x19]] << 24;
			lsb |= BASE_16_VALUES[chars[0x1a]] << 20;
			lsb |= BASE_16_VALUES[chars[0x1b]] << 16;
			lsb |= BASE_16_VALUES[chars[0x1c]] << 12;
			lsb |= BASE_16_VALUES[chars[0x1d]] << 8;
			lsb |= BASE_16_VALUES[chars[0x1e]] << 4;
			lsb |= BASE_16_VALUES[chars[0x1f]];
		} else {
			// UUID string WITH hyphen
			msb |= BASE_16_VALUES[chars[0x00]] << 60;
			msb |= BASE_16_VALUES[chars[0x01]] << 56;
			msb |= BASE_16_VALUES[chars[0x02]] << 52;
			msb |= BASE_16_VALUES[chars[0x03]] << 48;
			msb |= BASE_16_VALUES[chars[0x04]] << 44;
			msb |= BASE_16_VALUES[chars[0x05]] << 40;
			msb |= BASE_16_VALUES[chars[0x06]] << 36;
			msb |= BASE_16_VALUES[chars[0x07]] << 32;
			// input[8] = '-'
			msb |= BASE_16_VALUES[chars[0x09]] << 28;
			msb |= BASE_16_VALUES[chars[0x0a]] << 24;
			msb |= BASE_16_VALUES[chars[0x0b]] << 20;
			msb |= BASE_16_VALUES[chars[0x0c]] << 16;
			// input[13] = '-'
			msb |= BASE_16_VALUES[chars[0x0e]] << 12;
			msb |= BASE_16_VALUES[chars[0x0f]] << 8;
			msb |= BASE_16_VALUES[chars[0x10]] << 4;
			msb |= BASE_16_VALUES[chars[0x11]];
			// input[18] = '-'
			lsb |= BASE_16_VALUES[chars[0x13]] << 60;
			lsb |= BASE_16_VALUES[chars[0x14]] << 56;
			lsb |= BASE_16_VALUES[chars[0x15]] << 52;
			lsb |= BASE_16_VALUES[chars[0x16]] << 48;
			// input[23] = '-'
			lsb |= BASE_16_VALUES[chars[0x18]] << 44;
			lsb |= BASE_16_VALUES[chars[0x19]] << 40;
			lsb |= BASE_16_VALUES[chars[0x1a]] << 36;
			lsb |= BASE_16_VALUES[chars[0x1b]] << 32;
			lsb |= BASE_16_VALUES[chars[0x1c]] << 28;
			lsb |= BASE_16_VALUES[chars[0x1d]] << 24;
			lsb |= BASE_16_VALUES[chars[0x1e]] << 20;
			lsb |= BASE_16_VALUES[chars[0x1f]] << 16;
			lsb |= BASE_16_VALUES[chars[0x20]] << 12;
			lsb |= BASE_16_VALUES[chars[0x21]] << 8;
			lsb |= BASE_16_VALUES[chars[0x22]] << 4;
			lsb |= BASE_16_VALUES[chars[0x23]];
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
