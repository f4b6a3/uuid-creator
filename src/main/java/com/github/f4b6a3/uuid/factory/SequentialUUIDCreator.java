package com.github.f4b6a3.uuid.factory;

import com.github.f4b6a3.uuid.strategy.msb.SequentialMostSignificantBitsStrategy;

public class SequentialUUIDCreator extends TimeBasedUUIDCreator {
	
	private static final long serialVersionUID = 9208566282411426708L;

	public SequentialUUIDCreator() {
		super();
		this.version = VERSION_0;
		this.mostSignificantBitsStrategy = new SequentialMostSignificantBitsStrategy();
	}

}
