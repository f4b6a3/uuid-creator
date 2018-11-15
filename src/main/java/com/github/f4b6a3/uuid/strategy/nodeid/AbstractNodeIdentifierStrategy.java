package com.github.f4b6a3.uuid.strategy.nodeid;

import java.util.Random;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;

public abstract class AbstractNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier = 0;
	
	protected static Random random = new Xorshift128PlusRandom();
	
	/**
	 * Sets the the multicast bit on.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	protected long setMulticastNodeIdentifier(long nodeIdentifier) {
		return nodeIdentifier | 0x0000010000000000L;
	}
	
	/**
	 * Sets the the multicast bit off.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	protected long setUnicastNodeIdentifier(long nodeIdentifier) {
		return nodeIdentifier & 0xFFFFFEFFFFFFFFFFL;
	}
}
