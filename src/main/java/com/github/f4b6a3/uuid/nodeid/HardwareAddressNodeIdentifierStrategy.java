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

import java.util.List;

import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.NetworkData;
import com.github.f4b6a3.uuid.util.NodeIdentifierUtil;
import com.github.f4b6a3.uuid.util.RandomUtil;

public class HardwareAddressNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;

	public HardwareAddressNodeIdentifierStrategy() {
		this.nodeIdentifier = getHardwareAddress();
	}

	/**
	 * Get the machine address.
	 * 
	 * It returns the first MAC that can be found. First it tries to find the
	 * MAC that is associated with the host name. Otherwise, it tries to find
	 * the first MAC that is up and running. This second try may be very
	 * expensive on Windows, because it iterates over a lot of virtual network
	 * interfaces created by the operating system.
	 * 
	 * If no MAC is found, a random node identifier is returned.
	 * 
	 * ### RFC-4122 - 4.1.6. Node
	 * 
	 * (1) For UUID version 1, the node field consists of an IEEE 802 MAC
	 * address, usually the host address. For systems with multiple IEEE 802
	 * addresses, any available one can be used. The lowest addressed octet
	 * (octet number 10) contains the global/local bit and the unicast/multicast
	 * bit, and is the first octet of the address transmitted on an 802.3 LAN.
	 * 
	 * (2) For systems with no IEEE address, a randomly or pseudo-randomly
	 * generated value may be used; see Section 4.5. The multicast bit must be
	 * set in such addresses, in order that they will never conflict with
	 * addresses obtained from network cards.
	 * 
	 * @return a node identifier
	 */
	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}

	/**
	 * 
	 * {@link HardwareAddressNodeIdentifierStrategy#getNodeIdentifier()}
	 * 
	 * @return a hardware address
	 */
	protected long getHardwareAddress() {

		// first try
		NetworkData networkData = NetworkData.getNetworkData();

		// second try, if the first one failed
		if (networkData == null) {
			List<NetworkData> networkDataList = NetworkData.getNetworkDataList();
			if (networkDataList != null && !networkDataList.isEmpty()) {
				networkData = networkDataList.get(0);
			}
		}

		// Return the hardware address if found
		if (networkData != null) {
			String hardwareAddress = networkData.getInterfaceHardwareAddress();
			if (hardwareAddress != null && !hardwareAddress.isEmpty()) {
				return ByteUtil.toNumber(networkData.getInterfaceHardwareAddress());
			}
		}

		// Return a random node identifier
		return getRandomMulticastNodeIdentifier();
	}

	/**
	 * Return a random generated node identifier.
	 * 
	 * {@link HardwareAddressNodeIdentifierStrategy#getNodeIdentifier()}
	 * 
	 * @return a random multicast node identifier
	 */
	protected long getRandomMulticastNodeIdentifier() {
		return NodeIdentifierUtil.setMulticastNodeIdentifier(RandomUtil.getInstance().nextLong());
	}
}
