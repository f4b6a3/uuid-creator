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

package com.github.f4b6a3.uuid.creator;

import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVersion;

/**
 * Abstract class for {@link UUID} creators.
 */
public abstract class AbstractUuidCreator {

	protected final int version;
	protected final long versionBits;

	protected AbstractUuidCreator() {
		this.version = 0;
		this.versionBits = 0;
	}

	public AbstractUuidCreator(int version) {
		if (version < 0x00000000 || version > 0x0000000f) {
			throw new IllegalArgumentException("Invalid UUID version");
		}
		this.version = version;
		this.versionBits = version << 12;
	}

	public AbstractUuidCreator(UuidVersion version) {
		this(version.getValue());
	}

	/**
	 * Returns the version number for this creator.
	 * 
	 * @return the version number
	 */
	public int getVersion() {
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
	protected UUID getUuid(byte[] bytes) {

		long msb = 0;
		long lsb = 0;

		msb = (bytes[0] & 0xff);
		msb = (bytes[1] & 0xff) | (msb << 8);
		msb = (bytes[2] & 0xff) | (msb << 8);
		msb = (bytes[3] & 0xff) | (msb << 8);
		msb = (bytes[4] & 0xff) | (msb << 8);
		msb = (bytes[5] & 0xff) | (msb << 8);
		msb = (bytes[6] & 0xff) | (msb << 8);
		msb = (bytes[7] & 0xff) | (msb << 8);

		lsb = (bytes[8] & 0xff);
		lsb = (bytes[9] & 0xff) | (lsb << 8);
		lsb = (bytes[10] & 0xff) | (lsb << 8);
		lsb = (bytes[11] & 0xff) | (lsb << 8);
		lsb = (bytes[12] & 0xff) | (lsb << 8);
		lsb = (bytes[13] & 0xff) | (lsb << 8);
		lsb = (bytes[14] & 0xff) | (lsb << 8);
		lsb = (bytes[15] & 0xff) | (lsb << 8);

		msb = (msb & 0xffffffffffff0fffL) | this.versionBits; // set version
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
	protected UUID getUuid(long msb, long lsb) {
		msb = (msb & 0xffffffffffff0fffL) | this.versionBits; // set version
		lsb = (lsb & 0x3fffffffffffffffL) | 0x8000000000000000L; // set variant
		return new UUID(msb, lsb);
	}
}
