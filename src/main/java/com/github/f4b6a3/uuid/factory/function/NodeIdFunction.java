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

package com.github.f4b6a3.uuid.factory.function;

import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.util.internal.RandomUtil;

/**
 * Function that must return a number between 0 and 2^48-1.
 * <p>
 * Example:
 * 
 * <pre>{@code
 * // A function that returns new random multicast node identifiers
 * NodeIdFunction f = () -> NodeIdFunction.getMulticastRandom();
 * }</pre>
 * 
 */
@FunctionalInterface
public interface NodeIdFunction extends LongSupplier {

	/**
	 * Returns a new random node identifier.
	 * 
	 * @return a number in the range 0 to 2^48-1.
	 */
	static long getRandom() {
		return toExpectedRange(RandomUtil.newSecureRandom().nextLong());
	}

	/**
	 * Return a new random multicast node identifier.
	 * 
	 * @return a number in the range 0 to 2^48-1.
	 */
	static long getMulticastRandom() {
		return toMulticast(getRandom());
	}

	/**
	 * Clears the leading bits so that the resulting number is in the range 0 to
	 * 2^48-1.
	 * <p>
	 * The result is equivalent to {@code n % 2^48}.
	 * 
	 * @param nodeid the node identifier
	 * @return a number in the range 0 to 2^48-1.
	 */
	static long toExpectedRange(final long nodeid) {
		return nodeid & 0x0000_ffffffffffffL;
	}

	/**
	 * Sets the multicast bit of a node identifier.
	 * <p>
	 * It also clears leading bits so that the resulting number is within the range
	 * 0 to 2^48-1.
	 * 
	 * @param nodeid the node identifier
	 * @return a node identifier with the multicast bit set
	 */
	static long toMulticast(long nodeid) {
		return (nodeid & 0x0000_ffffffffffffL) | 0x0000_010000000000L;
	}

	/**
	 * Checks if the multicast bit of a node identifier is set.
	 * 
	 * @param nodeid a node identifier
	 * @return true if the node identifier is multicast
	 */
	static boolean isMulticast(long nodeid) {
		return (nodeid & 0x0000_010000000000L) == 0x0000_010000000000L;
	}
}
