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

package com.github.f4b6a3.uuid.factory.function.impl;

import static com.github.f4b6a3.uuid.util.internal.ByteUtil.toNumber;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.github.f4b6a3.uuid.factory.function.NodeIdFunction;

public final class MacNodeIdFunction implements NodeIdFunction {

	private final long nodeIdentifier;

	public MacNodeIdFunction() {
		this.nodeIdentifier = getHardwareAddress();
	}

	@Override
	public long getAsLong() {
		return this.nodeIdentifier;
	}

	private long getHardwareAddress() {
		try {

			// first try: return the MAC associated to the host name
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface nic = NetworkInterface.getByInetAddress(ip);
			if (nic != null && !nic.isLoopback()) {
				byte[] mac = nic.getHardwareAddress();
				if (mac != null && mac.length == 6) {
					return toNumber(mac);
				}
			}

			// second try: return the first MAC found
			Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
			while (nics.hasMoreElements()) {
				NetworkInterface next = nics.nextElement();
				if (next != null && !next.isLoopback()) {
					byte[] mac = next.getHardwareAddress();
					if (mac != null && mac.length == 6) {
						return toNumber(mac);
					}
				}
			}
		} catch (UnknownHostException | SocketException e) {
			// do nothing
		}

		// Return a random node identifier
		return NodeIdFunction.getMulticastRandom();
	}
}
