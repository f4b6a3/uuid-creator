package com.github.f4b6a3.uuid.factory;

class UlidBasedGuidCreatorMock extends UlidBasedGuidCreator {

	public UlidBasedGuidCreatorMock(long low, long high, long previousTimestamp) {
		this(low, high, low, high, previousTimestamp);
	}

	public UlidBasedGuidCreatorMock(long low, long high, long firstLow, long firstHigh, long previousTimestamp) {
		super();

		// Set initial values
		this.low = low;
		this.high = high;

		// Save the initial values
		this.firstLow = firstLow;
		this.firstHigh = firstHigh;

		this.previousTimestamp = previousTimestamp;
	}
}