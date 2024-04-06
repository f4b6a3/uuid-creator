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

package com.github.f4b6a3.uuid.util;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * A UUID builder.
 * <p>
 * Usage:
 * 
 * <pre>{@code
 * SecureRandom random = new SecureRandom();
 * UUID uuid = new UuidBuilder(4) // sets version 4 (random-based)
 * 		.put(random.nextLong()) // put the most significant 64 bits
 * 		.put(random.nextLong()) // put the least significant 64 bits
 * 		.build(); // return the built UUID
 * }</pre>
 */
public class UuidBuilder {

	private Integer version;

	// newly-created byte buffers are always BIG_ENDIAN
	private ByteBuffer buffer = ByteBuffer.allocate(16);

	/**
	 * Instantiates a new builder without a version number.
	 * 
	 */
	public UuidBuilder() {
		this.version = null;
	}

	/**
	 * Instantiates a new builder with a version number.
	 * 
	 * @param version a value between 0 and 15
	 */
	public UuidBuilder(int version) {
		if (version < 0x00L || version > 0xfL) {
			throw new IllegalArgumentException("Invalid version number");
		}
		this.version = version;
	}

	/**
	 * Puts 8 bytes containing the given long.
	 *
	 * @param value a long value
	 *
	 * @return This buffer
	 *
	 * @throws BufferOverflowException If there are fewer than 8 bytes remaining
	 */
	public synchronized UuidBuilder put(long value) {
		buffer.putLong(value);
		return this;
	}

	/**
	 * Puts 4 bytes containing the given int.
	 *
	 * @param value an int value
	 *
	 * @return This buffer
	 *
	 * @throws BufferOverflowException If there are fewer than 4 bytes remaining
	 */
	public synchronized UuidBuilder put(int value) {
		buffer.putInt(value);
		return this;
	}

	/**
	 * Puts 2 bytes containing the given short.
	 *
	 * @param value a short value
	 *
	 * @return This buffer
	 *
	 * @throws BufferOverflowException If there are fewer than 2 bytes remaining
	 */
	public synchronized UuidBuilder put(short value) {
		buffer.putShort(value);
		return this;
	}

	/**
	 * Puts the given byte.
	 *
	 * @param value a byte value
	 *
	 * @return This buffer
	 *
	 * @throws BufferOverflowException If there are fewer than 1 bytes remaining
	 */
	public synchronized UuidBuilder put(byte value) {
		buffer.put(value);
		return this;
	}

	/**
	 * Puts the given byte array.
	 *
	 * @param value a byte array
	 *
	 * @return This buffer
	 *
	 * @throws BufferOverflowException If there are fewer bytes remaining than the
	 *                                 array length
	 */
	public synchronized UuidBuilder put(byte[] array) {
		buffer.put(array);
		return this;
	}

	/**
	 * Builds a UUID after all 16 bytes are filled.
	 * <p>
	 * This method ends the use of a builder.
	 * <p>
	 * Successive calls will always return the same UUID value.
	 * <p>
	 * Note: this method overrides bits 48 through 51 (version field) and bits 52
	 * through 63 (variant field), 6 bits total, to comply the UUID specification.
	 * 
	 * @throws BufferUnderflowException If there are bytes remaining to be filled
	 */
	public synchronized UUID build() {

		validate();
		buffer.rewind();

		if (this.version != null) {
			// set the 4 most significant bits of the 7th byte (version field)
			final long msb = (buffer.getLong() & 0xffff_ffff_ffff_0fffL) | (version & 0xfL) << 12;
			// set the 2 most significant bits of the 9th byte to 1 and 0 (variant field)
			final long lsb = (buffer.getLong() & 0x3fff_ffff_ffff_ffffL) | 0x8000_0000_0000_0000L;
			return new UUID(msb, lsb);
		}

		final long msb = buffer.getLong();
		final long lsb = buffer.getLong();
		return new UUID(msb, lsb);
	}

	private synchronized void validate() {
		if (buffer.hasRemaining()) {
			throw new BufferUnderflowException();
		}
	}
}
