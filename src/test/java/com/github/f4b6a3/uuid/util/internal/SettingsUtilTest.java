package com.github.f4b6a3.uuid.util.internal;

import static org.junit.Assert.*;

import java.util.SplittableRandom;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SettingsUtilTest {

	@BeforeClass
	public static void beforeClass() {
		SettingsUtil.clearProperty(SettingsUtil.PROPERTY_NODE);
	}

	@AfterClass
	public static void afterClass() {
		SettingsUtil.clearProperty(SettingsUtil.PROPERTY_NODE);
	}

	@Test
	public void testSetNodeIdentifier() {
		SplittableRandom random = new SplittableRandom(1);
		for (int i = 0; i < 100; i++) {
			long number = random.nextLong() >>> 16;
			SettingsUtil.setNodeIdentifier(number);
			long result = SettingsUtil.getNodeIdentifier();
			assertEquals(number, result);
		}
	}

	@Test
	public void testSetProperty() {
		SplittableRandom random = new SplittableRandom(1);
		for (int i = 0; i < 100; i++) {
			long number = random.nextLong() >>> 16;
			String string = Long.toString(number);
			SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODE, string);
			long result = SettingsUtil.getNodeIdentifier();
			assertEquals(number, result);
		}
	}

	@Test
	public void testSetPropertyWith0x() {
		SplittableRandom random = new SplittableRandom(1);
		long number = random.nextLong() >>> 16;
		String string = "0x" + Long.toHexString(number);
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODE, string);
		long result = SettingsUtil.getNodeIdentifier();
		assertEquals(number, result);
	}

	@Test
	public void testSetPropertyInvalid() {
		String string = "0xx112233445566"; // typo
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODE, string);
		Long result = SettingsUtil.getNodeIdentifier();
		assertNull(result);

		string = " 0x112233445566"; // space
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODE, string);
		result = SettingsUtil.getNodeIdentifier();
		assertNull(result);

		string = " 0x1122334455zz"; // non hexadecimal
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODE, string);
		result = SettingsUtil.getNodeIdentifier();
		assertNull(result);

		string = ""; // empty
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODE, string);
		result = SettingsUtil.getNodeIdentifier();
		assertNull(result);

		string = " "; // blank
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODE, string);
		result = SettingsUtil.getNodeIdentifier();
		assertNull(result);
	}
}
