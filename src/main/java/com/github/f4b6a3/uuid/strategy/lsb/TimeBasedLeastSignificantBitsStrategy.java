package com.github.f4b6a3.uuid.strategy.lsb;

import com.github.f4b6a3.uuid.factory.UUIDCreator;

public class TimeBasedLeastSignificantBitsStrategy implements LeastSignificantBitsStrategy {

	/**
	 * Returns the least significant bits of the UUID.
	 * 
	 * ### RFC-4122 - 4.2.2. Generation Details
	 * 
	 * Set the clock_seq_low field to the eight least significant bits (bits
	 * zero through 7) of the clock sequence in the same order of significance.
	 * 
	 * Set the 6 least significant bits (bits zero through 5) of the
	 * clock_seq_hi_and_reserved field to the 6 most significant bits (bits 8
	 * through 13) of the clock sequence in the same order of significance.
	 * 
	 * Set the two most significant bits (bits 6 and 7) of the
	 * clock_seq_hi_and_reserved to zero and one, respectively.
	 * 
	 * Set the node field to the 48-bit IEEE address in the same order of
	 * significance as the address.
	 * 
	 * @param nodeIdentifier
	 */
	@Override
	public long getLeastSignificantBits(long nodeIdentifier, long sequence) {
		
		long seq = sequence << 48;
		seq = setVariantBits(seq);
		
		long nod = nodeIdentifier & 0x0000ffffffffffffL;
		
		return (seq | nod);
	}
	
	/**
	 * Set UUID variant bits into the "Least Significant Bits".
	 */
	protected long setVariantBits(long lsb) {
		return (lsb & 0x3fffffffffffffffL) | UUIDCreator.RFC4122_VARIANT_BITS;
	}

}
