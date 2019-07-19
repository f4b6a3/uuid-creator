package com.github.f4b6a3.uuid.factory;

class LexicalOrderGuidCreatorMock extends LexicalOrderGuidCreator {

	protected long fixedLow;
	protected long fixedHigh;

	public LexicalOrderGuidCreatorMock(long low, long high) {
		this.fixedLow = low;
		this.fixedHigh = high;
	}

	@Override
	protected synchronized void reset() {
		this.low = this.fixedLow;
		this.high = this.fixedHigh;
	}
}