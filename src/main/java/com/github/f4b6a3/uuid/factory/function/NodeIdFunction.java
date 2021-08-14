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

package com.github.f4b6a3.uuid.factory.function;

import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.util.internal.RandomUtil;

/**
 * It must return a number between 0 and 2^48-1.
 * 
 * Use {@link NodeIdFunction#toExpectedRange(long)} to set the output within the
 * range 0 to 2^48-1.
 * 
 * If necessary, use {@link NodeIdFunction#toMulticast(long)} to set the
 * multicast bit. It also sets the output within the range 0 to 2^48-1.
 * 
 * Examples:
 * 
 * <pre>
 * // A `NodeIdFunction` that always returns a random number between 0 and 2^48-1.
 * NodeIdFunction f = () -> NodeIdFunction.getRandom();
 * </pre>
 * 
 * <pre>
 * // A `NodeIdFunction` that always returns the same number between 0 and 2^48-1.
 * final long number = 1234567890;
 * NodeIdFunction f = () -> NodeIdFunction.toExpectedRange(number);
 * </pre>
 * 
 */
@FunctionalInterface
public interface NodeIdFunction extends LongSupplier {

	/**
	 * This method return a new random node identifier.
	 * 
	 * @return a node identifier in the range 0 to 2^48-1.
	 */
	public static long getRandom() {
		return toExpectedRange(RandomUtil.nextLong());
	}

	/**
	 * This method return a new multicast random node identifier.
	 * 
	 * @return a node identifier in the range 0 to 2^48-1.
	 */
	public static long getMulticastRandom() {
		return toMulticast(RandomUtil.nextLong());
	}

	/**
	 * This method clears the unnecessary leading bits so that the resulting number
	 * is in the range 0 to 2^48-1.
	 * 
	 * The result is equivalent to {@code n % 2^48}.
	 * 
	 * @param nodeid the node identifier
	 * @return a node identifier in the range 0 to 2^48-1.
	 */
	public static long toExpectedRange(long nodeid) {
		return nodeid & 0x0000_ffffffffffffL;
	}

	/**
	 * This method sets the multicast bit of a node identifier.
	 * 
	 * It also clears the unnecessary leading bits so that the resulting number is
	 * within the range 0 to 281474976710655 (2^48-1).
	 * 
	 * @param nodeid the node identifier
	 * @return a node identifier with the multicast bit set
	 */
	public static long toMulticast(long nodeid) {
		return (nodeid & 0x0000_ffffffffffffL) | 0x0000_010000000000L;
	}

	/**
	 * This method checks if the multicast bit of a node identifier is set.
	 * 
	 * @param nodeid a node identifier
	 * @return true if the node identifier is multicast
	 */
	public static boolean isMulticast(long nodeid) {
		return (nodeid & 0x0000_010000000000L) == 0x0000_010000000000L;
	}
}
