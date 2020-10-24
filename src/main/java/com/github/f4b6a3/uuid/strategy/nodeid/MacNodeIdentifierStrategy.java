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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.github.f4b6a3.uuid.strategy.NodeIdentifierStrategy;

import static com.github.f4b6a3.uuid.strategy.NodeIdentifierStrategy.getRandomNodeIdentifier;
import static com.github.f4b6a3.uuid.util.internal.ByteUtil.toNumber;

/**
 * Strategy that provides the current machine address (MAC), if available.
 *
 * If no MAC is found, a random node identifier is returned.
 * 
 * It looks for the first MAC that can be found. First it tries to find the MAC
 * that is associated with the host name. Otherwise, it tries to find the first
 * MAC that is up and running. This second try may be very expensive on Windows,
 * because it iterates over a lot of virtual network interfaces created by the
 * operating system.
 * 
 * ### RFC-4122 - 4.1.6. Node
 * 
 * For UUID version 1, the node field consists of an IEEE 802 MAC address,
 * usually the host address. For systems with multiple IEEE 802 addresses, any
 * available one can be used. The lowest addressed octet (octet number 10)
 * contains the global/local bit and the unicast/multicast bit, and is the first
 * octet of the address transmitted on an 802.3 LAN.
 * 
 * For systems with no IEEE address, a randomly or pseudo-randomly generated
 * value may be used; see Section 4.5. The multicast bit must be set in such
 * addresses, in order that they will never conflict with addresses obtained
 * from network cards.
 * 
 */
public final class MacNodeIdentifierStrategy implements NodeIdentifierStrategy {

	private final long nodeIdentifier;

	/**
	 * This constructor works in three steps:
	 * 
	 * 1. Find the MAC associated with the host name;
	 * 
	 * 2. If not found, find the first MAC that is up and running;
	 * 
	 * 3. If not found, generate a random node identifier.
	 * 
	 */
	public MacNodeIdentifierStrategy() {
		this.nodeIdentifier = getHardwareAddress();
	}

	/**
	 * Get the node identifier.
	 * 
	 * @return a node identifier
	 */
	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}

	/**
	 * Returns the hardware address (MAC), if available.
	 * 
	 * {@link MacNodeIdentifierStrategy#getNodeIdentifier()}
	 * 
	 * @return a hardware address
	 */
	private long getHardwareAddress() {
		try {

			// first try: return the MAC associated to the host name
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface nic = NetworkInterface.getByInetAddress(ip);
			if (nic != null) {
				byte[] mac = nic.getHardwareAddress();
				if (!nic.isLoopback() && mac != null && mac.length == 6) {
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
		return getRandomNodeIdentifier();
	}
}
