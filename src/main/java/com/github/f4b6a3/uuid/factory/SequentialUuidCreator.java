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

package com.github.f4b6a3.uuid.factory;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.abst.AbstractTimeBasedUuidCreator;
import com.github.f4b6a3.uuid.util.UuidUtil;

/**
 * Factory that creates sequential UUIDs also known as Ordered UUIDs.
 * 
 * RFC-4122 version: 0 (extension).
 * 
 * This version is a 'extension' of the RFC-4122. It leaves the timestamp bits
 * in the 'natural' order, instead of rearranging them as the version 1 does.
 *
 */
public class SequentialUuidCreator extends AbstractTimeBasedUuidCreator {

	public SequentialUuidCreator() {
		this(UuidVersion.SEQUENTIAL);
	}

	protected SequentialUuidCreator(UuidVersion version) {
		super(version);
	}

	/**
	 * 
	 * {@link UuidUtil#formatSequentialMostSignificantBits(long)}
	 * 
	 * @param timestamp
	 *            a timestamp
	 * @return the MSB
	 */
	@Override
	protected long formatMostSignificantBits(final long timestamp) {
		return UuidUtil.formatSequentialMostSignificantBits(timestamp);
	}
}
