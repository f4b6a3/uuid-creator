package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.UUID;

import com.github.f4b6a3.commons.util.ByteUtil;
import com.github.f4b6a3.uuid.UuidCreator;

import static com.github.f4b6a3.uuid.util.UuidUtil.*;

public class UuidConverterTest {

	@Test
	public void testToString() {
		UUID uuid = UuidCreator.getTimeBased();
		String string = UuidConverter.toString(uuid);
		UUID result = UUID.fromString(string);
		assertEquals(uuid, result);
	}

	@Test
	public void testFromString() {
		String string = UuidCreator.getTimeBased().toString();
		UUID uuid = UuidConverter.fromString(string);
		String result = uuid.toString();
		assertEquals(string, result);
	}

	@Test
	public void testToStringSample() {
		
		UUID sample1 = UUID.fromString("02d2c2db-a8e2-478d-9c12-5bfcdba221ae");
		UUID sample2 = UUID.fromString("4c7d486a-050b-407d-96b7-c85c77c44c8a");
		UUID sample3 = UUID.fromString("c179669c-c2e4-4049-84cc-2e42bc831f43");
		
		String uuid1 = UuidConverter.toString(sample1);
		String uuid2 = UuidConverter.toString(sample2);
		String uuid3 = UuidConverter.toString(sample3);
		
		assertEquals(sample1.toString(), uuid1);
		assertEquals(sample2.toString(), uuid2);
		assertEquals(sample3.toString(), uuid3);
	}
	
	@Test
	public void testFromStringSample() {

		// With dashes
		String sample1 = "538a1187-40b7-4b00-ad1c-15db126d1f77";
		String sample2 = "cd202f27-fd38-4c42-ba6a-00b7cc14e08a";
		String sample3 = "b499319b-369f-450c-803e-ae7296b27821";

		UUID uuid1 = UuidConverter.fromString(sample1);
		UUID uuid2 = UuidConverter.fromString(sample2);
		UUID uuid3 = UuidConverter.fromString(sample3);

		assertEquals(sample1, uuid1.toString());
		assertEquals(sample2, uuid2.toString());
		assertEquals(sample3, uuid3.toString());

		// Without dashes
		String sample4 = "0e95ea9abc304ce19db7bb1a4b84a489";
		String sample5 = "b40904ba51e248bb972a07a49c3c0e7b";
		String sample6 = "8c22a87adc374cf396f3c3297756a670";

		UUID uuid4 = UuidConverter.fromString(sample4);
		UUID uuid5 = UuidConverter.fromString(sample5);
		UUID uuid6 = UuidConverter.fromString(sample6);

		assertEquals(sample4, uuid4.toString().replace("-", ""));
		assertEquals(sample5, uuid5.toString().replace("-", ""));
		assertEquals(sample6, uuid6.toString().replace("-", ""));
	}

	@Test
	public void testToBytes() {
		UUID uuid1 = UuidCreator.getTimeBased();
		byte[] bytes = UuidConverter.toBytes(uuid1);
		long msb = ByteUtil.toNumber(ByteUtil.copy(bytes, 0, 8));
		long lsb = ByteUtil.toNumber(ByteUtil.copy(bytes, 8, 16));
		UUID uuid2 = new UUID(msb, lsb);
		assertEquals(uuid1, uuid2);
	}

	@Test
	public void testToUuidFromBytes() {
		UUID uuid1 = UuidCreator.getTimeBased();
		byte[] bytes = UuidConverter.toBytes(uuid1);
		UUID uuid2 = UuidConverter.fromBytes(bytes);
		assertEquals(uuid1, uuid2);
	}

	@Test
	public void testToTimeOrderedUuid() {
		UUID uuid1 = UuidCreator.getTimeBased();
		UUID uuid2 = UuidConverter.toTimeOrderedUuid(uuid1);

		assertTrue(isTimeOrdered(uuid2));
	}

	@Test
	public void testToTimeBasedUuid() {
		UUID uuid1 = UuidCreator.getTimeOrdered();
		UUID uuid2 = UuidConverter.toTimeBasedUuid(uuid1);

		assertTrue(isTimeBased(uuid2));
	}

	@Test
	public void testToTimeOrderedUuidSample() {

		String sample1 = "5714f720-1268-11e7-a24b-96d95aa38c32"; // UUID v1
		String sample2 = "1e712685-714f-6720-a24b-96d95aa38c32"; // UUID v6

		UUID uuidv1 = UUID.fromString(sample1);
		UUID uuidv6 = UUID.fromString(sample2);

		UUID result = UuidConverter.toTimeOrderedUuid(uuidv1);

		assertEquals(uuidv6, result);
	}

	@Test
	public void testToTimeBasedUuidSample() {

		String sample1 = "1e71269a-b116-6740-a694-68c004266291"; // UUID v6
		String sample2 = "ab116740-1269-11e7-a694-68c004266291"; // UUID v1

		UUID uuidv6 = UUID.fromString(sample1);
		UUID uuidv1 = UUID.fromString(sample2);

		UUID result = UuidConverter.toTimeBasedUuid(uuidv6);

		assertEquals(uuidv1, result);
	}

	@Test
	public void testToAndFromMsGuid() {

		// Random-based
		UUID uuid1 = UuidCreator.getRandomBased();
		UUID guid1 = UuidConverter.toAndFromMsGuid(uuid1);
		UUID result1 = UuidConverter.toAndFromMsGuid(guid1);
		assertEquals(uuid1, result1);

		// Time-based
		UUID uuid2 = UuidCreator.getTimeBased();
		UUID guid2 = UuidConverter.toAndFromMsGuid(uuid2);
		UUID result2 = UuidConverter.toAndFromMsGuid(guid2);
		assertEquals(uuid2, result2);
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
}
