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

package com.github.f4b6a3.uuid.util.immutable;

import java.util.Arrays;

/**
 * Immutable array of bytes.
 */
public final class ByteArray {

	private final byte[] array;

	private ByteArray(byte[] a) {
		array = Arrays.copyOf(a, a.length);
	}

	/**
	 * Creates an instance of this class.
	 * 
	 * @param a an array of bytes
	 * @return a new instance
	 */
	public static ByteArray from(byte[] a) {
		return new ByteArray(a);
	}

	/**
	 * Return the byte at a position.
	 * 
	 * @param index the position
	 * @return a byte
	 */
	public byte get(int index) {
		return array[index];
	}

	/**
	 * Returns the array length
	 * 
	 * @return the length
	 */
	public int length() {
		return this.array.length;
	}

	/**
	 * Returns copy of the array.
	 * 
	 * @return an array of bytes
	 */
	public byte[] array() {
		return Arrays.copyOf(array, array.length);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(array);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ByteArray other = (ByteArray) obj;
		return Arrays.equals(array, other.array);
	}

	@Override
	public String toString() {
		return "ByteArray [array=" + Arrays.toString(array) + "]";
	}
}
