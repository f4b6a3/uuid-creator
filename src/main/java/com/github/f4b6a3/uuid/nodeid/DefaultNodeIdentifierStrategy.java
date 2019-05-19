package com.github.f4b6a3.uuid.nodeid;

import com.github.f4b6a3.uuid.util.SettingsUtil;
import com.github.f4b6a3.uuid.util.SystemDataUtil;

public class DefaultNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;

	/**
	 * This constructor works in two steps:
	 * 
	 * 1. Check if there's a preferred node identifier set in a system property
	 * or environment variable;
	 * 
	 * 2. If no preferred node identifier exists, calculate a node identifier
	 * based on system data like: operating system, java virtual machine,
	 * network details and system resources.
	 * 
	 * @see {@link SystemDataUtil#getSystemId(String)}
	 */
	public DefaultNodeIdentifierStrategy() {

		long preferedNodeIdentifier = SettingsUtil.getNodeIdentifier();
		if (preferedNodeIdentifier != 0) {
			this.nodeIdentifier = preferedNodeIdentifier;
		} else {
			String salt = SettingsUtil.getNodeIdentifierSalt();
			this.nodeIdentifier = SystemDataUtil.getSystemId(salt);
		}
	}

	/**
	 * Get the node identifier.
	 * 
	 * The node identifier in this library is calculated using system data. The
	 * resulting node identifier is as unique and mutable as the host machine.
	 * 
	 * If the calculated node identifier is not desired, you can override this
	 * by setting a preferred node identifier via system property or environment
	 * variable.
	 * 
	 * @see {@link SystemDataUtil}
	 * 
	 */
	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}
}
