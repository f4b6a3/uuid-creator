package com.github.f4b6a3.uuid.strategy.nodeid;

public class MulticastNodeIdentifierStrategy extends AbstractNodeIdentifierStrategy {
	
	public MulticastNodeIdentifierStrategy() {
		this.nodeIdentifier = setMulticastNodeIdentifier(random.nextLong());
	}
	
	public MulticastNodeIdentifierStrategy(long nodeIdentifier) {
		this.nodeIdentifier = setMulticastNodeIdentifier(nodeIdentifier);
	}
	
	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}
}
