package com.github.f4b6a3.uuid.factory.abst;

import org.junit.Test;

import com.github.f4b6a3.uuid.enums.UuidVersion;

import static org.junit.Assert.*;

public class AbstractUuidCreatorTest {

	// Values to be used in bitwise operations
	protected static final long RFC4122_VARIANT_BITS = 0x8000000000000000L;
	
	protected static final long[] RFC4122_VERSION_BITS = {
			0x0000000000000000L, 0x0000000000001000L, 0x0000000000002000L,
			0x0000000000003000L, 0x0000000000004000L, 0x0000000000005000L };
	
	@Test
	public void testSetVersionBits() {
		
		AbstractUuidCreator creator = new AbstractUuidCreator(UuidVersion.RANDOM_BASED) { };
		long msb = 0x0000000000000000L | RFC4122_VERSION_BITS[UuidVersion.RANDOM_BASED.getValue()];
		long result1 = creator.setVersionBits(msb);
		long result2 = creator.getVersionBits(result1);
		assertEquals(msb, result2);
	}
	
	@Test
	public void testSetVariantBits() {
		AbstractUuidCreator creator = new AbstractUuidCreator(UuidVersion.RANDOM_BASED) { };
		long lsb = 0x0000000000000000L | RFC4122_VARIANT_BITS;
		long result1 = creator.setVariantBits(lsb);
		long result2 = creator.getVariantBits(result1);
		assertEquals(lsb, result2);
	}
}
