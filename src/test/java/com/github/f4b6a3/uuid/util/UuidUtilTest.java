package com.github.f4b6a3.uuid.util;

import static com.github.f4b6a3.uuid.util.UuidUtil.*;
import static org.junit.Assert.*;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.enums.UuidLocalDomain;
import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.factory.rfc4122.DceSecurityFactory;
import com.github.f4b6a3.uuid.factory.rfc4122.TimeBasedFactory;
import com.github.f4b6a3.uuid.factory.rfc4122.TimeOrderedFactory;

public class UuidUtilTest {

	protected static final int DEFAULT_LOOP_MAX = 100;

	private static final long GREG_TIMESTAMP_MASK = 0x0fffffffffffffffL;
	private static final long DCE_TIMESTAMP_MASK = 0xffffffff00000000L;
	private static final long CLOCK_SEQUENCE_MASK = 0x0000000000003fffL;
	private static final long NODE_IDENTIFIER_MASK = 0x0000ffffffffffffL;

	@Test
	public void testIsNil() {
		UUID uuid = new UUID(0, 0);
		assertTrue(isNil(uuid));
	}

	@Test
	public void testIsMax() {
		UUID uuid = new UUID(-1L, -1L);
		assertTrue(isMax(uuid));
	}

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
	public void testCheckCompatibility() {
		UUID uuid = UuidCreator.getTimeBased();
		assertEquals(uuid.timestamp(), getTimestamp(uuid));
		assertEquals(uuid.clockSequence(), getClockSequence(uuid));
		assertEquals(uuid.node(), getNodeIdentifier(uuid));
		assertEquals(uuid.version(), getVersion(uuid).getValue());
		assertEquals(uuid.variant(), getVariant(uuid).getValue());
	}

	@Test
	public void testGetTimestamp() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Instant instant = UuidTime.fromGregTimestamp(ThreadLocalRandom.current().nextLong() & GREG_TIMESTAMP_MASK);
			long unixTimestamp = UuidTime.toUnixTimestamp(instant);
			long gregTimestamp = UuidTime.toGregTimestamp(instant);

			TimeBasedFactory factory1 = TimeBasedFactory.builder().withTimeFunction(() -> unixTimestamp).build();
			UUID uuid1 = factory1.create();
			long gregTimestamp1 = getTimestamp(uuid1);
			assertEquals(gregTimestamp, gregTimestamp1);

			TimeOrderedFactory factory2 = TimeOrderedFactory.builder().withTimeFunction(() -> unixTimestamp).build();
			UUID uuid2 = factory2.create();
			long gregTimestamp2 = getTimestamp(uuid2);
			assertEquals(gregTimestamp, gregTimestamp2);

			DceSecurityFactory factory3 = DceSecurityFactory.builder().withTimeFunction(() -> unixTimestamp).build();
			UUID uuid3 = factory3.create(UuidLocalDomain.LOCAL_DOMAIN_PERSON, 0);
			long gregTimestamp3 = getTimestamp(uuid3);
			assertEquals(gregTimestamp & 0xffffffff00000000L, gregTimestamp3);
		}
	}

	@Test
	public void testGetInstant() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Instant instant = UuidTime.fromGregTimestamp(ThreadLocalRandom.current().nextLong() & GREG_TIMESTAMP_MASK);

			TimeBasedFactory factory1 = TimeBasedFactory.builder().withInstant(instant).build();
			UUID uuid1 = factory1.create();
			Instant instant1 = getInstant(uuid1);
			assertEquals(instant, instant1);

			TimeOrderedFactory factory2 = TimeOrderedFactory.builder().withInstant(instant).build();
			UUID uuid2 = factory2.create();
			Instant instant2 = getInstant(uuid2);
			assertEquals(instant, instant2);

			DceSecurityFactory factory3 = DceSecurityFactory.builder().withInstant(instant).build();
			UUID uuid3 = factory3.create(UuidLocalDomain.LOCAL_DOMAIN_PERSON, 0);
			Instant instant3 = getInstant(uuid3);
			assertEquals(UuidTime.fromGregTimestamp(UuidTime.toGregTimestamp(instant3) & DCE_TIMESTAMP_MASK), instant3);
		}
	}

	@Test
	public void testGetClockSequence() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long clockSequence1 = ThreadLocalRandom.current().nextLong() & CLOCK_SEQUENCE_MASK;
			UUID uuid = TimeBasedFactory.builder().withClockSeq(clockSequence1).build().create();
			long clockSequence2 = getClockSequence(uuid);
			assertEquals(clockSequence1, clockSequence2);
		}
	}

	@Test
	public void testGetNodeIdentifier() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long nodeIdentifier1 = ThreadLocalRandom.current().nextLong() & NODE_IDENTIFIER_MASK;
			UUID uuid = TimeBasedFactory.builder().withNodeId(nodeIdentifier1).build().create();
			long nodeIdentifier2 = getNodeIdentifier(uuid);
			assertEquals(nodeIdentifier1, nodeIdentifier2);
		}
	}

	@Test
	public void testGetLocalDomain() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			int localIdentifier1 = ThreadLocalRandom.current().nextInt();
			byte localDomain1 = (byte) ThreadLocalRandom.current().nextInt();
			UUID uuid = UuidCreator.getDceSecurity(localDomain1, localIdentifier1);
			byte localDomain2 = getLocalDomain(uuid);
			assertEquals(localDomain1, localDomain2);
		}
	}

	@Test
	public void testGetLocalIdentifier() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			int localIdentifier1 = ThreadLocalRandom.current().nextInt();
			byte localDomain1 = (byte) ThreadLocalRandom.current().nextInt();
			UUID uuid = UuidCreator.getDceSecurity(localDomain1, localIdentifier1);
			int localIdentifier2 = getLocalIdentifier(uuid);
			assertEquals(localIdentifier1, localIdentifier2);
		}
	}

	@Test
	public void testIsRfc4122() {
		UUID uuid1 = UuidNamespace.NAMESPACE_DNS.getValue();
		UUID uuid2 = UuidCreator.getNil();

		assertTrue(isRfc4122(uuid1));
		assertFalse(isRfc4122(uuid2));
	}

	@Test
	public void testGetAllCatchExceptions() {

		UUID uuid = UUID.randomUUID();

		try {
			getInstant(uuid);
			fail();
		} catch (IllegalArgumentException e) {
			// Success
		}

		try {
			getTimestamp(uuid);
			fail();
		} catch (IllegalArgumentException e) {
			// Success
		}

		try {
			getClockSequence(uuid);
			fail();
		} catch (IllegalArgumentException e) {
			// Success
		}

		try {
			getNodeIdentifier(uuid);
			fail();
		} catch (IllegalArgumentException e) {
			// Success
		}

		try {
			getLocalDomain(uuid);
			fail();
		} catch (IllegalArgumentException e) {
			// Success
		}

		try {
			getLocalIdentifier(uuid);
			fail();
		} catch (IllegalArgumentException e) {
			// Success
		}
	}
}
