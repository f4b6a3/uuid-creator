package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.commons.util.ByteUtil;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.enums.UuidVersion;

import static com.github.f4b6a3.uuid.util.UuidUtil.*;

public class UuidUtilTest {
	
	// Values to be used in bitwise operations
	protected static final long RFC4122_VARIANT_BITS = 0x8000000000000000L;
	
	protected static final long[] RFC4122_VERSION_BITS = {
			0x0000000000000000L, 0x0000000000001000L, 0x0000000000002000L,
			0x0000000000003000L, 0x0000000000004000L, 0x0000000000005000L };
	
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
	public void testIsRfc4122() {
		UUID uuid1 = UuidNamespace.NAMESPACE_DNS.getValue();
		UUID uuid2 = UuidCreator.getNil();

		assertTrue(isRfc4122(uuid1));
		assertFalse(isRfc4122(uuid2));
	}

	@Test
	public void testFromUuidToBytes() {
		UUID uuid1 = UuidCreator.getTimeBased();
		byte[] bytes = UuidUtil.fromUuidToBytes(uuid1);
		long msb = ByteUtil.toNumber(ByteUtil.copy(bytes, 0, 8));
		long lsb = ByteUtil.toNumber(ByteUtil.copy(bytes, 8, 16));
		UUID uuid2 = new UUID(msb, lsb);
		assertEquals(uuid1, uuid2);
	}

	@Test
	public void testFromBytesToUuid() {
		UUID uuid1 = UuidCreator.getTimeBased();
		byte[] bytes = UuidUtil.fromUuidToBytes(uuid1);
		UUID uuid2 = UuidUtil.fromBytesToUuid(bytes);
		assertEquals(uuid1, uuid2);
	}

	
	@Test
	public void testExtractDceSecurityTimestamp() {

		byte localDomain = (byte) 42;
		int localIdentifier = 1701;
		
		Instant instant1 = Instant.now();
		long timestamp1 = UuidTimeUtil.toTimestamp(instant1);
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
		long expectedTimestamp = UuidTimeUtil.toTimestamp(instant1) & 0xffffffff00000000L;
		Instant expectedInstant = UuidTimeUtil.toInstant(expectedTimestamp);

		assertEquals(expectedInstant, instant2);
	}

	@Test
	public void testFromTimeBasedUuidToSequentialUuid(){
		UUID uuid1 = UuidCreator.getTimeBased();
		UUID uuid2 = fromTimeBasedUuidToSequentialUuid(uuid1);
		
		assertTrue(isSequential(uuid2));
	}
	
	@Test
	public void testFromSequentialUuidToTimeBasedUuid(){
		UUID uuid1 = UuidCreator.getSequential();
		UUID uuid2 = fromSequentialUuidToTimeBasedUuid(uuid1);
		
		assertTrue(isTimeBased(uuid2));
	}
	
	@Test
	public void testFromTimeBasedUuidToMssqlGuidIsCorrect() {

		// Test with a fixed value
		UUID uuid1 = new UUID(0x0011223344551677L,0x8888888888888888L);
		UUID uuid2 = UuidUtil.fromUuidToMssqlGuid(uuid1);
		long timestamp = uuid2.getMostSignificantBits();
		assertEquals(0x3322110055447716L, timestamp);
		
		// Test with a generated value
		UUID uuid3 = UuidCreator.getTimeBased();
		UUID uuid4 = UuidUtil.fromUuidToMssqlGuid(uuid3);
		UUID uuid5 = UuidUtil.fromMssqlGuidToUuid(uuid4);
		assertEquals(uuid3, uuid5);
		
	}
	
	@Test
	public void testFromRandomBasedUuidToMssqlGuidIsCorrect() {
		// Test with a generated value
		UUID uuid1 = UuidCreator.getRandom();
		UUID uuid2 = UuidUtil.fromUuidToMssqlGuid(uuid1);
		UUID uuid3 = UuidUtil.fromMssqlGuidToUuid(uuid2);
		assertEquals(uuid1, uuid3);
	}
	
