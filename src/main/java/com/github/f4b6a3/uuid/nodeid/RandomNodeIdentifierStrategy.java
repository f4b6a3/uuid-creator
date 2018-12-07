package com.github.f4b6a3.uuid.nodeid;

import java.util.Random;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.util.UuidUtil;

public class RandomNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected Random random = new Xorshift128PlusRandom();

	/**
	 * Return a random node identifier.
	 * 
	 * It uses {@link Xorshift128PlusRandom} to generate a different random
	 * number for each call.
	 * 
	 * @return
	 */
	@Override
	public long getNodeIdentifier() {
		return UuidUtil.setMulticastNodeIdentifier(random.nextLong());
	}
}
