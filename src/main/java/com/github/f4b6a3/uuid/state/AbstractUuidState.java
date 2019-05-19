package com.github.f4b6a3.uuid.state;

public abstract class AbstractUuidState {

	protected long timestamp = 0;
	protected int clockSequence = 0;
	protected long nodeIdentifier = 0;

	public AbstractUuidState() {
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getClockSequence() {
		return clockSequence;
	}

	public void setClockSequence(int clockSequence) {
		this.clockSequence = clockSequence;
	}

	public long getNodeIdentifier() {
		return nodeIdentifier;
	}

	public void setNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}

	public abstract void store();

	public abstract void load();
	
	public abstract boolean isValid();
}
