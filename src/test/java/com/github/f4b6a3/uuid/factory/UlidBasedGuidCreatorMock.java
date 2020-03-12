package com.github.f4b6a3.uuid.factory;

class UlidBasedGuidCreatorMock extends UlidBasedGuidCreator {

	public UlidBasedGuidCreatorMock(long previousTimestamp) {
		super();
		this.previousTimestamp = previousTimestamp;
	}

	public UlidBasedGuidCreatorMock(long randomMsb, long randomLsb, long randomMsbMax, long randomLsbMax, long previousTimestamp) {

		this.randomMsb = randomMsb;
		this.randomLsb = randomLsb;

		this.randomMsbMax = randomMsbMax;
		this.randomLsbMax = randomLsbMax;

		this.previousTimestamp = previousTimestamp;
	}

	public long getRandomMsb() {
		return this.randomMsb;
	}

	public long getRandomLsb() {
		return this.randomLsb;
	}

	public long getRandomHiMax() {
		return this.randomMsb;
	}

	public long getRandomLoMax() {
		return this.randomLsb;
	}
}