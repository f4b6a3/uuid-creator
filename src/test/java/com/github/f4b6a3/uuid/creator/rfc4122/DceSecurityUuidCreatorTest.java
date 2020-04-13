package com.github.f4b6a3.uuid.creator.rfc4122;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.DceSecurityUuidCreator;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static org.junit.Assert.*;

import java.util.UUID;

public class DceSecurityUuidCreatorTest extends AbstractUuidCreatorTest {

	@Test
	public void testGetDCESecuritylLocalDomain() {

		DceSecurityUuidCreator creator = UuidCreator.getDceSecurityCreator();
		DceSecurityUuidCreator creatorWithMac = UuidCreator.getDceSecurityCreator().withMacNodeIdentifier();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			byte localDomain = (byte) i;
			int localIdentifier = 1701 + i;

			UUID uuid = creator.create(localDomain, localIdentifier);
			byte localDomain2 = UuidUtil.extractLocalDomain(uuid);
			assertEquals(localDomain, localDomain2);

			// Test with hardware address too
			uuid = creatorWithMac.create(localDomain, localIdentifier);
			localDomain2 = UuidUtil.extractLocalDomain(uuid);
			assertEquals(localDomain, localDomain2);
		}
	}
	
	@Test
	public void testGetDCESecuritylLocalIdentifier() {

		DceSecurityUuidCreator creator = UuidCreator.getDceSecurityCreator();
		DceSecurityUuidCreator creatorWithMac = UuidCreator.getDceSecurityCreator().withMacNodeIdentifier();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			byte localDomain = (byte) i;
			int localIdentifier = 1701 + i;

			UUID uuid = creator.create(localDomain, localIdentifier);
			int localIdentifier2 = UuidUtil.extractLocalIdentifier(uuid);
			assertEquals(localIdentifier, localIdentifier2);

			// Test with hardware address too
			uuid = creatorWithMac.create(localDomain, localIdentifier);
			localIdentifier2 = UuidUtil.extractLocalIdentifier(uuid);
			assertEquals(localIdentifier, localIdentifier2);
		}
	}
}
