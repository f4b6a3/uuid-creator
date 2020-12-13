package com.github.f4b6a3.uuid.codec.uri;

import static org.junit.Assert.*;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UuidCodec;

public class UuidUriCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 1_000;

	private static final String URN_PREFIX = "urn:uuid:";

	private static final String RFC4122_URN_PATTERN = "^urn:uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-6][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	@Test
	public void testEncode() {
		UuidCodec<URI> codec = new UuidUriCodec();
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
		UuidCodec<URI> codec = new UuidUriCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(URI.create(URN_PREFIX + string));
			assertEquals(string, uuid.toString());
		}
	}

	private void checkPattern(String string, String pattern) {
		assertTrue("Doesn't match the pattern: " + string, string.toString().matches(pattern));
	}
}
