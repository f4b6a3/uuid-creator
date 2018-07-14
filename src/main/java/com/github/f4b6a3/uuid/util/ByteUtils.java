/**
 * Copyright 2018 Fabio Lima <br/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); <br/>
 * you may not use this file except in compliance with the License. <br/>
 * You may obtain a copy of the License at <br/>
 *
 * http://www.apache.org/licenses/LICENSE-2.0 <br/>
 *
 * Unless required by applicable law or agreed to in writing, software <br/>
 * distributed under the License is distributed on an "AS IS" BASIS, <br/>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br/>
 * See the License for the specific language governing permissions and <br/>
 * limitations under the License. <br/>
 *
 */

package com.github.f4b6a3.uuid.util;

/**
 * Class that contains many static methods for byte handling.
 * 
 * @author fabiolimace
 *
 */
public class ByteUtils {
	
	/**
	 * Get a number from a given hexadecimal string.
	 *
	 * @param hexadecimal
	 * @return
	 */
	public static long toNumber(String hexadecimal) {
		return toNumber(toBytes(hexadecimal));
	}
	
	/**
	 * Get a number from a given array of bytes.
	 * 
	 * @param bytes
	 * @return
	 */
	public static long toNumber(byte[] bytes) {
		long result = 0;
		for (int i = 0; i < bytes.length; i++) {
			result = (result << 8) | (bytes[i] & 0xff);
		}
		return result;
	}

	/**
	 * Get an array of bytes from a given number.
	 *
	 * @param number
	 * @return
	 */
	public static byte[] toBytes(long number) {
		return toBytes(number, 8);
	}
	
	/**
	 * Get an array of bytes from a given number.
	 *
	 * @param number
	 * @return
	 */
	public static byte[] toBytes(long number, int size) {
		byte[] bytes = new byte[size];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) (number >>> (8 * ((bytes.length - 1) - i)));
		}
		return bytes;
	}
	
	/**
	 * Get an array of bytes from a given hexadecimal string.
	 *
	 * @param hexadecimal
	 * @return
	 */
	public static byte[] toBytes(String hexadecimal) {
		int len = hexadecimal.length();
		byte[] bytes = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			bytes[i / 2] = (byte) ((fromHexChar(hexadecimal.charAt(i)) << 4) | fromHexChar(hexadecimal.charAt(i + 1)));
		}
		return bytes;
	}
	
	/**
	 * Get a hexadecimal string from given array of bytes.
	 *
	 * @param bytes
	 * @return
	 */
	public static String toHexadecimal(byte[] bytes) {
		char[] hexadecimal = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xFF;
			hexadecimal[i * 2] = toHexChar(v >>> 4);
			hexadecimal[(i * 2) + 1] = toHexChar(v & 0x0F);
		}
		return new String(hexadecimal);
	}
	
	/**
	 * Get a number value from a hexadecimal char.
	 * 
	 * @param chr
	 * @return
	 */
	public static int fromHexChar(char chr) {
		
		if (chr >= 0x61 && chr <= 0x66) {
			// ASCII codes from 'a' to 'f'
			return (int) chr - 0x57;
		} else if (chr >= 0x41 && chr <= 0x46) {
			// ASCII codes from 'A' to 'F'
			return (int) chr - 0x37;
		} else if(chr >= 0x30 && chr <= 0x39) {
			// ASCII codes from 0 to 9
			return (int) chr - 0x30;
		}

		return 0;
	}
	
	/**
	 * Get a hexadecimal from a number value.
	 * 
	 * @param number
	 * @return
	 */
	public static char toHexChar(int number) {

		if (number >= 0x0a && number <= 0x0f) {
			// ASCII codes from 'a' to 'f'
			return (char) (0x57 + number);
		} else if (number >= 0x00 && number <= 0x09) {
			// ASCII codes from 0 to 9
			return (char) (0x30 + number);
		}

		return 0;
	}
	
	/**
	 * Get a new array with a specific length and filled with a byte value.
	 *
	 * @param length
	 * @param value
	 * @return
	 */
	public static byte[] array(int length, byte value) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = value;
		}
		return result;
	}

	/**
	 * Copy an entire array.
	 *
	 * @param bytes
	 * @return
	 */
	public static byte[] copy(final byte[] bytes) {
		byte[] result = copy(bytes, 0, bytes.length);
		return result;
	}

	/**
	 * Copy part of an array.
	 *
	 * @param bytes
	 * @param start
	 * @param end
	 * @return
	 */
	public static byte[] copy(byte[] bytes, int start, int end) {

		byte[] result = new byte[end - start];
		for (int i = 0; i < result.length; i++) {
			result[i] = bytes[start + i];
		}
		return result;
	}
	
	/**
	 * Concatenates two byte arrays.
	 * 
	 * @param bytes1
	 * @param bytes2
	 * @return
	 */
	public static byte[] concat(byte[] bytes1, byte[] bytes2) {
		
		int length = bytes1.length + bytes2.length;
		byte[] result = new byte[length];

		for (int i = 0; i < bytes1.length; i++) {
			result[i] = bytes1[i];
		}
		for (int j = 0; j < bytes2.length; j++) {
			result[bytes1.length + j] = bytes2[j];
		}
		return result;
	}
	
	/**
	 * Replace part of an array of bytes with another subarray of bytes and
	 * starting from a given index.
	 *
	 * @param bytes
	 * @param replacement
	 * @param index
	 * @return
	 */
	public static byte[] replace(final byte[] bytes, final byte[] replacement, int index) {

		byte[] result = new byte[bytes.length];
		
		for(int i = 0; i < index; i++) {
			result[i] = bytes[i];
		}
		
		for (int i = 0; i < replacement.length; i++) {
			result[index + i] = replacement[i];
		}
		return result;
	}

	/**
	 * Check if two arrays of bytes are equal.
	 *
	 * @param bytes1
	 * @param bytes2
	 * @return
	 */
	public static boolean equalArrays(byte[] bytes1, byte[] bytes2) {
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
