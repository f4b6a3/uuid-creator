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

package com.github.f4b6a3.uuid.util.internal;

/**
 * Utility class that contains many static methods for byte handling.
 */
public final class ByteUtil {

	private ByteUtil() {
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
	 * @param bytes a byte array
	 * @param start first byte of the array
	 * @param end   last byte of the array (exclusive)
	 * @return a long
	 */
	public static long toNumber(final byte[] bytes, final int start, final int end) {
		long result = 0;
		for (int i = start; i < end; i++) {
			result = (result << 8) | (bytes[i] & 0xffL);
		}
		return result;
	}

	/**
	 * Get a hexadecimal string from given array of bytes.
	 *
	 * @param bytes byte array
	 * @return a string
	 */
	public static String toHexadecimal(final byte[] bytes) {

		final char[] chars = new char[bytes.length * 2];
		for (int i = 0, j = 0; i < bytes.length; i++, j += 2) {
			final int v = bytes[i] & 0xff;
			chars[j] = toHexChar(v >>> 4);
			chars[j + 1] = toHexChar(v & 0x0f);
		}
		return new String(chars);
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
	 * Converts an array of bytes into an array of integers. Each integer is formed by combining 4 bytes
	 * from the input array. This method assumes that the input byte array is at least 16 bytes long.
	 * The conversion is done by treating each set of 4 bytes as a single integer, with the first byte being the most significant.
	 *
	 * @param bytes An array of bytes to be converted into integers. This array should be at least 16 bytes long.
	 * @return An array of 4 integers, where each integer is formed by combining 4 bytes from the input array.
	 */
	public static int[] toInts(byte[] bytes) {
		int[] ints = new int[4];
		ints[0] |= (bytes[0x0] & 0xff) << 24;
		ints[0] |= (bytes[0x1] & 0xff) << 16;
		ints[0] |= (bytes[0x2] & 0xff) << 8;
		ints[0] |= (bytes[0x3] & 0xff);
		ints[1] |= (bytes[0x4] & 0xff) << 24;
		ints[1] |= (bytes[0x5] & 0xff) << 16;
		ints[1] |= (bytes[0x6] & 0xff) << 8;
		ints[1] |= (bytes[0x7] & 0xff);
		ints[2] |= (bytes[0x8] & 0xff) << 24;
		ints[2] |= (bytes[0x9] & 0xff) << 16;
		ints[2] |= (bytes[0xa] & 0xff) << 8;
		ints[2] |= (bytes[0xb] & 0xff);
		ints[3] |= (bytes[0xc] & 0xff) << 24;
		ints[3] |= (bytes[0xd] & 0xff) << 16;
		ints[3] |= (bytes[0xe] & 0xff) << 8;
		ints[3] |= (bytes[0xf] & 0xff);
		return ints;
	}

	/**
	 * Converts an array of integers into an array of bytes. Each integer is decomposed into 4 bytes,
	 * with the most significant byte being placed first. This method produces a byte array of length 16,
	 * assuming the input array contains exactly 4 integers. The conversion is performed by shifting
	 * and masking operations to extract each byte from the integers.
	 *
	 * @param ints An array of integers to be converted into bytes. This array should contain exactly 4 integers.
	 * @return A byte array of length 16, where each group of 4 bytes represents one of the integers from the input array.
	 */
	public static byte[] fromInts(int[] ints) {
		byte[] bytes = new byte[16]; 
		bytes[0x0] = (byte) (ints[0] >>> 24);
		bytes[0x1] = (byte) (ints[0] >>> 16);
		bytes[0x2] = (byte) (ints[0] >>> 8);
		bytes[0x3] = (byte) (ints[0]);
		bytes[0x4] = (byte) (ints[1] >>> 24);
		bytes[0x5] = (byte) (ints[1] >>> 16);
		bytes[0x6] = (byte) (ints[1] >>> 8);
		bytes[0x7] = (byte) (ints[1]);
		bytes[0x8] = (byte) (ints[2] >>> 24);
		bytes[0x9] = (byte) (ints[2] >>> 16);
		bytes[0xa] = (byte) (ints[2] >>> 8);
		bytes[0xb] = (byte) (ints[2]);
		bytes[0xc] = (byte) (ints[3] >>> 24);
		bytes[0xd] = (byte) (ints[3] >>> 16);
		bytes[0xe] = (byte) (ints[3] >>> 8);
		bytes[0xf] = (byte) (ints[3]);
		return bytes;
	}
}
