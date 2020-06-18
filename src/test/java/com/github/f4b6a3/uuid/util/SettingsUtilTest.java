package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.f4b6a3.uuid.util.SettingsUtil;

public class SettingsUtilTest {

	Random random = new Random();

	@BeforeClass
	public static void beforeClass() {
		SettingsUtil.clearProperty(SettingsUtil.PROPERTY_NODEID);
	}

	@AfterClass
	public static void afterClass() {
		SettingsUtil.clearProperty(SettingsUtil.PROPERTY_NODEID);
	}

	@Test
	public void testSetNodeIdentifier() {
		for (int i = 0; i < 100; i++) {
			long number = RandomUtil.get().nextLong() >>> 16;
			SettingsUtil.setNodeIdentifier(number);
			long result = SettingsUtil.getNodeIdentifier();
			assertEquals(number, result);
		}
	}

	@Test
	public void testSetProperty() {
		for (int i = 0; i < 100; i++) {
			long number = random.nextLong() >>> 16;
			String string = Long.toHexString(number);
			SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODEID, string);
			long result = SettingsUtil.getNodeIdentifier();
			assertEquals(number, result);
		}
	}

	@Test
	public void testSetPropertyWith0x() {
		long number = random.nextLong() >>> 16;
		String string = "0x" + Long.toHexString(number);
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODEID, string);
		long result = SettingsUtil.getNodeIdentifier();
		assertEquals(number, result);
	}

	@Test
	public void testSetPropertyInvalid() {
		String string = "0xx112233445566"; // typo
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODEID, string);
		long result = SettingsUtil.getNodeIdentifier();
		assertEquals(0, result);

		string = " 0x112233445566"; // space
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODEID, string);
		result = SettingsUtil.getNodeIdentifier();
		assertEquals(0, result);

		string = " 0x1122334455zz"; // non hexadecimal
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODEID, string);
		result = SettingsUtil.getNodeIdentifier();
		assertEquals(0, result);

		string = ""; // empty
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODEID, string);
		result = SettingsUtil.getNodeIdentifier();
		assertEquals(0, result);

		string = " "; // blank
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODEID, string);
		result = SettingsUtil.getNodeIdentifier();
		assertEquals(0, result);
	}
}
