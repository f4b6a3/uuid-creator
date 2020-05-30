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

package com.github.f4b6a3.uuid.creator.nonstandard;

import java.security.SecureRandom;
import java.util.UUID;

import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.RandomUtil;
import com.github.f4b6a3.uuid.creator.AbstractRandomBasedUuidCreator;
import com.github.f4b6a3.uuid.enums.UuidVersion;

/**
 * Factory that creates Suffix COMB GUIDs.
 * 
 * A Suffix COMB GUID is a UUID that combines a creation time with random bits.
 * 
 * The creation millisecond is a 6 bytes SUFFIX at the LEAST significant bits.
 * 
 * Read: The Cost of GUIDs as Primary Keys
 * http://www.informit.com/articles/article.aspx?p=25862
 * 
 */
public class SuffixCombCreator extends AbstractRandomBasedUuidCreator {

	public SuffixCombCreator() {
		super(UuidVersion.VERSION_RANDOM_BASED);
	}

	/**
	 * Return a Suffix COMB GUID.
	 * 
	 * It combines a creation time with random bits.
	 * 
	 * The creation millisecond is a SUFFIX at the LEAST significant bits.
	 */
	@Override
	public synchronized UUID create() {

		final long timestamp = System.currentTimeMillis();

		long msb;
		long lsb;

		// Get random values for MSB and LSB
		if (random == null) {
			final byte[] bytes = new byte[10];
			RandomUtil.get().nextBytes(bytes);
			msb = ByteUtil.toNumber(bytes, 0, 8);
			lsb = (bytes[8] << 8) | (bytes[9] & 0xff);
		} else {
			if (this.random instanceof SecureRandom) {
				final byte[] bytes = new byte[10];
				this.random.nextBytes(bytes);
				msb = ByteUtil.toNumber(bytes, 0, 8);
				lsb = (bytes[8] << 8) | (bytes[9] & 0xff);
			} else {
				msb = this.random.nextLong();
				lsb = this.random.nextLong();
			}
		}

		// Insert the suffix in the LSB
		lsb = (lsb << 48) | (timestamp & 0x0000ffffffffffffL);

		// Set the version and variant bits
		return new UUID(applyVersionBits(msb), applyVariantBits(lsb));
	}
}
