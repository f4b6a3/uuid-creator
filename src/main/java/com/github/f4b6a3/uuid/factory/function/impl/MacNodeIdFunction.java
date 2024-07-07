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

package com.github.f4b6a3.uuid.factory.function.impl;

import static com.github.f4b6a3.uuid.util.internal.ByteUtil.toNumber;

import java.net.NetworkInterface;
import java.net.SocketException;

import com.github.f4b6a3.uuid.factory.function.NodeIdFunction;
import com.github.f4b6a3.uuid.util.internal.NetworkUtil;

/**
 * Function that returns a MAC address.
 * <p>
 * The MAC address is obtained once during instantiation.
 * <p>
 * If no MAC address is found, it returns a random multicast node identifier.
 * 
 * @see NodeIdFunction
 */
public final class MacNodeIdFunction implements NodeIdFunction {

	private final long nodeIdentifier;

	/**
	 * Default constructor.
	 */
	public MacNodeIdFunction() {
		this.nodeIdentifier = getHardwareAddress();
	}

	@Override
	public long getAsLong() {
		return this.nodeIdentifier;
	}

	private long getHardwareAddress() {

		try {
			NetworkInterface nic = NetworkUtil.nic();
			if (nic != null) {
				return toNumber(nic.getHardwareAddress());
			}
		} catch (SocketException e) {
			// do nothing
		}

		return NodeIdFunction.getMulticastRandom();
	}
}
