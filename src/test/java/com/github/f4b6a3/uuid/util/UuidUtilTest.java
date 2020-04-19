package com.github.f4b6a3.uuid.util;

import static com.github.f4b6a3.uuid.util.UuidUtil.*;
import static org.junit.Assert.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.enums.UuidLocalDomain;
import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.exception.IllegalUuidException;
import com.github.f4b6a3.uuid.strategy.TimestampStrategy;
import com.github.f4b6a3.uuid.strategy.timestamp.FixedTimestampStretegy;
import com.github.f4b6a3.uuid.util.UuidTimeUtil;

public class UuidUtilTest {

	@Test
	public void testIsNameBasedMd5() {
		UUID uuid = UUID.nameUUIDFromBytes("test".getBytes());
		assertTrue(isNameBasedMd5(uuid));
	}

	@Test
	public void testIsRandomBased() {
		UUID uuid = UUID.randomUUID();
		assertTrue(isRandomBased(uuid));
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
		long timestamp1 = UuidTimeUtil.toTimestamp(instant1);
		TimestampStrategy strategy = new FixedTimestampStretegy(instant1);

		UUID uuid = UuidCreator.getTimeBasedCreator().withTimestampStrategy(strategy).create();
		long timestamp2 = extractTimestamp(uuid);
		assertEquals(timestamp1, timestamp2);

		uuid = UuidCreator.getTimeOrderedCreator().withTimestampStrategy(strategy).create();
		timestamp2 = extractTimestamp(uuid);
		assertEquals(timestamp1, timestamp2);
	}

	@Test
	public void testExtractInstant() {
		Instant instant1 = Instant.now().truncatedTo(ChronoUnit.MILLIS);
		TimestampStrategy strategy = new FixedTimestampStretegy(instant1);

		UUID uuid = UuidCreator.getTimeBasedCreator().withTimestampStrategy(strategy).create();
		Instant instant2 = extractInstant(uuid);
		assertEquals(instant1, instant2);

		uuid = UuidCreator.getTimeOrderedCreator().withTimestampStrategy(strategy).create();
		instant2 = extractInstant(uuid);
		assertEquals(instant1, instant2);
	}

	@Test
	public void testExtractLocalDomain() {
		UuidLocalDomain localDomain1 = UuidLocalDomain.LOCAL_DOMAIN_PERSON;
		int localIdentifier1 = 1701;
		UUID uuid = UuidCreator.getDceSecurity(localDomain1, localIdentifier1);
		byte localDomain2 = extractLocalDomain(uuid);
		assertEquals(localDomain1.getValue(), localDomain2);
	}

	@Test
	public void testExctractLocalIdentifier() {
		UuidLocalDomain localDomain1 = UuidLocalDomain.LOCAL_DOMAIN_PERSON;
		int localIdentifier1 = 1701;
		UUID uuid = UuidCreator.getDceSecurity(localDomain1, localIdentifier1);
		int localIdentifier2 = extractLocalIdentifier(uuid);
		assertEquals(localIdentifier1, localIdentifier2);
	}

	@Test
	public void testIsRfc4122() {
		UUID uuid1 = UuidNamespace.NAMESPACE_DNS.getValue();
		UUID uuid2 = UuidCreator.getNil();

		assertTrue(isRfc4122(uuid1));
		assertFalse(isRfc4122(uuid2));
	}

	@Test
	public void testExtractDceSecurityTimestamp() {

		UuidLocalDomain localDomain = UuidLocalDomain.LOCAL_DOMAIN_PERSON;
		int localIdentifier = 1701;

		Instant instant1 = Instant.now();
		long timestamp1 = UuidTimeUtil.toTimestamp(instant1);
		UUID uuid = UuidCreator.getDceSecurity(localDomain, localIdentifier);
		long timestamp2 = extractTimestamp(uuid);

		assertEquals(timestamp1 & 0xffffffff00000000L, timestamp2);
	}

	@Test
	public void testExtractDceSecurityInstant() {

		UuidLocalDomain localDomain = UuidLocalDomain.LOCAL_DOMAIN_PERSON;
		int localIdentifier = 1701;

		Instant instant1 = Instant.now();
		UUID uuid = UuidCreator.getDceSecurity(localDomain, localIdentifier);

		Instant instant2 = extractInstant(uuid);

		// The expected Instant is trunked like the DCE Security does internally
		long expectedTimestamp = UuidTimeUtil.toTimestamp(instant1) & 0xffffffff00000000L;
		Instant expectedInstant = UuidTimeUtil.toInstant(expectedTimestamp);

		assertEquals(expectedInstant, instant2);
	}

	@Test
	public void testExtractAllCatchExceptions() {
		UUID uuid = UUID.randomUUID();

		try {
			extractInstant(uuid);
			fail();
		} catch (IllegalUuidException e) {
			// Success
		}

		try {
			extractTimestamp(uuid);
			fail();
		} catch (IllegalUuidException e) {
			// Success
		}

		try {
			extractNodeIdentifier(uuid);
			fail();
		} catch (IllegalUuidException e) {
			// Success
		}

		try {
			extractInstant(uuid);
			fail();
		} catch (IllegalUuidException e) {
			// Success
		}

		try {
			extractTimestamp(uuid);
			fail();
		} catch (IllegalUuidException e) {
			// Success
		}

		try {
			extractLocalDomain(uuid);
			fail();
		} catch (IllegalUuidException e) {
			// Success
		}

		try {
			extractLocalIdentifier(uuid);
			fail();
		} catch (IllegalUuidException e) {
			// Success
		}
	}
}
