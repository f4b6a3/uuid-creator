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

package com.github.f4b6a3.uuid.strategy;

import com.github.f4b6a3.uuid.util.RandomUtil;

/**
 * Strategy that provides node identifiers for time-based UUIDs.
 * 
 * It also provides static helper methods.
 */
public interface NodeIdentifierStrategy {
	long getNodeIdentifier();

	/**
	 * Return a random generated node identifier.
	 * 
	 * @return a random multicast node identifier
	 */
	public static long getRandomNodeIdentifier() {
		final long random = RandomUtil.get().nextLong();
		return NodeIdentifierStrategy.setMulticastNodeIdentifier(random);
	}

	/**
	 * Sets the the multicast bit ON to indicate that it's NOT a real MAC address.
	 * 
	 * The output is truncated to fit the node identifier bit length.
	 * 
	 * @param nodeIdentifier a node identifier
	 * @return a multicast node identifier
	 */
	public static long setMulticastNodeIdentifier(long nodeIdentifier) {
		return (nodeIdentifier & 0x0000ffffffffffffL) | 0x0000010000000000L;
	}

	/**
	 * Checks if a node identifier is multicast.
	 * 
	 * @param nodeIdentifier a node identifier
	 * @return true if is multicast
	 */
	public static boolean isMulticastNodeIdentifier(long nodeIdentifier) {
		return (nodeIdentifier & 0x0000010000000000L) == 0x0000010000000000L;
	}
}
