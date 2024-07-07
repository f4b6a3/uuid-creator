package com.github.f4b6a3.uuid.factory.standard;

import org.junit.Test;

import com.github.f4b6a3.uuid.factory.UuidFactoryTest;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static org.junit.Assert.*;

import java.util.UUID;

public class DceSecurityFactoryTest extends UuidFactoryTest {

	@Test
	public void testGetDCESecuritylLocalDomain() {

		DceSecurityFactory factory = new DceSecurityFactory();
		DceSecurityFactory factoryWithMac = DceSecurityFactory.builder().withMacNodeId().build();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			byte localDomain = (byte) i;
			int localIdentifier = 1701 + i;

			UUID uuid = factory.create(localDomain, localIdentifier);
			byte localDomain2 = UuidUtil.getLocalDomain(uuid);
			assertEquals(localDomain, localDomain2);

			// Test with hardware address too
			uuid = factoryWithMac.create(localDomain, localIdentifier);
			localDomain2 = UuidUtil.getLocalDomain(uuid);
			assertEquals(localDomain, localDomain2);
		}
	}

	@Test
	public void testGetDCESecuritylLocalIdentifier() {

		DceSecurityFactory factory = new DceSecurityFactory();
		DceSecurityFactory factoryWithMac = DceSecurityFactory.builder().withMacNodeId().build();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			byte localDomain = (byte) i;
			int localIdentifier = 1701 + i;

			UUID uuid = factory.create(localDomain, localIdentifier);
			int localIdentifier2 = UuidUtil.getLocalIdentifier(uuid);
			assertEquals(localIdentifier, localIdentifier2);

			// Test with hardware address too
			uuid = factoryWithMac.create(localDomain, localIdentifier);
			localIdentifier2 = UuidUtil.getLocalIdentifier(uuid);
			assertEquals(localIdentifier, localIdentifier2);
		}
	}
}
