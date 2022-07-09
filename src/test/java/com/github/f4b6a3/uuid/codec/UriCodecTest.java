package com.github.f4b6a3.uuid.codec;

import static org.junit.Assert.*;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

import com.github.f4b6a3.uuid.exception.InvalidUuidException;

public class UriCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	private static final String URN_PREFIX = "urn:uuid:";

	private static final String RFC4122_URN_PATTERN = "^urn:uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-6][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	@Test
	public void testEncode() {
		UriCodec codec = new UriCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID random = UUID.randomUUID();
			String expected = URN_PREFIX + random.toString();
			String actual = codec.encode(random).toString();
			checkPattern(actual, RFC4122_URN_PATTERN);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testDecode() {
		UriCodec codec = new UriCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(URI.create(URN_PREFIX + string));
			assertEquals(string, uuid.toString());
		}
	}

	@Test
	public void testEncodeAndDecode() {
		UriCodec codec = new UriCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			URI uri = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(uri)); // decode back
		}
	}

	@Test
	public void testIsUuidUri() {

		{
			{
				URI uri = URI.create(URN_PREFIX + new UUID(0L, 0L).toString());
				assertTrue("Should be valid", UriCodec.isUuidUri(uri));
			}
			{
				URI uri = URI.create(URN_PREFIX + UUID.randomUUID().toString());
				assertTrue("Should be valid", UriCodec.isUuidUri(uri));
			}
		}

		{
			{
				// null object
				URI uri = null;
				assertFalse("Should not be valid", UriCodec.isUuidUri(uri));
			}

			{
				// empty string
				URI uri = URI.create("");
				assertFalse("Should not be valid", UriCodec.isUuidUri(uri));
			}

			{
				// incomplete string
				URI uri = URI.create(URN_PREFIX);
				assertFalse("Should not be valid", UriCodec.isUuidUri(uri));
			}
		}
	}

	@Test
	public void testEncodeInvalidUuidException() {

		UriCodec codec = new UriCodec();

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

		UriCodec codec = new UriCodec();

		{
			try {
				String string = URN_PREFIX + new UUID(0L, 0L).toString();
				codec.decode(URI.create(string));
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				String string = URN_PREFIX + UUID.randomUUID().toString();
				codec.decode(URI.create(string));
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}
		}

		{
			try {
				// null object
				URI uri = null;
				codec.decode(uri);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// empty string
				URI uri = URI.create("");
				codec.decode(uri);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// incomplete string
				URI uri = URI.create(URN_PREFIX);
				codec.decode(uri);
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
