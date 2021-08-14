/*
 * MIT License
 * 
 * Copyright (c) 2018-2021 Fabio Lima
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

import java.time.Instant;
import java.util.UUID;

/**
 * Utility that provides methods for extracting time from COMBs.
 */
public final class CombUtil {

	private CombUtil() {
	}

	/**
	 * Get the prefix from a Prefix COMB.
	 *
	 * The value returned is equivalent to the number of milliseconds since
	 * 1970-01-01 (Unix epoch).
	 * 
	 * @param comb a Prefix COMB
	 * @return the prefix (the Unix milliseconds)
	 */
	public static long getPrefix(UUID comb) {
		return (comb.getMostSignificantBits() >>> 16);
	}

	/**
	 * Get the suffix from a Suffix COMB.
	 *
	 * The value returned is equivalent to the number of milliseconds since
	 * 1970-01-01 (Unix epoch).
	 * 
	 * @param comb a Suffix COMB
	 * @return the suffix (the Unix milliseconds)
	 */
	public static long getSuffix(UUID comb) {
		return (comb.getLeastSignificantBits() & 0x0000ffffffffffffL);
	}

	/**
	 * Get the instant from a Prefix COMB.
	 *
	 * @param comb a Prefix COMB
	 * @return {@link Instant}
	 */
	public static Instant getPrefixInstant(UUID comb) {
		long milliseconds = getPrefix(comb);
		return Instant.ofEpochMilli(milliseconds);
	}

	/**
	 * Get the instant from a Suffix COMB.
	 *
	 * @param comb a Suffix COMB
	 * @return {@link Instant}
	 */
	public static Instant getSuffixInstant(UUID comb) {
		long milliseconds = getSuffix(comb);
		return Instant.ofEpochMilli(milliseconds);
	}
}
