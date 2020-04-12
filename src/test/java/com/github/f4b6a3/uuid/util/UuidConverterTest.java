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
		UUID uuid1 = UuidCreator.getTimeBased();
		String string = UuidConverter.toString(uuid1);
		UUID uuid2 = UUID.fromString(string);
		assertEquals(uuid1, uuid2);
	}

	@Test
	public void testToUuidFromString() {
		UUID uuid1 = UuidCreator.getTimeBased();
		String string = UuidConverter.toString(uuid1);
		UUID uuid2 = UuidConverter.fromString(string);
		assertEquals(uuid1, uuid2);
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
	public void testToMsGuid() {

		// Test with a fixed value
		UUID uuid1 = new UUID(0x0011223344551677L, 0x8888888888888888L);
		UUID uuid2 = UuidConverter.toAndFromMsGuid(uuid1);
		long timestamp = uuid2.getMostSignificantBits();
		assertEquals(0x3322110055447716L, timestamp);

		// Test with a generated value
		UUID uuid3 = UuidCreator.getTimeBased();
		UUID uuid4 = UuidConverter.toAndFromMsGuid(uuid3);
		UUID uuid5 = UuidConverter.toAndFromMsGuid(uuid4);
		assertEquals(uuid3, uuid5);

	}

	@Test
	public void testToMsGuidFromRandomBasedUuid() {
		// Test with a generated value
		UUID uuid1 = UuidCreator.getRandomBased();
		UUID uuid2 = UuidConverter.toAndFromMsGuid(uuid1);
		UUID uuid3 = UuidConverter.toAndFromMsGuid(uuid2);
		assertEquals(uuid1, uuid3);
	}
}
