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

package com.github.f4b6a3.uuid.nodeid;

import com.github.f4b6a3.uuid.util.FingerprintUtil;
import com.github.f4b6a3.uuid.util.NodeIdentifierUtil;

public class FingerprintNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;
	
	protected static final long NODEID_MAX = 0x0000FFFFFFFFFFFFL;

	/**
	 * This constructor calculates a node identifier based on system data like:
	 * operating system, java virtual machine, network details, system
	 * resources, locale and timezone.
	 * 
	 * Read: https://en.wikipedia.org/wiki/Device_fingerprint
	 * 
	 */
	public FingerprintNodeIdentifierStrategy() {
		final long fingerprint = FingerprintUtil.getFingerprint() & NODEID_MAX;
		this.nodeIdentifier = NodeIdentifierUtil.setMulticastNodeIdentifier(fingerprint);
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
