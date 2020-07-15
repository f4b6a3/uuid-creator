package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.f4b6a3.uuid.util.UuidSettings;

public class UuidSettingsTest {

	Random random = new Random();

	@BeforeClass
	public static void beforeClass() {
		UuidSettings.clearProperty(UuidSettings.PROPERTY_NODE);
		UuidSettings.clearProperty(UuidSettings.PROPERTY_NODEID);
	}

	@AfterClass
	public static void afterClass() {
		UuidSettings.clearProperty(UuidSettings.PROPERTY_NODE);
		UuidSettings.clearProperty(UuidSettings.PROPERTY_NODEID);
	}

	@Test
	public void testSetNodeIdentifier() {
		for (int i = 0; i < 100; i++) {
			long number = this.random.nextLong() >>> 16;
			UuidSettings.setNodeIdentifier(number);
			long result = UuidSettings.getNodeIdentifier();
			assertEquals(number, result);
		}
	}

	@Test
	public void testSetProperty() {
		for (int i = 0; i < 100; i++) {
			long number = random.nextLong() >>> 16;
			String string = Long.toString(number);
			UuidSettings.setProperty(UuidSettings.PROPERTY_NODE, string);
			long result = UuidSettings.getNodeIdentifier();
			assertEquals(number, result);
		}
	}
	
	@Test
	public void testSetPropertyDeprecated() {
		for (int i = 0; i < 100; i++) {
			long number = random.nextLong() >>> 16;
			String string = Long.toHexString(number);
			UuidSettings.setProperty(UuidSettings.PROPERTY_NODEID, string);
			long result = UuidSettings.getNodeIdentifierDeprecated();
			assertEquals(number, result);
		}
	}

	@Test
	public void testSetPropertyWith0x() {
		long number = random.nextLong() >>> 16;
		String string = "0x" + Long.toHexString(number);
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODE, string);
		long result = UuidSettings.getNodeIdentifier();
		assertEquals(number, result);
	}
	
	@Test
	public void testSetPropertyWith0xDeprecated() {
		long number = random.nextLong() >>> 16;
		String string = "0x" + Long.toHexString(number);
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODEID, string);
		long result = UuidSettings.getNodeIdentifierDeprecated();
		assertEquals(number, result);
	}

	@Test
	public void testSetPropertyInvalid() {
		String string = "0xx112233445566"; // typo
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODE, string);
		Long result = UuidSettings.getNodeIdentifier();
		assertNull(result);

		string = " 0x112233445566"; // space
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODE, string);
		result = UuidSettings.getNodeIdentifier();
		assertNull(result);

		string = " 0x1122334455zz"; // non hexadecimal
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODE, string);
		result = UuidSettings.getNodeIdentifier();
		assertNull(result);

		string = ""; // empty
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODE, string);
		result = UuidSettings.getNodeIdentifier();
		assertNull(result);

		string = " "; // blank
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODE, string);
		result = UuidSettings.getNodeIdentifier();
		assertNull(result);
	}
	
	@Test
	public void testSetPropertyInvalidDeprecated() {
		String string = "0xx112233445566"; // typo
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODEID, string);
		Long result = UuidSettings.getNodeIdentifierDeprecated();
		assertNull(result);

		string = " 0x112233445566"; // space
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODEID, string);
		result = UuidSettings.getNodeIdentifierDeprecated();
		assertNull(result);

		string = " 0x1122334455zz"; // non hexadecimal
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODEID, string);
		result = UuidSettings.getNodeIdentifierDeprecated();
		assertNull(result);

		string = ""; // empty
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODEID, string);
		result = UuidSettings.getNodeIdentifierDeprecated();
		assertNull(result);

		string = " "; // blank
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODEID, string);
		result = UuidSettings.getNodeIdentifierDeprecated();
		assertNull(result);
	}
}
