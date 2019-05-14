package com.github.f4b6a3.uuid.nodeid;

import com.github.f4b6a3.uuid.util.SettingsUtil;
import com.github.f4b6a3.uuid.util.SystemUtil;

public class DefaultNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;

	public DefaultNodeIdentifierStrategy() {

		long preferedNodeIdentifier = SettingsUtil.getNodeIdentifier();
		if (preferedNodeIdentifier != 0) {
			this.nodeIdentifier = preferedNodeIdentifier;
		} else {
			String salt = SettingsUtil.getNodeIdentifierSalt();
			this.nodeIdentifier = SystemUtil.getSystemHashId(salt);
		}
	}

	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}
}
