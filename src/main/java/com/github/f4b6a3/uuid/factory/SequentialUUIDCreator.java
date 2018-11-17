package com.github.f4b6a3.uuid.factory;

import com.github.f4b6a3.uuid.factory.abst.AbstractTimeBasedUUIDCreator;
import com.github.f4b6a3.uuid.strategy.SequentialTimeBasedUUIDStrategy;

public class SequentialUUIDCreator extends AbstractTimeBasedUUIDCreator {
	
	private static final long serialVersionUID = 9208566282411426708L;

	public SequentialUUIDCreator() {
		super(VERSION_0, new SequentialTimeBasedUUIDStrategy());
	}
}
