package com.github.f4b6a3.uuid.nodeid;

import com.github.f4b6a3.uuid.util.FingerprintUtil;

public class FingerprintNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;

	/**
	 * This constructor calculates a node identifier based on system data like:
	 * operating system, java virtual machine, network details and system
	 * resources.
	 * 
	 * 
	 * Read: https://en.wikipedia.org/wiki/Device_fingerprint
	 * 
	 * See {@link FingerprintUtil#getFingerprint()}.
	 */
	public FingerprintNodeIdentifierStrategy() {
		this.nodeIdentifier = FingerprintUtil.getFingerprint();
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
