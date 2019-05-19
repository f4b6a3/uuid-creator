package com.github.f4b6a3.uuid.nodeid;

public class FixedNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;

	public FixedNodeIdentifierStrategy(long nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}

	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}
}