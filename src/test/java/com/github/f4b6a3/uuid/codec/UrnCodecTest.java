package com.github.f4b6a3.uuid.codec;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UrnCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

public class UrnCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	private static final String URN_PREFIX = "urn:uuid:";

	private static final String RFC4122_URN_PATTERN = "^urn:uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-6][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	@Test
	public void testEncode() {
		UrnCodec codec = new UrnCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID random = UUID.randomUUID();
			String expected = URN_PREFIX + random.toString();
			String actual = codec.encode(random);
			checkPattern(actual, RFC4122_URN_PATTERN);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testDecode() {
		UrnCodec codec = new UrnCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(URN_PREFIX + string);
			assertEquals(string, uuid.toString());
		}
	}

	@Test
	public void testEncodeAndDecode() {
		UrnCodec codec = new UrnCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String urn = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(urn)); // decode back
		}
	}

	@Test
	public void testIsUuidUrn() {

		{
			{
				String string = URN_PREFIX + new UUID(0L, 0L).toString();
				assertTrue("Should be valid", UrnCodec.isUuidUrn(string));
			}
			{
				String string = URN_PREFIX + UUID.randomUUID().toString();
				assertTrue("Should be valid", UrnCodec.isUuidUrn(string));
			}
		}

		{
			{
				// null object
				String string = null;
				assertFalse("Should not be valid", UrnCodec.isUuidUrn(string));
			}

			{
				// empty string
				String string = "";
				assertFalse("Should not be valid", UrnCodec.isUuidUrn(string));
			}

			{
				// incomplete string
				String string = URN_PREFIX;
				assertFalse("Should not be valid", UrnCodec.isUuidUrn(string));
			}
		}
	}

	@Test
	public void testEncodeInvalidUuidException() {

		UrnCodec codec = new UrnCodec();

		{
			try {
				UUID uuid = new UUID(0L, 0L);
				codec.encode(uuid);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				UUID uuid = UUID.randomUUID();
				codec.encode(uuid);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}
		}

		{
			UUID uuid = null;
			try {
				codec.encode(uuid);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}
		}
	}

	@Test
	public void testDecodeInvalidUuidException() {

		UrnCodec codec = new UrnCodec();

		{
			try {
				String string = URN_PREFIX + new UUID(0L, 0L).toString();
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				String string = URN_PREFIX + UUID.randomUUID().toString();
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}
		}

		{
			try {
				// null object
				String string = null;
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// empty string
				String string = "";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// incomplete string
				String string = URN_PREFIX;
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}
		}
	}

	private void checkPattern(String string, String pattern) {
		assertTrue("Doesn't match the pattern: " + string, string.matches(pattern));
	}
}
