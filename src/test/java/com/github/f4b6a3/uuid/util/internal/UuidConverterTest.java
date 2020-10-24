package com.github.f4b6a3.uuid.util.internal;

import static com.github.f4b6a3.uuid.util.UuidUtil.*;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.util.UuidConverter;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

public class UuidConverterTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;
	
	private static final String URN_PREFIX = "urn:uuid:";
	
	private static final String RFC4122_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-6][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	@Test
	public void testToString() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID random = UUID.randomUUID();
			String expected = random.toString();
			String actual = UuidConverter.toString(random);
			
			checkPattern(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testFromString() {

		// Lower case with hyphens
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = UuidConverter.fromString(string);
			assertEquals(string, uuid.toString());
		}

		// Lower case without hyphens
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = UuidConverter.fromString(string.replace("-", ""));
			assertEquals(string, uuid.toString());
		}

		// Upper case with hyphens
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = UuidConverter.fromString(string.toUpperCase());
			assertEquals(string, uuid.toString());
		}

		// Upper case without hyphens
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = UuidConverter.fromString(string.toUpperCase().replace("-", ""));
			assertEquals(string, uuid.toString());
		}
		
		// With URN prefix: "urn:uuid:"
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = UuidConverter.fromString(URN_PREFIX + string);
			assertEquals(string, uuid.toString());
		}
		
		// With curly braces: '{' and '}'
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = UuidConverter.fromString("{" + string + "}");
			assertEquals(string, uuid.toString());
		}
	}

	@Test
	public void testToBytes() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid1 = UUID.randomUUID();
			byte[] bytes = UuidConverter.toBytes(uuid1);
			long msb = ByteUtil.toNumber(bytes, 0, 8);
			long lsb = ByteUtil.toNumber(bytes, 8, 16);
			UUID uuid2 = new UUID(msb, lsb);
			assertEquals(uuid1, uuid2);
		}
	}

	@Test
	public void testFromBytes() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid1 = UUID.randomUUID();
			byte[] bytes = UuidConverter.toBytes(uuid1);
			UUID uuid2 = UuidConverter.fromBytes(bytes);
			assertEquals(uuid1, uuid2);
		}
	}

	@Test
	public void testIsTimeOrderedUuid() {
		UUID uuid1 = UuidCreator.getTimeBased();
		UUID uuid2 = UuidConverter.toTimeOrderedUuid(uuid1);

		assertTrue(isTimeOrdered(uuid2));
	}

	@Test
	public void testIsTimeBasedUuid() {
		UUID uuid1 = UuidCreator.getTimeOrdered();
		UUID uuid2 = UuidConverter.toTimeBasedUuid(uuid1);

		assertTrue(isTimeBased(uuid2));
	}

	@Test
	public void testToTimeOrderedUuid() {

		// Using samples
		String sample1 = "5714f720-1268-11e7-a24b-96d95aa38c32"; // UUID v1
		String sample2 = "1e712685-714f-6720-a24b-96d95aa38c32"; // UUID v6
		UUID uuidv1 = UUID.fromString(sample1);
		UUID uuidv6 = UUID.fromString(sample2);
		UUID result = UuidConverter.toTimeOrderedUuid(uuidv1);
		assertEquals(uuidv6, result);

		// Using loop
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuidv6 = UuidCreator.getTimeOrdered();
			uuidv1 = UuidConverter.toTimeBasedUuid(uuidv6);
			result = UuidConverter.toTimeOrderedUuid(uuidv1);
			assertEquals(uuidv6, result);
		}
	}

	@Test
	public void testToTimeBasedUuid() {

		// Using samples
		String sample1 = "1e71269a-b116-6740-a694-68c004266291"; // UUID v6
		String sample2 = "ab116740-1269-11e7-a694-68c004266291"; // UUID v1
		UUID uuidv6 = UUID.fromString(sample1);
		UUID uuidv1 = UUID.fromString(sample2);
		UUID result = UuidConverter.toTimeBasedUuid(uuidv6);
		assertEquals(uuidv1, result);

		// Using loop
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuidv1 = UuidCreator.getTimeBased();
			uuidv6 = UuidConverter.toTimeOrderedUuid(uuidv1);
			result = UuidConverter.toTimeBasedUuid(uuidv6);
			assertEquals(uuidv1, result);
		}
	}

	@Test
	public void testToAndFromMsGuid() {

		// Using loop - Random-based
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid1 = UuidCreator.getRandomBased();
			UUID guid1 = UuidConverter.toAndFromMsGuid(uuid1);
			UUID result1 = UuidConverter.toAndFromMsGuid(guid1);
			assertEquals(uuid1, result1);
		}

		// Using loop - Time-based
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid2 = UuidCreator.getTimeBased();
			UUID guid2 = UuidConverter.toAndFromMsGuid(uuid2);
			UUID result2 = UuidConverter.toAndFromMsGuid(guid2);
			assertEquals(uuid2, result2);
		}
	}

	@Test
	public void testToMsGuidSample() {

		String sample1 = "917f116e-3447-4524-a959-ece721413f93"; // UUID v4
		String sample2 = "6e117f91-4734-2445-a959-ece721413f93"; // MS GUID

		UUID uuid = UUID.fromString(sample1);
		UUID guid = UUID.fromString(sample2);
		UUID result = UuidConverter.toAndFromMsGuid(uuid);

		assertEquals(guid, result);
	}

	@Test
	public void testFromMsGuidSample() {

		String sample1 = "8ECF7543-F5DE-F643-92F3-074D34A4CE35"; // MS GUID
		String sample2 = "4375CF8E-DEF5-43F6-92F3-074D34A4CE35"; // UUID v4

		UUID guid = UUID.fromString(sample1);
		UUID uuid = UUID.fromString(sample2);
		UUID result = UuidConverter.toAndFromMsGuid(guid);

		assertEquals(uuid, result);
	}
	
	private void checkPattern(String string) {
		assertTrue(string.toString().matches(RFC4122_PATTERN));
	}
}
