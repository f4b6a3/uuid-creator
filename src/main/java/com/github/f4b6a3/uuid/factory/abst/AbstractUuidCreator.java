/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
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

package com.github.f4b6a3.uuid.factory.abst;

import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVersion;

/**
 * Abstract class for subclasses that create {@link UUID} objects.
 */
public abstract class AbstractUuidCreator {
	
	protected final int version;
	protected final long versionBits;
	
	protected AbstractUuidCreator() {
		this.version = 0;
		this.versionBits = 0;
	}
	
	public AbstractUuidCreator(int version) {
		this.version = version & 0x0000000F;
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
	 * Returns the variant bits from the "Least Significant Bits".
	 * 
	 * @param lsb the LSB
	 * @return long the variant number bits
	 */
	protected long getVariantBits(final long lsb) {
		return (lsb & 0xc000000000000000L);
	}
	
	/**
	 * Set UUID variant bits into the "Least Significant Bits".
	 * 
	 * @param lsb the LSB
	 * @return the LSB with the correct variant bits
	 */
	protected long setVariantBits(final long lsb) {
		return (lsb & 0x3fffffffffffffffL) | 0x8000000000000000L;
	}
	
	/**
	 * Returns the version bits from the "Most Significant Bits".
	 * 
	 * @param msb the MSB
	 * @return long version number bits
	 */
	protected long getVersionBits(final long msb) {
		return (msb & 0x000000000000f000L);
	}
	
	/**
	 * Set UUID version bits into the "Most Significant Bits".
	 * 
	 * @param msb the MSB
	 * @return the MSB
	 */
	protected long setVersionBits(final long msb) {
		return (msb & 0xffffffffffff0fffL) | this.versionBits;
	}
}
