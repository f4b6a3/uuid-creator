package com.github.f4b6a3.uuid.alt;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.codec.other.TimeOrderedCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidTime;
import com.github.f4b6a3.uuid.util.UuidUtil;
import com.github.f4b6a3.uuid.util.UuidValidator;

public class GUIDTest {

	protected static final int DEFAULT_LOOP_MAX = 100;

	@Test
	public void testConstructorGUID() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			GUID guid = new GUID(new GUID(msb, lsb));
			assertEquals(msb, guid.getMostSignificantBits());
			assertEquals(lsb, guid.getLeastSignificantBits());
		}
	}

	@Test
	public void testConstructorLongs() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			GUID guid = new GUID(msb, lsb);
			assertEquals(msb, guid.getMostSignificantBits());
			assertEquals(lsb, guid.getLeastSignificantBits());
		}
	}

	@Test
	public void testConstructorJdkUUID() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			GUID guid = new GUID(new UUID(msb, lsb));
			assertEquals(msb, guid.getMostSignificantBits());
			assertEquals(lsb, guid.getLeastSignificantBits());
		}
	}

	@Test
	public void testConstructorString() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			UUID uuid = new UUID(msb, lsb);
			String str = uuid.toString();
			GUID guid = new GUID(str);
			assertEquals(msb, guid.getMostSignificantBits());
			assertEquals(lsb, guid.getLeastSignificantBits());
		}
	}

	@Test
	public void testConstructorBytes() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			byte[] bytes = new byte[GUID.GUID_BYTES];
			random.nextBytes(bytes);
			GUID guid = new GUID(bytes);
			assertEquals(Arrays.toString(bytes), Arrays.toString(guid.toBytes()));
		}

		try {
			byte[] bytes = null;
			new GUID(bytes);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}

		try {
			byte[] bytes = new byte[GUID.GUID_BYTES + 1];
			new GUID(bytes);
			fail("Should throw an exception");
		} catch (IllegalArgumentException e) {
			// success
		}
	}

	@Test
	public void testV1() {
		GUID prev = GUID.v1();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long t0 = System.currentTimeMillis();
			GUID uuid = GUID.v1();
			long t1 = UuidTime.toUnixTimestamp(uuid.get().timestamp()) / 10_000L;
			long t2 = System.currentTimeMillis();
			assertNotNull(uuid);
			assertNotEquals(prev, uuid);
			assertNotEquals(GUID.NIL, uuid);
			assertNotEquals(GUID.MAX, uuid);
			assertEquals(1, uuid.version());
			assertTrue(t0 <= t1 && t1 <= t2);
			prev = uuid;
		}
	}

	@Test
	public void testV2() {
		GUID prev = GUID.v2((byte) 0, (int) 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			byte ld = (byte) i;
			int li = (int) i * 31;
			GUID uuid = GUID.v2(ld, li);
			byte localDomain = UuidUtil.getLocalDomain(uuid.get());
			int localIdentifier = UuidUtil.getLocalIdentifier(uuid.get());
			assertNotNull(uuid);
			assertNotEquals(prev, uuid);
			assertNotEquals(GUID.NIL, uuid);
			assertNotEquals(GUID.MAX, uuid);
			assertEquals(2, uuid.version());
			assertEquals(ld, localDomain);
			assertEquals(li, localIdentifier);
			prev = uuid;
		}
	}

	@Test
	public void testV3() {
		GUID prev = GUID.v3(GUID.NIL, "");
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			GUID namespace = new GUID(UUID.randomUUID());
			String name = UUID.randomUUID().toString();
			GUID uuid = GUID.v3(namespace, name);
			assertNotNull(uuid);
			assertNotEquals(prev, uuid);
			assertEquals(3, uuid.version());
			assertNotEquals(GUID.NIL, uuid);
			assertNotEquals(GUID.MAX, uuid);
			assertNotEquals(GUID.v3(null, name), GUID.v3(namespace, name));
			assertNotEquals(GUID.v3(GUID.NIL, name), GUID.v3(namespace, name));
			assertNotEquals(GUID.v3(namespace, name), GUID.v5(namespace, name)); // v5
			assertNotEquals(GUID.v3(namespace, name), GUID.v8(namespace, name)); // v8
			assertEquals(GUID.v3(null, name), GUID.v3(null, name));
			assertEquals(GUID.v3(GUID.NIL, name), GUID.v3(GUID.NIL, name));
			assertEquals(GUID.v3(namespace, name), GUID.v3(namespace, name));
			assertEquals(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), GUID.v3(null, name).get());
			prev = uuid;
		}
		{
			// Example of a UUIDv3 Value
			// draft-ietf-uuidrev-rfc4122bis-03
			GUID guid = GUID.v3(GUID.NAMESPACE_DNS, "www.example.com");
			assertEquals("5df41881-3aed-3515-88a7-2f4a814cf09e", guid.toString());
		}
	}

	@Test
	public void testV4() {
		GUID prev = GUID.v4();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			GUID uuid = GUID.v4();
			assertNotNull(uuid);
			assertNotEquals(prev, uuid);
			assertNotEquals(GUID.NIL, uuid);
			assertNotEquals(GUID.MAX, uuid);
			assertEquals(4, uuid.version());
			prev = uuid;
		}
	}

	@Test
	public void testV5() {
		GUID prev = GUID.v5(GUID.NIL, "");
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			GUID namespace = new GUID(UUID.randomUUID());
			String name = UUID.randomUUID().toString();
			GUID uuid = GUID.v5(namespace, name);
			assertNotNull(uuid);
			assertNotEquals(prev, uuid);
			assertEquals(5, uuid.version());
			assertNotEquals(GUID.NIL, uuid);
			assertNotEquals(GUID.MAX, uuid);
			assertNotEquals(GUID.v5(null, name), GUID.v5(namespace, name));
			assertNotEquals(GUID.v5(GUID.NIL, name), GUID.v5(namespace, name));
			assertNotEquals(GUID.v5(namespace, name), GUID.v3(namespace, name)); // v3
			assertNotEquals(GUID.v5(namespace, name), GUID.v8(namespace, name)); // v8
			assertEquals(GUID.v5(null, name), GUID.v5(null, name));
			assertEquals(GUID.v5(GUID.NIL, name), GUID.v5(GUID.NIL, name));
			assertEquals(GUID.v5(namespace, name), GUID.v5(namespace, name));
			prev = uuid;
		}
		{
			// Example of a UUIDv5 Value
			// draft-ietf-uuidrev-rfc4122bis-03
			GUID guid = GUID.v5(GUID.NAMESPACE_DNS, "www.example.com");
			assertEquals("2ed6657d-e927-568b-95e1-2665a8aea6a2", guid.toString());
		}
	}

	@Test
	public void testV6() {
		GUID prev = GUID.v6();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long t0 = System.currentTimeMillis();
			GUID uuid = GUID.v6();
			GUID temp = new GUID(TimeOrderedCodec.INSTANCE.decode(uuid.get()));
			long t1 = UuidTime.toUnixTimestamp(temp.get().timestamp()) / 10_000L;
			long t2 = System.currentTimeMillis();
			assertNotNull(uuid);
			assertNotEquals(prev, uuid);
			assertNotEquals(GUID.NIL, uuid);
			assertNotEquals(GUID.MAX, uuid);
			assertEquals(6, uuid.version());
			assertTrue(t0 <= t1 && t1 <= t2);
			prev = uuid;
		}
	}

	@Test
	public void testV7() {
		GUID prev = GUID.v7();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long t0 = System.currentTimeMillis();
			GUID uuid = GUID.v7();
			long t1 = uuid.getMostSignificantBits() >>> 16;
			long t2 = System.currentTimeMillis();
			assertNotNull(uuid);
			assertNotEquals(prev, uuid);
			assertNotEquals(GUID.NIL, uuid);
			assertNotEquals(GUID.MAX, uuid);
			assertEquals(7, uuid.version());
			assertTrue(t0 <= t1 && t1 <= t2);
			prev = uuid;
		}
	}

	@Test
	public void testV8() {
		GUID prev = GUID.v8(GUID.NIL, "");
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			GUID namespace = new GUID(UUID.randomUUID());
			String name = UUID.randomUUID().toString();
			GUID uuid = GUID.v8(namespace, name);
			assertNotNull(uuid);
			assertNotEquals(prev, uuid);
			assertEquals(8, uuid.version());
			assertNotEquals(GUID.NIL, uuid);
			assertNotEquals(GUID.MAX, uuid);
			assertNotEquals(GUID.v8(null, name), GUID.v8(namespace, name));
			assertNotEquals(GUID.v8(GUID.NIL, name), GUID.v8(namespace, name));
			assertNotEquals(GUID.v8(namespace, name), GUID.v3(namespace, name)); // v3
			assertNotEquals(GUID.v8(namespace, name), GUID.v5(namespace, name)); // v5
			assertEquals(GUID.v8(null, name), GUID.v8(null, name));
			assertEquals(GUID.v8(GUID.NIL, name), GUID.v8(GUID.NIL, name));
			assertEquals(GUID.v8(namespace, name), GUID.v8(namespace, name));
			prev = uuid;
		}
		{
			// Example of a UUIDv8 Value
			// draft-ietf-uuidrev-rfc4122bis-03
			GUID guid = GUID.v8(GUID.NAMESPACE_DNS, "www.example.com");
			assertEquals("401835fd-a627-870a-873f-ed73f2bc5b2c", guid.toString());
		}
	}

	@Test
	public void testToUUID() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			UUID uuid = (new GUID(msb, lsb)).get();
			assertEquals(msb, uuid.getMostSignificantBits());
			assertEquals(lsb, uuid.getLeastSignificantBits());
		}
	}

	@Test
	public void testToString() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UUID.randomUUID();
			GUID guid = new GUID(uuid);
			assertEquals(uuid.toString(), guid.toString());
		}
	}

	@Test
	public void testToBytes() {
		Random random = new Random();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			byte[] bytes1 = new byte[16];
			random.nextBytes(bytes1);
			GUID guid0 = new GUID(bytes1);

			byte[] bytes2 = guid0.toBytes();
			for (int j = 0; j < bytes1.length; j++) {
				assertEquals(bytes1[j], bytes2[j]);
			}
		}
	}

	@Test
	public void testHashCode() {

		Random random = new Random();
		byte[] bytes = new byte[GUID.GUID_BYTES];

		// invoked on the same object
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			random.nextBytes(bytes);
			GUID guid1 = new GUID(bytes);
			assertEquals(guid1.hashCode(), guid1.hashCode());
		}

		// invoked on two equal objects
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			random.nextBytes(bytes);
			GUID guid1 = new GUID(bytes);
			GUID guid2 = new GUID(bytes);
			assertEquals(guid1.hashCode(), guid2.hashCode());
		}
	}

	@Test
	public void testEquals() {

		Random random = new Random();
		byte[] bytes = new byte[GUID.GUID_BYTES];

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			random.nextBytes(bytes);
			GUID guid1 = new GUID(bytes);
			GUID guid2 = new GUID(bytes);
			assertEquals(guid1, guid2);
			assertEquals(guid1.toString(), guid2.toString());
			assertEquals(Arrays.toString(guid1.toBytes()), Arrays.toString(guid2.toBytes()));

			// change all bytes
			for (int j = 0; j < bytes.length; j++) {
				bytes[j]++;
			}
			GUID guid3 = new GUID(bytes);
			assertNotEquals(guid1, guid3);
			assertNotEquals(guid1.toString(), guid3.toString());
			assertNotEquals(Arrays.toString(guid1.toBytes()), Arrays.toString(guid3.toBytes()));
		}
	}

	@Test
	public void testCompareTo() {

		final long zero = 0L;
		Random random = new Random();
		byte[] bytes = new byte[GUID.GUID_BYTES];

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(random.nextLong()).array();
			GUID guid1 = new GUID(bytes);
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(random.nextLong()).array();
			GUID guid2 = new GUID(bytes);
			GUID guid3 = new GUID(bytes);
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, guid1.compareTo(guid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, guid1.compareTo(guid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, guid2.compareTo(guid3) == 0);

			// compare lexicographically
			assertEquals(number1.compareTo(number2) > 0, guid1.toString().compareTo(guid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, guid1.toString().compareTo(guid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, guid2.toString().compareTo(guid3.toString()) == 0);
		}

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			bytes = ByteBuffer.allocate(16).putLong(zero).putLong(random.nextLong()).array();
			GUID guid1 = new GUID(bytes);
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(zero).putLong(random.nextLong()).array();
			GUID guid2 = new GUID(bytes);
			GUID guid3 = new GUID(bytes);
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, guid1.compareTo(guid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, guid1.compareTo(guid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, guid2.compareTo(guid3) == 0);

			// compare lexicographically
			assertEquals(number1.compareTo(number2) > 0, guid1.toString().compareTo(guid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, guid1.toString().compareTo(guid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, guid2.toString().compareTo(guid3.toString()) == 0);
		}

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(zero).array();
			GUID guid1 = new GUID(bytes);
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(zero).array();
			GUID guid2 = new GUID(bytes);
			GUID guid3 = new GUID(bytes);
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, guid1.compareTo(guid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, guid1.compareTo(guid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, guid2.compareTo(guid3) == 0);

			// compare lexicographically
			assertEquals(number1.compareTo(number2) > 0, guid1.toString().compareTo(guid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, guid1.toString().compareTo(guid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, guid2.toString().compareTo(guid3.toString()) == 0);
		}
	}

	@Test
	public void testValid() {

		String uuid = null; // Null
		assertFalse("Null UUID string should be invalid.", GUID.valid(uuid));

		uuid = ""; // length: 0
		assertFalse("UUID with empty string should be invalid.", GUID.valid(uuid));

		uuid = "01234567-89ab-4def-abcd-ef0123456789"; // String length = 36
		assertTrue("UUID with length equals to 36 should be valid.", GUID.valid(uuid));

		uuid = "01234567-89ab-4def-abcdef01-23456789"; // String length = 36 with hyphen in wrong position
		assertFalse("UUID with length equals to 36 with hyphen in wrong position should be invalid.", GUID.valid(uuid));

		uuid = "01234567-89ab-4def-abcd-ef01-3456789"; // String length = 36 with an extra hyphen
		assertFalse("UUID with length equals to 36 with an extra hyphen should be invalid.", GUID.valid(uuid));

		uuid = "01234567-89ab-4def-abcddef0123456789"; // String length = 36 with a missing hyphen
		assertFalse("UUID with length equals to 36 with a missing hyphen should be invalid.", GUID.valid(uuid));

		uuid = "0123456789ab4defabcdef0123456789"; // Missing hyphens
		assertFalse("UUID without hyphen should be invalid.", GUID.valid(uuid));

		uuid = "01234567-89ab-4def-abcd-ef0123456789"; // All lower case
		assertTrue("UUID in lower case should be valid.", GUID.valid(uuid));

		uuid = "01234567-89AB-4DEF-ABCD-EF0123456789"; // All upper case
		assertTrue("UUID in upper case should valid.", GUID.valid(uuid));

		uuid = "01234567-89ab-4DEF-abcd-EF0123456789"; // Mixed case
		assertTrue("UUID in upper and lower case should valid.", GUID.valid(uuid));

		uuid = "01234567-89ab-4def-abcd-SOPQRSTUVXYZ"; // String with non hexadecimal chars
		assertFalse("UUID string with non hexadecimal chars should be invalid.", GUID.valid(uuid));

		uuid = "01234567-89ab-4def-!@#$-ef0123456789"; // String with non alphanumeric chars
		assertFalse("UUID string non alphanumeric chars should be invalid.", GUID.valid(uuid));

		try {
			uuid = null;
			UuidValidator.validate(uuid);
			fail();
		} catch (InvalidUuidException e) {
			// Success
		}
	}
}