	@Test
	public void testExtractAllCatchExceptions() {
		UUID uuid = UUID.randomUUID();

		try {
			extractInstant(uuid);
			fail();
		} catch (UuidUtilException e) {
			// Success
		}

		try {
			extractTimestamp(uuid);
			fail();
		} catch (UuidUtilException e) {
			// Success
		}

		try {
			extractNodeIdentifier(uuid);
			fail();
		} catch (UuidUtilException e) {
			// Success
		}

		try {
			extractDceSecurityInstant(uuid);
			fail();
		} catch (UuidUtilException e) {
			// Success
		}

		try {
			extractDceSecurityTimestamp(uuid);
			fail();
		} catch (UuidUtilException e) {
			// Success
		}

		try {
			extractDceSecurityLocalDomain(uuid);
			fail();
		} catch (UuidUtilException e) {
			// Success
		}

		try {
			extractDceSecurityLocalIdentifier(uuid);
			fail();
		} catch (UuidUtilException e) {
			// Success
		}
	}
	
	@Test
	public void testIsValidLoose() {

		String ulid = null; // Null
		assertFalse("Null UUID should be invalid in loose mode.", UuidUtil.isValid(ulid));

		ulid = ""; // length: 0
		assertFalse("UUID with empty string should be invalid  in loose mode.", UuidUtil.isValid(ulid));

		ulid = "01234567-89ab-4def-abcd-ef0123456789"; // All lower case
		assertTrue("UUID in lower case should be invalid in loose mode.", UuidUtil.isValid(ulid));
		
		ulid = "01234567-89AB-4DEF-ABCD-EF0123456789"; // All upper case
		assertTrue("UUID in upper case should valid in loose mode.", UuidUtil.isValid(ulid));

		ulid = "01234567-89ab-4DEF-abcd-EF0123456789"; // Mixed case
		assertTrue("UUID in upper and lower case should valid in loose mode.", UuidUtil.isValid(ulid));

		ulid = "0123456789AB4DEFABCDEF0123456789"; // All upper case, without hyphen
		assertTrue("UUID in upper case without hyphen should be invalid in loose mode.", UuidUtil.isValid(ulid));

		ulid = "0123456789ab4defabcdef0123456789"; // All lower case, without hyphen
		assertTrue("UUID in lower case without hyphen should be invalid in loose mode.", UuidUtil.isValid(ulid));

		ulid = "0123456789ab4DEFabcdEF0123456789"; // mixed case, without hyphen
		assertTrue("UUID in upper and lower case without hyphen should be invalid in loose mode.", UuidUtil.isValid(ulid));
		
		ulid = "{01234567-89ab-4def-abcd-ef0123456789}"; // All lower case, with curly braces
		assertTrue("UUID in lower case should be invalid in loose mode.", UuidUtil.isValid(ulid));
		
		ulid = "{01234567-89AB-4DEF-ABCD-EF0123456789}"; // All upper case, with curly braces
		assertTrue("UUID in upper case should valid in loose mode.", UuidUtil.isValid(ulid));
		
	}
	
	@Test
	public void testIsValidStrict() {
		boolean strict = true;

		String ulid = null; // Null
		assertFalse("Null UUID should be invalid in strict mode.", UuidUtil.isValid(ulid, strict));

		ulid = ""; // length: 0
		assertFalse("UUID with empty string should be invalid  in strict mode.", UuidUtil.isValid(ulid, strict));

		ulid = "01234567-89ab-4def-abcd-ef0123456789"; // All lower case
		assertTrue("UUID in lower case should be invalid in strict mode.", UuidUtil.isValid(ulid, strict));
		
		ulid = "01234567-89AB-4DEF-ABCD-EF0123456789"; // All upper case
		assertTrue("UUID in upper case should valid in strict mode.", UuidUtil.isValid(ulid, strict));

		ulid = "01234567-89ab-4DEF-abcd-EF0123456789"; // Mixed case
		assertTrue("UUID in upper and lower case should valid in strict mode.", UuidUtil.isValid(ulid, strict));

		ulid = "0123456789AB4DEFABCDEF0123456789"; // All upper case, without hyphen
		assertFalse("UUID in upper case without hyphen should be invalid in strict mode.", UuidUtil.isValid(ulid, strict));

		ulid = "0123456789ab4defabcdef0123456789"; // All lower case, without hyphen
		assertFalse("UUID in lower case without hyphen should be invalid in strict mode.", UuidUtil.isValid(ulid, strict));

		ulid = "0123456789ab4DEFabcdEF0123456789"; // mixed case, without hyphen
		assertFalse("UUID in upper and lower case without hyphen should be invalid in strict mode.", UuidUtil.isValid(ulid, strict));
		
	}
}
