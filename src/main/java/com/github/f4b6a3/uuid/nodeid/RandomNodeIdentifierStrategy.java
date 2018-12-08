package com.github.f4b6a3.uuid.nodeid;

import java.util.Random;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.util.NodeIdentifierUtil;

public class RandomNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected Random random;

	public RandomNodeIdentifierStrategy() {
		this.random = new Xorshift128PlusRandom();
	}

	/**
	 * Return a random node identifier.
	 * 
	 * It uses {@link Xorshift128PlusRandom} to generate random numbers.
	 * 
	 * The only difference to the {@link DefaultNodeIdentifierStrategy} is that
	 * the random value always changes for each call.
	 * 
	 * @see {@link DefaultNodeIdentifierStrategy}
	 * 
	 * @return
	 */
	@Override
	public long getNodeIdentifier() {
		return NodeIdentifierUtil.setMulticastNodeIdentifier(random.nextLong());
	}
}
