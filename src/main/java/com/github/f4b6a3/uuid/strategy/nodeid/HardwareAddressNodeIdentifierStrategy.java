package com.github.f4b6a3.uuid.strategy.nodeid;

import java.net.NetworkInterface;
import java.net.SocketException;

import com.github.f4b6a3.uuid.util.ByteUtils;

public class HardwareAddressNodeIdentifierStrategy extends AbstractNodeIdentifierStrategy {

	public HardwareAddressNodeIdentifierStrategy() {
		this.nodeIdentifier = getHardwareAddress();
		
		if(this.nodeIdentifier == 0) {
			this.nodeIdentifier = setMulticastNodeIdentifier(random.nextLong());
		}
	}
	
	public HardwareAddressNodeIdentifierStrategy(long nodeIdentifier) {
		this.nodeIdentifier = setUnicastNodeIdentifier(nodeIdentifier);
	}

	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}
	
	protected long getHardwareAddress() {
		try {
			NetworkInterface nic = NetworkInterface.getNetworkInterfaces().nextElement();
			byte[] realHardwareAddress = nic.getHardwareAddress();
			if (realHardwareAddress != null) {
				return ByteUtils.toNumber(realHardwareAddress);
			}
		} catch (SocketException | NullPointerException e) {
			return 0;
		}
		return 0;
	}
}
