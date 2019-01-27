package com.github.f4b6a3.uuid.nodeid;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.NodeIdentifierUtil;

public class MacNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;
	protected Random random;

	public MacNodeIdentifierStrategy() {
		this.random = new SecureRandom();
		this.nodeIdentifier = getHardwareAddress();

	}

	/**
	 * Get the machine address.
	 * 
	 * It looks for the first MAC that is up and running. If none is up, it
	 * looks for the first MAC.
	 * 
	 * It returns ZERO if none is found.
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
	 * {@link MacNodeIdentifierStrategy#getNodeIdentifier()}
	 * 
	 * @return a hardware address
	 */
	protected long getHardwareAddress() {
		try {
			byte[] mac;
			NetworkInterface nic;
			List<InterfaceAddress> list;
			Enumeration<NetworkInterface> enm;

			enm = NetworkInterface.getNetworkInterfaces();
			while (enm.hasMoreElements()) {
				nic = enm.nextElement();
				list = nic.getInterfaceAddresses();
				mac = nic.getHardwareAddress();
				if ((mac != null) && (!list.isEmpty()) && !(nic.isLoopback() || nic.isVirtual())) {
					return ByteUtil.toNumber(mac);
				}
			}
		} catch (SocketException | NullPointerException e) {
			// (2) return random number
			return getRandomMulticastNodeIdentifier();
		}
		// (2) return random number
		return getRandomMulticastNodeIdentifier();
	}

	/**
	 * Return a random generated node identifier.
	 * 
	 * {@link MacNodeIdentifierStrategy#getNodeIdentifier()}
	 * 
	 * @return a random multicast node identifier
	 */
	protected long getRandomMulticastNodeIdentifier() {
		return NodeIdentifierUtil.setMulticastNodeIdentifier(random.nextLong());
	}
}
