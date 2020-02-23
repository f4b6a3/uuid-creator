package com.github.f4b6a3.uuid.factory;

class CombGuidCreatorMock extends CombGuidCreator {
	public CombGuidCreatorMock(long low, long high, long previousTimestamp) {
		super();
		this.low = low;
		this.high = high;
		this.previousTimestamp = previousTimestamp;
	}
}