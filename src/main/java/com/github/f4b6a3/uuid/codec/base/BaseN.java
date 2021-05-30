/*
 * MIT License
 * 
 * Copyright (c) 2018-2021 Fabio Lima
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

package com.github.f4b6a3.uuid.codec.base;

import com.github.f4b6a3.uuid.exception.UuidCodecException;
import com.github.f4b6a3.uuid.util.internal.immutable.CharArray;
import com.github.f4b6a3.uuid.util.internal.immutable.LongArray;

/**
 * Class that represents the base-n encodings.
 */
public final class BaseN {

	public static final BaseN BASE_16 = new BaseN("0-9a-f");
	public static final BaseN BASE_32 = new BaseN("a-z2-7");
	public static final BaseN BASE_32_HEX = new BaseN("0-9a-v");
	public static final BaseN BASE_32_CROCKFORD = new BaseN("0123456789abcdefghjkmnpqrstvwxyz");
	public static final BaseN BASE_36 = new BaseN("0-9a-z");
	public static final BaseN BASE_58 = new BaseN("0-9A-Za-v");
	public static final BaseN BASE_58_BITCOIN = new BaseN("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");
	public static final BaseN BASE_58_FLICKR = new BaseN("123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ");
	public static final BaseN BASE_62 = new BaseN("0-9A-Za-z");
	public static final BaseN BASE_64 = new BaseN("A-Za-z0-9+/");
	public static final BaseN BASE_64_URL = new BaseN("A-Za-z0-9-_");

	private int radix;
	private int length;
	private boolean sensitive;
	private char padding;

	// alphabet chars
	private final CharArray alphabet;

	// ASCII map
	// each char is mapped to a value
	private final LongArray map;

	protected static final int RADIX_MIN = 2;
	protected static final int RADIX_MAX = 64;

	private static final int BIT_LENGTH = 128;

	/**
	 * Public constructor for the base-n object.
	 * 
	 * The radix is the alphabet size.
	 * 
	 * The supported alphabet sizes are from 2 to 64.
	 * 
	 * If there are mixed cases in the alphabet, the base-n is case SENSITIVE.
	 * 
	 * The encoded string length is equal to `CEIL(128 / LOG2(n))`, where n is the
	 * radix. The encoded string is padded to fit the expected length.
	 * 
	 * The padding character is the first character of the string. For example, the
	 * padding character for the alphabet "abcdef0123456" is 'a'.
	 * 
	 * The example below shows how to create a {@link BaseN} for an hypothetical
	 * base-26 encoding that contains only letters. You only need to pass a number
	 * 40.
	 * 
	 * <pre>
	 * String radix = 40;
	 * BaseN base = new BaseN(radix);
	 * </pre>
	 * 
	 * If radix is greater than 36, the alphabet generated is a subset of the
	 * character sequence "0-9A-Za-z-_". Otherwise it is a subset of "0-9a-z". In
	 * the example above the resulting alphabet is
	 * "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcd" (0-9A-Za-d).
	 * 
	 * @param radix the radix to be used
	 */
	public BaseN(int radix) {
		this(alphabet(radix));
	}

	/**
	 * Public constructor for the base-n object.
	 * 
	 * The radix is the alphabet size.
	 * 
	 * The supported alphabet sizes are from 2 to 64.
	 * 
	 * If there are mixed cases in the alphabet, the base-n is case SENSITIVE.
	 * 
	 * The encoded string length is equal to `CEIL(128 / LOG2(n))`, where n is the
	 * radix. The encoded string is padded to fit the expected length.
	 * 
	 * The padding character is the first character of the string. For example, the
	 * padding character for the alphabet "abcdef0123456" is 'a'.
	 * 
	 * The example below shows how to create a {@link BaseN} for an hypothetical
	 * base-26 encoding that contains only letters. You only need to pass a string
	 * with 26 characters.
	 * 
	 * <pre>
	 * String alphabet = "abcdefghijklmnopqrstuvwxyz";
	 * BaseN base = new BaseN(alphabet);
	 * </pre>
	 * 
	 * Alphabet strings similar to "a-f0-9" are expanded to "abcdef0123456789". The
	 * same example using the string "a-z" instead of "abcdefghijklmnopqrstuvwxyz":
	 * 
	 * <pre>
	 * String alphabet = "a-z";
	 * BaseN base = new BaseN(alphabet);
	 * </pre>
	 * 
	 * @param alphabet the alphabet to be used
	 */
	public BaseN(String alphabet) {

		// expand the alphabet
		String charset = expand(alphabet);
		this.alphabet = CharArray.from(charset.toCharArray());

		// set the radix field
		this.radix = charset.length();
		if (this.radix < RADIX_MIN || this.radix > RADIX_MAX) {
			throw new UuidCodecException("Unsupported radix: " + this.radix);
		}

		// set the length field
		this.length = (int) Math.ceil(BIT_LENGTH / (Math.log(this.radix) / Math.log(2)));

		// set the sensitive field
		this.sensitive = sensitive(charset);

		// set the padding field
		this.padding = charset.charAt(0);

		// set the map LongArray
		this.map = map(charset, sensitive);
	}

