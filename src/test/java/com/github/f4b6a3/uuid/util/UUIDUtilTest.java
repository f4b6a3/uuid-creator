package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UUIDGenerator;
import com.github.f4b6a3.uuid.factory.abst.AbstractUUIDCreator;

import static com.github.f4b6a3.uuid.util.UUIDUtil.*;

public class UUIDUtilTest {

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
		UUID uuid = UUIDGenerator.getTimeBasedCreator().withMulticastNodeIdentifier(nodeIdentifier1).create();
		long nodeIdentifier2 = extractNodeIdentifier(uuid);
		assertEquals(nodeIdentifier1, nodeIdentifier2);
	}

	@Test
	public void testExtractTimestamp() {

		Instant instant1 = Instant.now().truncatedTo(ChronoUnit.MILLIS);
		long timestamp1 = TimestampUtil.toTimestamp(instant1);

		UUID uuid = UUIDGenerator.getTimeBasedCreator().withInstant(instant1).create();
		long timestamp2 = extractTimestamp(uuid);
		assertEquals(timestamp1, timestamp2);

		uuid = UUIDGenerator.getOrderedCreator().withInstant(instant1).create();
		timestamp2 = extractTimestamp(uuid);
		assertEquals(timestamp1, timestamp2);
	}

	@Test
	public void testExtractInstant() {
		Instant instant1 = Instant.now().truncatedTo(ChronoUnit.MILLIS);

		UUID uuid = UUIDGenerator.getTimeBasedCreator().withInstant(instant1).create();
		Instant instant2 = extractInstant(uuid);
		assertEquals(instant1, instant2);

		uuid = UUIDGenerator.getOrderedCreator().withInstant(instant1).create();
		instant2 = extractInstant(uuid);
		assertEquals(instant1, instant2);
	}

	@Test
	public void testDCESecurityLocalDomain() {
		byte localDomain1 = 42;
		int localIdentifier1 = 1701;
		UUID uuid = UUIDGenerator.getDCESecurity(localDomain1, localIdentifier1);
		byte localDomain2 = extractDCESecurityLocalDomain(uuid);
		assertEquals(localDomain1, localDomain2);
	}

	@Test
	public void testDCESecurityLocalIdentifier() {
		byte localDomain1 = 42;
		int localIdentifier1 = 1701;
		UUID uuid = UUIDGenerator.getDCESecurity(localDomain1, localIdentifier1);
		int localIdentifier2 = extractDCESecurityLocalIdentifier(uuid);
		assertEquals(localIdentifier1, localIdentifier2);
	}

	@Test
	public void testIsRFC4122Variant() {
		UUID uuid1 = AbstractUUIDCreator.NAMESPACE_DNS;
		UUID uuid2 = AbstractUUIDCreator.NIL_UUID;

		assertTrue(isRFC4122Variant(uuid1));
		assertFalse(isRFC4122Variant(uuid2));
	}

	@Test
	public void testExtractDCESecurityTimestamp() {

		byte localDomain = (byte) 42;
		int localIdentifier = 1701;
		
		Instant instant1 = Instant.now();
		long timestamp1 = TimestampUtil.toTimestamp(instant1);
		UUID uuid = UUIDGenerator.getDCESecurity(localDomain, localIdentifier);
		long timestamp2 = extractDCESecurityTimestamp(uuid);

		assertEquals(timestamp1 & 0xffffffff00000000L, timestamp2);
	}

	@Test
	public void testExtractDCESecurityInstant() {

		byte localDomain = (byte) 42;
		int localIdentifier = 1701;
		
		Instant instant1 = Instant.now();
		UUID uuid = UUIDGenerator.getDCESecurity(localDomain, localIdentifier);

		Instant instant2 = extractDCESecurityInstant(uuid);

		// The expected Instant is trunked like the DCE Security does internally
		long expectedTimestamp = TimestampUtil.toTimestamp(instant1) & 0xffffffff00000000L;
		Instant expectedInstant = TimestampUtil.toInstant(expectedTimestamp);

		assertEquals(expectedInstant, instant2);
	}

	@Test()
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
			extractDCESecurityInstant(uuid);
			fail();
		} catch (UnsupportedOperationException e) {
		}

		try {
			extractDCESecurityTimestamp(uuid);
			fail();
		} catch (UnsupportedOperationException e) {
		}

		try {
			extractDCESecurityLocalDomain(uuid);
			fail();
		} catch (UnsupportedOperationException e) {
		}

		try {
			extractDCESecurityLocalIdentifier(uuid);
			fail();
		} catch (UnsupportedOperationException e) {
		}
	}

}
