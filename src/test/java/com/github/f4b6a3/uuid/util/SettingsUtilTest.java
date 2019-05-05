package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.SettingsUtil;
import com.github.f4b6a3.uuid.util.UuidUtil;

public class SettingsUtilTest {

	private static final int CLOCKSEQ = 0x1111;
	private static final int CLOCKSEQ_ZERO = 0x0000;
	
	private static final long NODEID = 0x111111111111L;
	private static final long NODEID_ZERO = 0x000000000000L;
	
	@Before
	public void clearSystemProperties() {
		SettingsUtil.setStateEnabled(false);
		System.clearProperty(SettingsUtil.PROPERTY_NODEID);
		System.clearProperty(SettingsUtil.PROPERTY_CLOCKSEQ);
		System.clearProperty(SettingsUtil.PROPERTY_STATE_ENABLED);
		System.clearProperty(SettingsUtil.PROPERTY_STATE_DIRECTORY);
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
	
	@Test
	public void clockSequenceShouldBeIqualToSystemProperty() {
		SettingsUtil.setClockSequence(CLOCKSEQ);
		UUID uuid = UuidCreator.getTimeBasedCreator().create();
		long clockseq = UuidUtil.extractClockSequence(uuid);
		assertEquals(ByteUtil.toHexadecimal(CLOCKSEQ), ByteUtil.toHexadecimal(clockseq));
	}
	
	@Test
	public void clockSequenceShouldNotBeIqualToSystemProperty() {
		SettingsUtil.setClockSequence(CLOCKSEQ_ZERO);
		UUID uuid = UuidCreator.getTimeBasedCreator().create();
		long clockseq = UuidUtil.extractClockSequence(uuid);
		assertNotEquals(ByteUtil.toHexadecimal(CLOCKSEQ), ByteUtil.toHexadecimal(clockseq));
		
		uuid = UuidCreator.getTimeBasedCreator().create();
		clockseq = UuidUtil.extractClockSequence(uuid);
		assertNotEquals(ByteUtil.toHexadecimal(CLOCKSEQ), ByteUtil.toHexadecimal(clockseq));
	}
}
