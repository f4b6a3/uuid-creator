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

package com.github.f4b6a3.uuid.codec;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.base.Base16Codec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidValidator;
import com.github.f4b6a3.uuid.util.immutable.ByteArray;
import com.github.f4b6a3.uuid.util.immutable.CharArray;
import com.github.f4b6a3.uuid.util.internal.JavaVersionUtil;

/**
 * Codec for UUID string representation as defined in RFC-4122.
 * <p>
 * The string representation, also referred as canonical textual representation,
 * is a string of 32 hexadecimal (base-16) digits, displayed in five groups
 * separated by hyphens, in the form 8-4-4-4-12 for a total of 36 characters (32
 * hexadecimal characters and 4 hyphens).
 * <p>
 * This codec decodes (parses) strings in these formats, with/without hyphens:
 * <ul>
 * <li>00000000-0000-V000-0000-000000000000 (canonical string)
 * <li>{00000000-0000-V000-0000-000000000000} (MS GUID string)
 * <li>urn:uuid:00000000-0000-V000-0000-000000000000 (URN UUID string)
 * </ul>
 * <p>
 * The encoding and decoding processes can be much faster (7x) than
 * {@link UUID#toString()} and {@link UUID#fromString(String)} in JDK 8.
 * <p>
 * If you prefer a string representation without hyphens, use
 * {@link Base16Codec} instead of {@link StringCodec}. {@link Base16Codec} can
 * be much faster (22x) than doing
 * <code>uuid.toString().replaceAll("-", "")</code>.
 */
public class StringCodec implements UuidCodec<String> {

	/**
	 * A shared immutable instance.
	 */
	public static final StringCodec INSTANCE = new StringCodec();

	private static final ByteArray MAP = Base16Codec.INSTANCE.getBase().getMap();
	private static final CharArray ALPHABET = Base16Codec.INSTANCE.getBase().getAlphabet();

	private static final String URN_PREFIX = "urn:uuid:";
	private static final boolean JAVA_VERSION_GREATER_THAN_8 = JavaVersionUtil.getJavaVersion() > 8;

	private static final int WITH_DASH_UUID_LENGTH = 36;
	private static final int WITHOUT_DASH_UUID_LENGTH = 32;
	private static final int URN_PREFIX_UUID_LENGTH = 45;
	private static final int CURLY_BRACES_UUID_LENGTH = 38;

	private static final int DASH_POSITION_1 = 8;
	private static final int DASH_POSITION_2 = 13;
	private static final int DASH_POSITION_3 = 18;
	private static final int DASH_POSITION_4 = 23;

