package com.github.f4b6a3.uuid.factory;

class CombGuidCreatorMock extends CombGuidCreator {

	protected long fixedLow;
	protected long fixedHigh;

	public CombGuidCreatorMock(long low, long high) {
		this.fixedLow = low;
		this.fixedHigh = high;
	}

	@Override
	protected synchronized void reset() {
		this.low = this.fixedLow;
		this.high = this.fixedHigh;
	}
}