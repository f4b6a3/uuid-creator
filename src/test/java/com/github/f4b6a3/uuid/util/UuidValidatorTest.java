package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidValidator;

public class UuidValidatorTest {

	@Test
	public void testIsValidBytes() {

		byte[] uuid = null;
		assertFalse("Null UUID byte array should be invalid.", UuidValidator.isValid(uuid));

		uuid = new byte[0];
		assertFalse("Empty UUID byte array should be invalid .", UuidValidator.isValid(uuid));

		uuid = new byte[15];
		assertFalse("UUID byte array with length lower than 16 should be invalid .", UuidValidator.isValid(uuid));

		uuid = new byte[17];
		assertFalse("UUID byte array with length greater than 16 should be invalid .", UuidValidator.isValid(uuid));

		try {
			uuid = null;
			UuidValidator.validate(uuid);
			fail();
		} catch (InvalidUuidException e) {
			// Success
		}
	}

	@Test
	public void testIsValidString() {

		String uuid = null; // Null
		assertFalse("Null UUID string should be invalid.", UuidValidator.isValid(uuid));

		uuid = ""; // length: 0
		assertFalse("UUID with empty string should be invalid.", UuidValidator.isValid(uuid));

		uuid = "01234567-89ab-4def-abcd-ef0123456789"; // String length = 36
		assertTrue("UUID with length equals to 36 should be valid.", UuidValidator.isValid(uuid));

		uuid = "0123456789ab4defabcdef0123456789"; // String length = 32
		assertTrue("UUID with length equals to 32 should be valid.", UuidValidator.isValid(uuid));

		uuid = "01234567-89ab-4def-abcd-ef0123456789"; // All lower case
		assertTrue("UUID in lower case should be valid.", UuidValidator.isValid(uuid));

		uuid = "01234567-89AB-4DEF-ABCD-EF0123456789"; // All upper case
		assertTrue("UUID in upper case should valid.", UuidValidator.isValid(uuid));

		uuid = "01234567-89ab-4DEF-abcd-EF0123456789"; // Mixed case
		assertTrue("UUID in upper and lower case should valid.", UuidValidator.isValid(uuid));

		uuid = "0123456789AB4DEFABCDEF0123456789"; // All upper case, without hyphen
		assertTrue("UUID in upper case without hyphen should be valid.", UuidValidator.isValid(uuid));

		uuid = "0123456789ab4defabcdef0123456789"; // All lower case, without hyphen
		assertTrue("UUID in lower case without hyphen should be valid.", UuidValidator.isValid(uuid));

		uuid = "0123456789ab4DEFabcdEF0123456789"; // mixed case, without hyphen
		assertTrue("UUID in upper and lower case without hyphen should be valid.", UuidValidator.isValid(uuid));

		uuid = "01234567-89ab-4def-abcd-SOPQRSTUVXYZ"; // String with non hexadecimal chars
		assertFalse("UUID string with non hexadecimal chars should be invalid.", UuidValidator.isValid(uuid));

		uuid = "01234567-89ab-4def-!@#$-ef0123456789"; // String with non alphanumeric chars
		assertFalse("UUID string non alphanumeric chars should be invalid.", UuidValidator.isValid(uuid));

		try {
			uuid = null;
			UuidValidator.validate(uuid);
			fail();
		} catch (InvalidUuidException e) {
			// Success
		}
	}
}