	/**
	 * Get a string from a UUID.
	 * <p>
	 * It can be much faster than {@link UUID#toString()} in JDK 8.
	 * 
	 * @param uuid a UUID
	 * @return a UUID string
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public String encode(UUID uuid) {

		UuidValidator.validate(uuid);

		if (JAVA_VERSION_GREATER_THAN_8) {
			return uuid.toString();
		}

		final char[] chars = new char[36];
		final long msb = uuid.getMostSignificantBits();
		final long lsb = uuid.getLeastSignificantBits();

		chars[0x00] = ALPHABET.get((int) (msb >>> 0x3c & 0xf));
		chars[0x01] = ALPHABET.get((int) (msb >>> 0x38 & 0xf));
		chars[0x02] = ALPHABET.get((int) (msb >>> 0x34 & 0xf));
		chars[0x03] = ALPHABET.get((int) (msb >>> 0x30 & 0xf));
		chars[0x04] = ALPHABET.get((int) (msb >>> 0x2c & 0xf));
		chars[0x05] = ALPHABET.get((int) (msb >>> 0x28 & 0xf));
		chars[0x06] = ALPHABET.get((int) (msb >>> 0x24 & 0xf));
		chars[0x07] = ALPHABET.get((int) (msb >>> 0x20 & 0xf));
		chars[0x08] = '-'; // 8
		chars[0x09] = ALPHABET.get((int) (msb >>> 0x1c & 0xf));
		chars[0x0a] = ALPHABET.get((int) (msb >>> 0x18 & 0xf));
		chars[0x0b] = ALPHABET.get((int) (msb >>> 0x14 & 0xf));
		chars[0x0c] = ALPHABET.get((int) (msb >>> 0x10 & 0xf));
		chars[0x0d] = '-'; // 13
		chars[0x0e] = ALPHABET.get((int) (msb >>> 0x0c & 0xf));
		chars[0x0f] = ALPHABET.get((int) (msb >>> 0x08 & 0xf));
		chars[0x10] = ALPHABET.get((int) (msb >>> 0x04 & 0xf));
		chars[0x11] = ALPHABET.get((int) (msb & 0xf));
		chars[0x12] = '-'; // 18
		chars[0x13] = ALPHABET.get((int) (lsb >>> 0x3c & 0xf));
		chars[0x14] = ALPHABET.get((int) (lsb >>> 0x38 & 0xf));
		chars[0x15] = ALPHABET.get((int) (lsb >>> 0x34 & 0xf));
		chars[0x16] = ALPHABET.get((int) (lsb >>> 0x30 & 0xf));
		chars[0x17] = '-'; // 23
		chars[0x18] = ALPHABET.get((int) (lsb >>> 0x2c & 0xf));
		chars[0x19] = ALPHABET.get((int) (lsb >>> 0x28 & 0xf));
		chars[0x1a] = ALPHABET.get((int) (lsb >>> 0x24 & 0xf));
		chars[0x1b] = ALPHABET.get((int) (lsb >>> 0x20 & 0xf));
		chars[0x1c] = ALPHABET.get((int) (lsb >>> 0x1c & 0xf));
		chars[0x1d] = ALPHABET.get((int) (lsb >>> 0x18 & 0xf));
		chars[0x1e] = ALPHABET.get((int) (lsb >>> 0x14 & 0xf));
		chars[0x1f] = ALPHABET.get((int) (lsb >>> 0x10 & 0xf));
		chars[0x20] = ALPHABET.get((int) (lsb >>> 0x0c & 0xf));
		chars[0x21] = ALPHABET.get((int) (lsb >>> 0x08 & 0xf));
		chars[0x22] = ALPHABET.get((int) (lsb >>> 0x04 & 0xf));
		chars[0x23] = ALPHABET.get((int) (lsb & 0xf));

		return new String(chars);
	}

	/**
	 * Get a UUID from a string.
	 * <p>
	 * It accepts strings:
	 * <ul>
	 * <li>With URN prefix: "urn:uuid:";
	 * <li>With curly braces: '{' and '}';
	 * <li>With upper or lower case;
	 * <li>With or without hyphens.
	 * </ul>
	 * <p>
	 * It can be much faster than {@link UUID#fromString(String)} in JDK 8.
	 * <p>
	 * It also can be twice as fast as {@link UUID#fromString(String)} in JDK 11.
	 * 
	 * @param string a UUID string
	 * @return a UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public UUID decode(final String string) {

		if (string == null) {
			throw newInvalidUuidException(string);
		}

		final String str = modifyString(string);

		if (str.length() == WITH_DASH_UUID_LENGTH) {
			if (str.charAt(DASH_POSITION_1) != '-' || str.charAt(DASH_POSITION_2) != '-'
					|| str.charAt(DASH_POSITION_3) != '-' || str.charAt(DASH_POSITION_4) != '-') {
				throw newInvalidUuidException(str);
			}
			final long hi1 = parseShort(str, 0x00, 0x01, 0x02, 0x03) << 16 | parseShort(str, 0x04, 0x05, 0x06, 0x07);
			final long hi2 = parseShort(str, 0x09, 0x0a, 0x0b, 0x0c) << 16 | parseShort(str, 0x0e, 0x0f, 0x10, 0x11);
			final long lo1 = parseShort(str, 0x13, 0x14, 0x15, 0x16) << 16 | parseShort(str, 0x18, 0x19, 0x1a, 0x1b);
			final long lo2 = parseShort(str, 0x1c, 0x1d, 0x1e, 0x1f) << 16 | parseShort(str, 0x20, 0x21, 0x22, 0x23);
			return new UUID(hi1 << 32 | hi2, lo1 << 32 | lo2);
		}

		if (str.length() == WITHOUT_DASH_UUID_LENGTH) {
			final long hi1 = parseShort(str, 0x00, 0x01, 0x02, 0x03) << 16 | parseShort(str, 0x04, 0x05, 0x06, 0x07);
			final long hi2 = parseShort(str, 0x08, 0x09, 0x0a, 0x0b) << 16 | parseShort(str, 0x0c, 0x0d, 0x0e, 0x0f);
			final long lo1 = parseShort(str, 0x10, 0x11, 0x12, 0x13) << 16 | parseShort(str, 0x14, 0x15, 0x16, 0x17);
			final long lo2 = parseShort(str, 0x18, 0x19, 0x1a, 0x1b) << 16 | parseShort(str, 0x1c, 0x1d, 0x1e, 0x1f);
			return new UUID(hi1 << 32 | hi2, lo1 << 32 | lo2);
		}

		throw newInvalidUuidException(str);
	}

	private static long parseShort(final String str, final int i1, final int i2, final int i3, final int i4) {

		final char chr1 = str.charAt(i1);
		final char chr2 = str.charAt(i2);
		final char chr3 = str.charAt(i3);
		final char chr4 = str.charAt(i4);

		if (chr1 > 0xff || chr2 > 0xff || chr3 > 0xff || chr4 > 0xff) {
			throw newInvalidUuidException(str);
		}

		final int val1 = MAP.get(chr1);
		final int val2 = MAP.get(chr2);
		final int val3 = MAP.get(chr3);
		final int val4 = MAP.get(chr4);

		if (val1 == -1 || val2 == -1 || val3 == -1 || val4 == -1) {
			throw newInvalidUuidException(str);
		}

		return (long) (val1 << 12 | val2 << 8 | val3 << 4 | val4);
	}

	private static RuntimeException newInvalidUuidException(final String str) {
		return new InvalidUuidException("Invalid UUID: " + str);
	}

	/**
	 * Returns a modified string without URN prefix and curly braces.
	 * <p>
	 * It removes URN prefix and curly braces from the original string.
	 * 
	 * @param string a string
	 * @return a substring
	 */
	protected static String modifyString(String string) {

		// UUID URN format: "urn:uuid:00000000-0000-0000-0000-000000000000"
		if (string.length() == URN_PREFIX_UUID_LENGTH && string.startsWith(URN_PREFIX)) {
			// Remove the URN prefix: "urn:uuid:"
			return string.substring(URN_PREFIX.length());
		}

		// Curly braces format: "{00000000-0000-0000-0000-000000000000}"
		if (string.length() == CURLY_BRACES_UUID_LENGTH && string.startsWith("{") && string.endsWith("}")) {
			// Remove curly braces: '{' and '}'
			return string.substring(1, string.length() - 1);
		}

		return string;
	}
}
