/*
 * MIT License
 * 
 * Copyright (c) 2018-2022 Fabio Lima
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

package com.github.f4b6a3.uuid.factory;

import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVersion;

/**
 * Abstract class for {@link UUID} factories.
 */
public abstract class UuidFactory {

	protected final UuidVersion version;
	protected final long versionMask;

	public UuidFactory(UuidVersion version) {
		this.version = version;
		this.versionMask = (long) version.getValue() << 12;
	}

	/**
	 * Returns the version number for this factory.
	 * 
	 * @return the version number
	 */
	public UuidVersion getVersion() {
		return this.version;
	}

	/**
	 * Creates a UUID from a byte array of 16 bytes.
	 * 
	 * It applies the version number to the resulting UUID.
	 * 
	 * @param bytes a byte array
	 * @return a UUID
	 */
	protected UUID getUuid(final byte[] bytes) {

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

		msb = (msb & 0xffffffffffff0fffL) | this.versionMask; // set version
		lsb = (lsb & 0x3fffffffffffffffL) | 0x8000000000000000L; // set variant
		return new UUID(msb, lsb);
	}

	/**
	 * Creates a UUID from a pair of numbers.
	 * 
	 * It applies the version number to the resulting UUID.
	 * 
	 * @param msb the most significant bits
	 * @param lsb the least significant bits
	 * @return a UUID
	 */
	protected UUID getUuid(final long msb, final long lsb) {
		final long msb0 = (msb & 0xffffffffffff0fffL) | this.versionMask; // set version
		final long lsb0 = (lsb & 0x3fffffffffffffffL) | 0x8000000000000000L; // set variant
		return new UUID(msb0, lsb0);
	}
}
