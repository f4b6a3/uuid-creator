package com.github.f4b6a3.uuid.util.internal;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.f4b6a3.uuid.util.internal.UuidCreatorSettings;

public class UuidCreatorSettingsTest {

	Random random = new Random();

	@BeforeClass
	public static void beforeClass() {
		UuidCreatorSettings.clearProperty(UuidCreatorSettings.PROPERTY_NODE);
	}

	@AfterClass
	public static void afterClass() {
		UuidCreatorSettings.clearProperty(UuidCreatorSettings.PROPERTY_NODE);
	}

	@Test
	public void testSetNodeIdentifier() {
		for (int i = 0; i < 100; i++) {
			long number = this.random.nextLong() >>> 16;
			UuidCreatorSettings.setNodeIdentifier(number);
			long result = UuidCreatorSettings.getNodeIdentifier();
			assertEquals(number, result);
		}
	}

	@Test
	public void testSetProperty() {
		for (int i = 0; i < 100; i++) {
			long number = random.nextLong() >>> 16;
			String string = Long.toString(number);
			UuidCreatorSettings.setProperty(UuidCreatorSettings.PROPERTY_NODE, string);
			long result = UuidCreatorSettings.getNodeIdentifier();
			assertEquals(number, result);
		}
	}

	@Test
	public void testSetPropertyWith0x() {
		long number = random.nextLong() >>> 16;
		String string = "0x" + Long.toHexString(number);
		UuidCreatorSettings.setProperty(UuidCreatorSettings.PROPERTY_NODE, string);
		long result = UuidCreatorSettings.getNodeIdentifier();
		assertEquals(number, result);
	}

	@Test
	public void testSetPropertyInvalid() {
		String string = "0xx112233445566"; // typo
		UuidCreatorSettings.setProperty(UuidCreatorSettings.PROPERTY_NODE, string);
		Long result = UuidCreatorSettings.getNodeIdentifier();
		assertNull(result);

		string = " 0x112233445566"; // space
		UuidCreatorSettings.setProperty(UuidCreatorSettings.PROPERTY_NODE, string);
		result = UuidCreatorSettings.getNodeIdentifier();
		assertNull(result);

		string = " 0x1122334455zz"; // non hexadecimal
		UuidCreatorSettings.setProperty(UuidCreatorSettings.PROPERTY_NODE, string);
		result = UuidCreatorSettings.getNodeIdentifier();
		assertNull(result);

		string = ""; // empty
		UuidCreatorSettings.setProperty(UuidCreatorSettings.PROPERTY_NODE, string);
		result = UuidCreatorSettings.getNodeIdentifier();
		assertNull(result);

		string = " "; // blank
		UuidCreatorSettings.setProperty(UuidCreatorSettings.PROPERTY_NODE, string);
		result = UuidCreatorSettings.getNodeIdentifier();
		assertNull(result);
	}
}
