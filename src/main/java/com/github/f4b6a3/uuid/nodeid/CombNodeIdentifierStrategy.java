package com.github.f4b6a3.uuid.nodeid;

public class CombNodeIdentifierStrategy implements NodeIdentifierStrategy {

	/**
	 * Returns the count of milliseconds since 01-01-1970.
	 * 
	 * The node identifier is a 48-bit value. This timestamp is the difference,
	 * measured in milliseconds, between the current time and midnight, January
	 * 1, 1970 UTC. The the time value rolls over around AD 10,889.
	 * 
	 * <pre>
	 * s = 1000;
	 * m = 60 * s;
	 * h = 60 * m;
	 * d = 24 * h;
	 * y = 365.25 * d;
	 * 2^48 / y = ~8919
	 * 8919 + 1970 = 10889
	 * </pre>
	 * 
	 */
	@Override
	public long getNodeIdentifier() {
		return System.currentTimeMillis();
	}
}