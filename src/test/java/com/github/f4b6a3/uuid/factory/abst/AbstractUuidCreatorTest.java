package com.github.f4b6a3.uuid.factory.abst;

import org.junit.Test;

import com.github.f4b6a3.uuid.enums.UuidVersion;

import static com.github.f4b6a3.uuid.factory.abst.AbstractUuidCreator.*;

import static org.junit.Assert.*;

public class AbstractUuidCreatorTest {

	
	@Test
	public void testValid() {
		
		long lsb = 0x0000000000000000L | RFC4122_VARIANT_BITS;
		
		AbstractUuidCreator creator = new AbstractUuidCreator(UuidVersion.SEQUENTIAL) { };
		long msb = 0x0000000000000000L | RFC4122_VERSION_BITS[UuidVersion.SEQUENTIAL.getValue()];
		assertTrue(creator.valid(msb, lsb));
		
		creator = new AbstractUuidCreator(UuidVersion.TIME_BASED) { };
		msb = 0x0000000000000000L | RFC4122_VERSION_BITS[UuidVersion.TIME_BASED.getValue()];
		assertTrue(creator.valid(msb, lsb));
		
		creator = new AbstractUuidCreator(UuidVersion.DCE_SECURITY) { };
		msb = 0x0000000000000000L | RFC4122_VERSION_BITS[UuidVersion.DCE_SECURITY.getValue()];
		assertTrue(creator.valid(msb, lsb));
		
		creator = new AbstractUuidCreator(UuidVersion.NAME_BASED_MD5) { };
		msb = 0x0000000000000000L | RFC4122_VERSION_BITS[UuidVersion.NAME_BASED_MD5.getValue()];
		assertTrue(creator.valid(msb, lsb));
		
		creator = new AbstractUuidCreator(UuidVersion.RANDOM_BASED) { };
		msb = 0x0000000000000000L | RFC4122_VERSION_BITS[UuidVersion.RANDOM_BASED.getValue()];
		
		assertTrue(creator.valid(msb, lsb));
		creator = new AbstractUuidCreator(UuidVersion.NAMBE_BASED_SHA1) { };
		msb = 0x0000000000000000L | RFC4122_VERSION_BITS[UuidVersion.NAMBE_BASED_SHA1.getValue()];
		assertTrue(creator.valid(msb, lsb));
	}
	
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
