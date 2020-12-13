package com.github.f4b6a3.uuid.codec;

import static org.junit.Assert.*;
import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public class UuidStringCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 1_000;

	private static final String URN_PREFIX = "urn:uuid:";

	private static final String RFC4122_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-6][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	@Test
	public void testEncode() {

		UuidCodec<String> codec = new UuidStringCodec();

		// UuidCreator.toString();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID random = UUID.randomUUID();
			String expected = random.toString();
			String actual = UuidCreator.toString(random);
			checkPattern(actual, RFC4122_PATTERN);
			assertEquals(expected, actual);
		}

		// UuidBytesCodec.encode();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID random = UUID.randomUUID();
			String expected = random.toString();
			String actual = codec.encode(random);
			checkPattern(actual, RFC4122_PATTERN);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testDecode() {

		UuidCodec<String> codec = new UuidStringCodec();

		// UuidCreator.fromString();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = UuidCreator.fromString(string);
			assertEquals(string, uuid.toString());
		}

		// Lower case with hyphens
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(string);
			assertEquals(string, uuid.toString());
		}

		// Lower case without hyphens
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(string.replace("-", ""));
			assertEquals(string, uuid.toString());
		}

		// Upper case with hyphens
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(string.toUpperCase());
			assertEquals(string, uuid.toString());
		}

		// Upper case without hyphens
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(string.toUpperCase().replace("-", ""));
			assertEquals(string, uuid.toString());
		}

		// With URN prefix: "urn:uuid:"
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(URN_PREFIX + string);
			assertEquals(string, uuid.toString());
		}

		// With curly braces: '{' and '}'
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode("{" + string + "}");
			assertEquals(string, uuid.toString());
		}

	}

	@Test
	public void testGetJavaVersion() {

		final String key = "java.version";
		final String backup = System.getProperty(key);

		System.setProperty(key, "1.8");
		assertEquals(8, UuidStringCodec.getJavaVersion());

		System.setProperty(key, "1.8.0");
		assertEquals(8, UuidStringCodec.getJavaVersion());

		System.setProperty(key, "8");
		assertEquals(8, UuidStringCodec.getJavaVersion());

		System.setProperty(key, "8.0");
		assertEquals(8, UuidStringCodec.getJavaVersion());

		System.setProperty(key, "8.0.0");
		assertEquals(8, UuidStringCodec.getJavaVersion());

		System.setProperty(key, "9");
		assertEquals(9, UuidStringCodec.getJavaVersion());

		System.setProperty(key, "9.0");
		assertEquals(9, UuidStringCodec.getJavaVersion());

		System.setProperty(key, "9.0.0");
		assertEquals(9, UuidStringCodec.getJavaVersion());

		System.setProperty(key, "10");
		assertEquals(10, UuidStringCodec.getJavaVersion());

		System.setProperty(key, "10.0");
		assertEquals(10, UuidStringCodec.getJavaVersion());

		System.setProperty(key, "10.0.0");
		assertEquals(10, UuidStringCodec.getJavaVersion());

		System.setProperty(key, backup);
	}

	private void checkPattern(String string, String pattern) {
		assertTrue("Doesn't match the pattern: " + string, string.toString().matches(pattern));
	}
}
