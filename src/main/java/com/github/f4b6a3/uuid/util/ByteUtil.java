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

/**
 * Class that contains many static methods for byte handling.
 */
public final class ByteUtil {

	private ByteUtil() {
	}

	/**
	 * Get a number from a given hexadecimal string.
	 *
	 * @param hexadecimal a string
	 * @return a long
	 */
	public static long toNumber(final String hexadecimal) {
		return toNumber(toBytes(hexadecimal));
	}

	/**
	 * Get a number from a given hexadecimal char array.
	 *
	 * @param hexadecimal a string
	 * @return a long
	 */
	public static long toNumber(final char[] hexadecimal) {
		return toNumber(toBytes(hexadecimal));
	}

	/**
	 * Get a number from a given array of bytes.
	 * 
	 * @param bytes a byte array
	 * @return a long
	 */
	public static long toNumber(final byte[] bytes) {
		return toNumber(bytes, 0, bytes.length);
	}

	/**
	 * Get a number from a given array of bytes.
	 * 
	 * @param bytes  a byte array
	 * @param start  first byte of the number
	 * @param length byte length of the number
	 * @return a long
	 */
	public static long toNumber(final byte[] bytes, final int start, final int length) {
		long result = 0;
		for (int i = start; i < length; i++) {
			result = (result << 8) | (bytes[i] & 0xff);
		}
		return result;
	}

	/**
	 * Get an array of bytes from a given number.
	 *
	 * @param number a number
	 * @return a byte array
	 */
	public static byte[] toBytes(final long number) {
		return new byte[] {
			(byte) (number >>> 56),
			(byte) (number >>> 48),
			(byte) (number >>> 40),
			(byte) (number >>> 32),
			(byte) (number >>> 24),
			(byte) (number >>> 16),
			(byte) (number >>> 8),
			(byte) (number)
		};
	}

	/**
	 * Get an array of bytes from a given array of numbers.
	 *
	 * @param numbers an array of numbers
	 * @return a byte array
	 */
	public static byte[] toBytes(final long... numbers) {
		byte[] bytes = new byte[numbers.length * 8];
		for (int i = 0, j = 0; i < numbers.length; i++, j += 8) {
			System.arraycopy(toBytes(numbers[i]), 0, bytes, j, 8);
		}
		return bytes;
	}

	/**
	 * Get an array of bytes from a given hexadecimal string.
	 *
	 * @param hexadecimal a string
	 * @return a byte array
	 */
	public static byte[] toBytes(final String hexadecimal) {
		return toBytes(hexadecimal.toCharArray());
	}

	/**
	 * Get an array of bytes from a given hexadecimal char array.
	 *
	 * @param hexadecimal a string
	 * @return a byte array
	 */
	public static byte[] toBytes(final char[] hexadecimal) {
		byte[] bytes = new byte[hexadecimal.length / 2];
		for (int i = 0, j = 0; i < bytes.length; i++, j += 2) {
			bytes[i] = (byte) ((fromHexChar(hexadecimal[j]) << 4) | fromHexChar(hexadecimal[j + 1]));
		}
		return bytes;
	}

	/**
	 * Get a hexadecimal string from given array of bytes.
	 *
	 * @param bytes byte array
	 * @return a string
	 */
	public static String toHexadecimal(final byte[] bytes) {
		return new String(toHexadecimalChars(bytes));
	}

	/**
	 * Get a hexadecimal char array from given array of bytes.
	 *
	 * @param bytes byte array
	 * @return a char array
	 */
	public static char[] toHexadecimalChars(final byte[] bytes) {
		final char[] chars = new char[bytes.length * 2];
		for (int i = 0, j = 0; i < bytes.length; i++, j += 2) {
			final int v = bytes[i] & 0xFF;
			chars[j] = toHexChar(v >>> 4);
			chars[j + 1] = toHexChar(v & 0x0F);
		}
		return chars;
	}

	/**
	 * Get a hexadecimal char array from given array of numbers.
	 *
	 * @param numbers an array of numbers
	 * @return a char array
	 */
	public static char[] toHexadecimalChars(final long... numbers) {
		return toHexadecimalChars(toBytes(numbers));
	}

	/**
	 * Get a hexadecimal string from given number.
	 * 
	 * @param numbers an array of numbers
	 * @return a string
	 */
	public static String toHexadecimal(final long... numbers) {
		return new String(toHexadecimalChars(toBytes(numbers)));
	}

	/**
	 * Get a hexadecimal string from given number.
	 * 
	 * @param number a number
	 * @return a string
	 */
	public static String toHexadecimal(final long number) {
		return new String(toHexadecimalChars(number));
	}

	/**
	 * Get a number value from a hexadecimal char.
	 * 
	 * @param chr a character
	 * @return an integer
	 */
	private static int fromHexChar(final char chr) {
		final int c = chr;
		if (c >= 0x30 && c <= 0x39) {
			// ASCII codes from 0 to 9
			return c - 0x30;
		} else if (c >= 0x61 && c <= 0x66) {
			// ASCII codes from 'a' to 'f'
			return c - 0x57;
		} else if (c >= 0x41 && c <= 0x46) {
			// ASCII codes from 'A' to 'F'
			return c - 0x37;
		}

		return 0;
	}

	/**
	 * Get a hexadecimal from a number value.
	 * 
	 * @param number a number
	 * @return a char
	 */
	private static char toHexChar(final int number) {
		if (number >= 0x00 && number <= 0x09) {
			// ASCII codes from 0 to 9
			return (char) (0x30 + number);
		} else if (number >= 0x0a && number <= 0x0f) {
			// ASCII codes from 'a' to 'f'
			return (char) (0x57 + number);
		}
		return 0;
	}

	/**
	 * Get a new array with a specific length and filled with a byte value.
	 *
	 * @param length array size
	 * @param value  byte value
	 * @return a byte array
	 */
	public static byte[] array(final int length, final byte value) {
		final byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = value;
		}
		return result;
	}

	/**
	 * Copy an entire array.
	 *
	 * @param bytes byte array
	 * @return a byte array
	 */
	public static byte[] copy(final byte[] bytes) {
		return copy(bytes, 0, bytes.length);
	}

	/**
	 * Copy part of an array.
	 *
	 * @param bytes byte array
	 * @param start start position
	 * @param end   end position
	 * @return a byte array
	 */
	public static byte[] copy(final byte[] bytes, final int start, final int end) {
		final int length = end - start;
		final byte[] result = new byte[length];
		System.arraycopy(bytes, start, result, 0, length);
		return result;
	}

	/**
	 * Concatenates two byte arrays.
	 * 
	 * @param bytes1 byte array 1
	 * @param bytes2 byte array 2
	 * @return a byte array
	 */
	public static byte[] concat(final byte[] bytes1, final byte[] bytes2) {
		final byte[] result = new byte[bytes1.length + bytes2.length];
		System.arraycopy(bytes1, 0, result, 0, bytes1.length);
		System.arraycopy(bytes2, 0, result, bytes1.length, bytes2.length);
		return result;
	}

	/**
	 * Check if two arrays of bytes are equal.
	 *
	 * @param bytes1 byte array 1
	 * @param bytes2 byte array 2
	 * @return a boolean
	 */
	public static boolean equalArrays(final byte[] bytes1, final byte[] bytes2) {
		if (bytes1.length != bytes2.length) {
			return false;
		}
		for (int i = 0; i < bytes1.length; i++) {
			if (bytes1[i] != bytes2[i]) {
				return false;
			}
		}
		return true;
	}
}
