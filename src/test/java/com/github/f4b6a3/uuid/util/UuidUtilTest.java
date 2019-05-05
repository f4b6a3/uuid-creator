package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.factory.abst.AbstractUuidCreator;
import com.github.f4b6a3.uuid.util.TimestampUtil;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static com.github.f4b6a3.uuid.util.UuidUtil.*;

public class UuidUtilTest {

	@Test
	public void testIsNameBasedVersion() {
		UUID uuid = UUID.nameUUIDFromBytes("test".getBytes());
		assertTrue(isNameBasedVersion(uuid));
	}

	@Test
	public void testIsRandomBasedVersion() {
		UUID uuid = UUID.randomUUID();
		assertTrue(isRandomBasedVersion(uuid));
	}

	@Test
	public void testExtractNodeIdentifier() {
		long nodeIdentifier1 = 0x111111111111L;
		UUID uuid = UuidCreator.getTimeBasedCreator().withNodeIdentifier(nodeIdentifier1).create();
		long nodeIdentifier2 = extractNodeIdentifier(uuid);
		assertEquals(nodeIdentifier1, nodeIdentifier2);
	}

	@Test
	public void testExtractTimestamp() {

		Instant instant1 = Instant.now().truncatedTo(ChronoUnit.MILLIS);
		long timestamp1 = TimestampUtil.toTimestamp(instant1);

		UUID uuid = UuidCreator.getTimeBasedCreator().withInstant(instant1).create();
		long timestamp2 = extractTimestamp(uuid);
		assertEquals(timestamp1, timestamp2);

		uuid = UuidCreator.getSequentialCreator().withInstant(instant1).create();
		timestamp2 = extractTimestamp(uuid);
		assertEquals(timestamp1, timestamp2);
	}

	@Test
	public void testExtractInstant() {
		Instant instant1 = Instant.now().truncatedTo(ChronoUnit.MILLIS);

		UUID uuid = UuidCreator.getTimeBasedCreator().withInstant(instant1).create();
		Instant instant2 = extractInstant(uuid);
		assertEquals(instant1, instant2);

		uuid = UuidCreator.getSequentialCreator().withInstant(instant1).create();
		instant2 = extractInstant(uuid);
		assertEquals(instant1, instant2);
	}

	@Test
	public void testDceSecurityLocalDomain() {
		byte localDomain1 = 42;
		int localIdentifier1 = 1701;
		UUID uuid = UuidCreator.getDceSecurity(localDomain1, localIdentifier1);
		byte localDomain2 = extractDceSecurityLocalDomain(uuid);
		assertEquals(localDomain1, localDomain2);
	}

	@Test
	public void testDceSecurityLocalIdentifier() {
		byte localDomain1 = 42;
		int localIdentifier1 = 1701;
		UUID uuid = UuidCreator.getDceSecurity(localDomain1, localIdentifier1);
		int localIdentifier2 = extractDceSecurityLocalIdentifier(uuid);
		assertEquals(localIdentifier1, localIdentifier2);
	}

	@Test
	public void testIsRfc4122Variant() {
		UUID uuid1 = AbstractUuidCreator.NAMESPACE_DNS;
		UUID uuid2 = AbstractUuidCreator.NIL_UUID;

		assertTrue(isRfc4122Variant(uuid1));
		assertFalse(isRfc4122Variant(uuid2));
	}

	@Test
	public void testExtractDceSecurityTimestamp() {

		byte localDomain = (byte) 42;
		int localIdentifier = 1701;
		
		Instant instant1 = Instant.now();
		long timestamp1 = TimestampUtil.toTimestamp(instant1);
		UUID uuid = UuidCreator.getDceSecurity(localDomain, localIdentifier);
		long timestamp2 = extractDceSecurityTimestamp(uuid);

		assertEquals(timestamp1 & 0xffffffff00000000L, timestamp2);
	}

	@Test
	public void testExtractDceSecurityInstant() {

		byte localDomain = (byte) 42;
		int localIdentifier = 1701;
		
		Instant instant1 = Instant.now();
		UUID uuid = UuidCreator.getDceSecurity(localDomain, localIdentifier);

		Instant instant2 = extractDceSecurityInstant(uuid);

		// The expected Instant is trunked like the DCE Security does internally
		long expectedTimestamp = TimestampUtil.toTimestamp(instant1) & 0xffffffff00000000L;
		Instant expectedInstant = TimestampUtil.toInstant(expectedTimestamp);

		assertEquals(expectedInstant, instant2);
	}

	@Test
	public void testFromTimeBasedToSequentialUuid(){
		UUID uuid1 = UuidCreator.getTimeBased();
		UUID uuid2 = fromTimeBasedToSequentialUuid(uuid1);
		
		assertTrue(isSequentialVersion(uuid2));
	}
	
	@Test
	public void testFromSequentialToTimeBasedUuid(){
		UUID uuid1 = UuidCreator.getSequential();
		UUID uuid2 = fromSequentialToTimeBasedUuid(uuid1);
		
		assertTrue(isTimeBasedVersion(uuid2));
	}
	
	@Test
	public void testExtractAll_catchExceptions() {
		UUID uuid = UUID.randomUUID();

		try {
			extractInstant(uuid);
			fail();
		} catch (UnsupportedOperationException e) {
		}

		try {
			extractTimestamp(uuid);
			fail();
		} catch (UnsupportedOperationException e) {
		}

		try {
			extractNodeIdentifier(uuid);
			fail();
		} catch (UnsupportedOperationException e) {
		}

		try {
			extractDceSecurityInstant(uuid);
			fail();
		} catch (UnsupportedOperationException e) {
		}

		try {
			extractDceSecurityTimestamp(uuid);
			fail();
		} catch (UnsupportedOperationException e) {
		}

		try {
			extractDceSecurityLocalDomain(uuid);
			fail();
		} catch (UnsupportedOperationException e) {
		}

		try {
			extractDceSecurityLocalIdentifier(uuid);
			fail();
		} catch (UnsupportedOperationException e) {
		}
	}
	
	@Test
	public void testFromTimeBasedToMssqlUuid_IsCorrect() {
		UUID uuid1 = new UUID(0x0011223344551677L,0x8888888888888888L);
		UUID uuid2 = UuidUtil.fromTimeBasedToMssqlUuid(uuid1);
		long timestamp = uuid2.getMostSignificantBits();
		assertEquals(0x3322110055447716L, timestamp);
	}

}