	public int getRadix() {
		return radix;
	}

	public int getLength() {
		return length;
	}

	public boolean isSensitive() {
		return sensitive;
	}

	public char getPadding() {
		return padding;
	}

	public CharArray getAlphabet() {
		return this.alphabet;
	}

	public LongArray getMap() {
		return this.map;
	}

	public boolean isValid(String string) {
		return string != null && isValid(string.toCharArray());
	}

	public boolean isValid(char[] chars) {
		if (chars == null || chars.length != this.length) {
			return false;
		}
		for (int i = 0; i < chars.length; i++) {
			if (this.map.get(chars[i]) == -1) {
				return false;
			}
		}
		return true;
	}

	private static boolean sensitive(String charset) {
		String lowercase = charset.toLowerCase();
		String uppercase = charset.toUpperCase();
		return !(charset.equals(lowercase) || charset.equals(uppercase));
	}

	private static String alphabet(int radix) {

		String string;
		if (radix > 36) {
			string = expand("0-9A-Za-z-_"); // 64 chars
		} else {
			string = expand("0-9a-z"); // 36 chars
		}

		// get the first 'n' characters
		return string.substring(0, radix);
	}

	private static LongArray map(String alphabet, boolean sensitive) {
		// initialize the map with -1
		final long[] mapping = new long[128];
		for (int i = 0; i < mapping.length; i++) {
			mapping[i] = -1;
		}
		// map the alphabets chars to values
		String lowercase = alphabet.toLowerCase();
		String uppercase = alphabet.toUpperCase();
		for (int i = 0; i < alphabet.length(); i++) {
			if (sensitive) {
				mapping[alphabet.charAt(i)] = i;
			} else {
				mapping[lowercase.charAt(i)] = i;
				mapping[uppercase.charAt(i)] = i;
			}
		}
		return LongArray.from(mapping);
	}

	/**
	 * Expands char sequences similar to 0-9, a-z and A-Z.
	 * 
	 * @param string a string to be expanded
	 * @return a string
	 */
	protected static String expand(String string) {

		StringBuilder buffer = new StringBuilder();

		int i = 1;
		while (i <= string.length()) {
			final char a = string.charAt(i - 1); // previous char
			if ((i < string.length() - 1) && (string.charAt(i) == '-')) {
				final char b = string.charAt(i + 1); // next char
				char[] expanded = expand(a, b);
				if (expanded.length != 0) {
					i += 2; // skip
					buffer.append(expanded);
				} else {
					buffer.append(a);
				}
			} else {
				buffer.append(a);
			}
			i++;
		}

		return buffer.toString();
	}

	private static char[] expand(char a, char b) {
		char[] expanded = expand(a, b, '0', '9'); // digits (0-9)
		if (expanded.length == 0) {
			expanded = expand(a, b, 'a', 'z'); // lower case letters (a-z)
		}
		if (expanded.length == 0) {
			expanded = expand(a, b, 'A', 'Z'); // upper case letters (A-Z)
		}
		return expanded;
	}

	private static char[] expand(char a, char b, char min, char max) {

		if ((a > b) || !(a >= min && a <= max && b >= min && b <= max)) {
			return new char[0];
		}

		char[] buffer = new char[(b - a) + 1];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (char) (a + i);
		}
		return buffer;
	}
}