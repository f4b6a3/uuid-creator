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

package com.github.f4b6a3.uuid.strategy.nodeid;

import com.github.f4b6a3.uuid.util.FingerprintUtil;
import com.github.f4b6a3.uuid.strategy.NodeIdentifierStrategy;

/**
 * Strategy that calculates a node identifier based on system data like:
 * operating system, java virtual machine, network details, system resources,
 * locale and timezone.
 * 
 * The resulting node identifier is as unique and mutable as the host machine
 * data.
 * 
 * Read: https://en.wikipedia.org/wiki/Device_fingerprint
 * 
 * ### RFC-4122 - 4.1.6. Node
 * 
 * For systems with no IEEE address, a randomly or pseudo-randomly generated
 * value may be used; see Section 4.5. The multicast bit must be set in such
 * addresses, in order that they will never conflict with addresses obtained
 * from network cards.
 * 
 * ### RFC-4122 - 4.5. Node IDs that Do Not Identify the Host
 * 
 * In addition, items such as the computer's name and the name of the operating
 * system, while not strictly speaking random, will help differentiate the
 * results from those obtained by other systems.
 * 
 * The exact algorithm to generate a node ID using these data is system
 * specific, because both the data available and the functions to obtain them
 * are often very system specific. A generic approach, however, is to accumulate
 * as many sources as possible into a buffer, use a message digest such as MD5
 * [4] or SHA-1 [8], take an arbitrary 6 bytes from the hash value, and set the
 * multicast bit as described above.
 */
public class HashNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;

	/**
	 * This constructor calculates a node identifier based on system data like:
	 * operating system, java virtual machine, network details, system resources,
	 * locale and timezone.
	 */
	public HashNodeIdentifierStrategy() {
		final long fingerprint = FingerprintUtil.getFingerprint();
		this.nodeIdentifier = NodeIdentifierStrategy.setMulticastNodeIdentifier(fingerprint);
	}

	/**
	 * Get the node identifier.
	 * 
	 * The node identifier is calculated using system data. The resulting node
	 * identifier is as unique and mutable as the host machine.
	 * 
	 * See {@link FingerprintUtil#getFingerprint()}.
	 */
	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}
}
