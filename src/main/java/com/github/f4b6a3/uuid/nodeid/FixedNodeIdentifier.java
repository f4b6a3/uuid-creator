package com.github.f4b6a3.uuid.nodeid;

public class FixedNodeIdentifier implements NodeIdentifierStrategy {

	protected long nodeIdentifier = 0;

	public FixedNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}

	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}
}