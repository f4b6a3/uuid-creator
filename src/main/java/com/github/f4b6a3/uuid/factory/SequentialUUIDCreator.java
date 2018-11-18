package com.github.f4b6a3.uuid.factory;

import com.github.f4b6a3.uuid.factory.abst.AbstractTimeBasedUUIDCreator;
import com.github.f4b6a3.uuid.strategy.SequentialTimeBasedUUIDStrategy;

/**
 * Factory that create time-based UUIDs 0, not present in the the RFC-4122.
 * 
 * The version 0 is a extension of the RFC-4122. It's just rearranges the bits
 * of timestamp in the 'natural' order.
 *
 */
public class SequentialUUIDCreator extends AbstractTimeBasedUUIDCreator {

	private static final long serialVersionUID = 9208566282411426708L;

	public SequentialUUIDCreator() {
		super(VERSION_0, new SequentialTimeBasedUUIDStrategy());
	}
}
