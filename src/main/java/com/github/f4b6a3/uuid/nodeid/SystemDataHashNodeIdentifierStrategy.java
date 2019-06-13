package com.github.f4b6a3.uuid.nodeid;

import com.github.f4b6a3.uuid.util.SystemDataUtil;

public class SystemDataHashNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;

	/**
	 * This constructor calculates a node identifier based on system data like:
	 * operating system, java virtual machine, network details and system
	 * resources.
	 * 
	 * See {@link SystemDataUtil#getSystemId()}.
	 */
	public SystemDataHashNodeIdentifierStrategy() {
		this.nodeIdentifier = SystemDataUtil.getSystemId();
	}

	/**
	 * Get the node identifier.
	 * 
	 * The node identifier is calculated using system data. The resulting node
	 * identifier is as unique and mutable as the host machine.
	 * 
	 * See {@link SystemDataUtil#getSystemId(String)}.
	 */
	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}
}
