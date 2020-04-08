package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.github.f4b6a3.commons.util.ByteUtil;
import com.github.f4b6a3.uuid.UuidCreator;

public class SettingsUtilTest {

	private static final long NODEID = 0x111111111111L;
	private static final long NODEID_ZERO = 0x000000000000L;

	@Before
	public void clearSystemProperties() {
		System.clearProperty(SettingsUtil.PROPERTY_NODEID);
	}

	@Test
	public void nodeIdentifierShouldBeIqualToSystemProperty() {
		SettingsUtil.setNodeIdentifier(NODEID);
		UUID uuid = UuidCreator.getTimeBasedCreator().create();
		long nodeid = UuidUtil.extractNodeIdentifier(uuid);
		assertEquals(ByteUtil.toHexadecimal(NODEID), ByteUtil.toHexadecimal(nodeid));
	}

	@Test
	public void nodeIdentifierShouldNotBeIqualToSystemProperty() {
		SettingsUtil.setNodeIdentifier(NODEID_ZERO);
		UUID uuid = UuidCreator.getTimeBasedCreator().create();
		long nodeid = UuidUtil.extractNodeIdentifier(uuid);
		assertNotEquals(ByteUtil.toHexadecimal(NODEID), ByteUtil.toHexadecimal(nodeid));

		clearSystemProperties();
		uuid = UuidCreator.getTimeBasedCreator().create();
		nodeid = UuidUtil.extractNodeIdentifier(uuid);
		assertNotEquals(ByteUtil.toHexadecimal(NODEID), ByteUtil.toHexadecimal(nodeid));
	}
}
