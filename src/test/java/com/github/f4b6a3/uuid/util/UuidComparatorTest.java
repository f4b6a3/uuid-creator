package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.other.TimeOrderedCodec;

public class UuidComparatorTest {

	private static final int DEFAULT_LOOP_MAX = 100;

	@Test
	public void testCompareNulls() {

		UuidComparator comparator = new UuidComparator();

		UUID x = null;
		UUID y = null;
		assertEquals(0, comparator.compare(x, y));

		x = null;
		y = UUID.randomUUID();
		assertEquals(-1, comparator.compare(x, y));

		x = UUID.randomUUID();
		y = null;
		assertEquals(1, comparator.compare(x, y));
	}

	@Test
	public void testCompare() {

		UuidComparator comparator = new UuidComparator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID x = UUID.randomUUID();
			UUID y = x;
			// (x.equals(y)), then (compare(x, y)==0)
			assertEquals(0, comparator.compare(x, y));
		}

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID x = UUID.randomUUID();
			UUID y = x;
			UUID z = UUID.randomUUID();
			// compare(x, y) == 0, then sgn(compare(x, z)) == sgn(compare(y, z))
			assertEquals(Integer.signum(comparator.compare(x, z)), Integer.signum(comparator.compare(y, z)));
		}

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			UUID x = UUID.randomUUID();
			UUID y = UUID.randomUUID();
			UUID z = UUID.randomUUID();

			// sgn(compare(x, y)) = -sgn(compare(y, x))
			assertEquals(Integer.signum(comparator.compare(x, y)), -Integer.signum((comparator.compare(y, x))));

			// compare(x, y) > 0 && compare(y, z) > 0, then compare(x, z) > 0
			if (comparator.compare(x, y) > 0 && comparator.compare(y, z) > 0) {
				assertTrue(comparator.compare(x, z) > 0);
			}
		}
	}

	@Test
	public void testCompareDefault() {

		final long zero = 0L;
		byte[] bytes = new byte[16];
		Random random = new Random();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(random.nextLong()).array();
			bytes = setVersion(bytes, 1); // set version 1
			UUID uuid1 = BinaryCodec.INSTANCE.decode(rearrange(bytes));
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(random.nextLong()).array();
			bytes = setVersion(bytes, 1); // set version 1
			UUID uuid2 = BinaryCodec.INSTANCE.decode(rearrange(bytes));
			UUID uuid3 = BinaryCodec.INSTANCE.decode(rearrange(bytes));
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, UuidComparator.defaultCompare(uuid1, uuid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, UuidComparator.defaultCompare(uuid1, uuid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, UuidComparator.defaultCompare(uuid2, uuid3) == 0);

			// compare lexicographically
			uuid1 = UuidUtil.setVersion(TimeOrderedCodec.INSTANCE.encode(uuid1), 1);
			uuid2 = UuidUtil.setVersion(TimeOrderedCodec.INSTANCE.encode(uuid2), 1);
			uuid3 = UuidUtil.setVersion(TimeOrderedCodec.INSTANCE.encode(uuid3), 1);
			assertEquals(number1.compareTo(number2) > 0, uuid1.toString().compareTo(uuid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, uuid1.toString().compareTo(uuid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, uuid2.toString().compareTo(uuid3.toString()) == 0);
		}

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			bytes = ByteBuffer.allocate(16).putLong(zero).putLong(random.nextLong()).array();
			bytes = setVersion(bytes, 1); // set version 1
			UUID uuid1 = BinaryCodec.INSTANCE.decode(rearrange(bytes));
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(zero).putLong(random.nextLong()).array();
			bytes = setVersion(bytes, 1); // set version 1
			UUID uuid2 = BinaryCodec.INSTANCE.decode(rearrange(bytes));
			UUID uuid3 = BinaryCodec.INSTANCE.decode(rearrange(bytes));
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, UuidComparator.defaultCompare(uuid1, uuid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, UuidComparator.defaultCompare(uuid1, uuid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, UuidComparator.defaultCompare(uuid2, uuid3) == 0);

			// compare lexicographically
			uuid1 = UuidUtil.setVersion(TimeOrderedCodec.INSTANCE.encode(uuid1), 1);
			uuid2 = UuidUtil.setVersion(TimeOrderedCodec.INSTANCE.encode(uuid2), 1);
			uuid3 = UuidUtil.setVersion(TimeOrderedCodec.INSTANCE.encode(uuid3), 1);
			assertEquals(number1.compareTo(number2) > 0, uuid1.toString().compareTo(uuid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, uuid1.toString().compareTo(uuid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, uuid2.toString().compareTo(uuid3.toString()) == 0);
		}

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(zero).array();
			bytes = setVersion(bytes, 1); // set version 1
			UUID uuid1 = BinaryCodec.INSTANCE.decode(rearrange(bytes));
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(zero).array();
			bytes = setVersion(bytes, 1); // set version 1
			UUID uuid2 = BinaryCodec.INSTANCE.decode(rearrange(bytes));
			UUID uuid3 = BinaryCodec.INSTANCE.decode(rearrange(bytes));
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, UuidComparator.defaultCompare(uuid1, uuid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, UuidComparator.defaultCompare(uuid1, uuid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, UuidComparator.defaultCompare(uuid2, uuid3) == 0);

			// compare lexicographically
			uuid1 = UuidUtil.setVersion(TimeOrderedCodec.INSTANCE.encode(uuid1), 1);
			uuid2 = UuidUtil.setVersion(TimeOrderedCodec.INSTANCE.encode(uuid2), 1);
			uuid3 = UuidUtil.setVersion(TimeOrderedCodec.INSTANCE.encode(uuid3), 1);
			assertEquals(number1.compareTo(number2) > 0, uuid1.toString().compareTo(uuid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, uuid1.toString().compareTo(uuid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, uuid2.toString().compareTo(uuid3.toString()) == 0);
		}
	}

	@Test
	public void testCompareOpaque() {

		final long zero = 0L;
		byte[] bytes = new byte[16];
		Random random = new Random();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(random.nextLong()).array();
			UUID uuid1 = BinaryCodec.INSTANCE.decode(bytes);
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(random.nextLong()).array();
			UUID uuid2 = BinaryCodec.INSTANCE.decode(bytes);
			UUID uuid3 = BinaryCodec.INSTANCE.decode(bytes);
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, UuidComparator.opaqueCompare(uuid1, uuid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, UuidComparator.opaqueCompare(uuid1, uuid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, UuidComparator.opaqueCompare(uuid2, uuid3) == 0);

			// compare lexicographically
			assertEquals(number1.compareTo(number2) > 0, uuid1.toString().compareTo(uuid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, uuid1.toString().compareTo(uuid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, uuid2.toString().compareTo(uuid3.toString()) == 0);
		}

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			bytes = ByteBuffer.allocate(16).putLong(zero).putLong(random.nextLong()).array();
			UUID uuid1 = BinaryCodec.INSTANCE.decode(bytes);
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(zero).putLong(random.nextLong()).array();
			UUID uuid2 = BinaryCodec.INSTANCE.decode(bytes);
			UUID uuid3 = BinaryCodec.INSTANCE.decode(bytes);
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, UuidComparator.opaqueCompare(uuid1, uuid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, UuidComparator.opaqueCompare(uuid1, uuid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, UuidComparator.opaqueCompare(uuid2, uuid3) == 0);

			// compare lexicographically
			assertEquals(number1.compareTo(number2) > 0, uuid1.toString().compareTo(uuid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, uuid1.toString().compareTo(uuid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, uuid2.toString().compareTo(uuid3.toString()) == 0);
		}

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(zero).array();
			UUID uuid1 = BinaryCodec.INSTANCE.decode(bytes);
			BigInteger number1 = new BigInteger(1, bytes);

			bytes = ByteBuffer.allocate(16).putLong(random.nextLong()).putLong(zero).array();
			UUID uuid2 = BinaryCodec.INSTANCE.decode(bytes);
			UUID uuid3 = BinaryCodec.INSTANCE.decode(bytes);
			BigInteger number2 = new BigInteger(1, bytes);
			BigInteger number3 = new BigInteger(1, bytes);

			// compare numerically
			assertEquals(number1.compareTo(number2) > 0, UuidComparator.opaqueCompare(uuid1, uuid2) > 0);
			assertEquals(number1.compareTo(number2) < 0, UuidComparator.opaqueCompare(uuid1, uuid2) < 0);
			assertEquals(number2.compareTo(number3) == 0, UuidComparator.opaqueCompare(uuid2, uuid3) == 0);

			// compare lexicographically
			assertEquals(number1.compareTo(number2) > 0, uuid1.toString().compareTo(uuid2.toString()) > 0);
			assertEquals(number1.compareTo(number2) < 0, uuid1.toString().compareTo(uuid2.toString()) < 0);
			assertEquals(number2.compareTo(number3) == 0, uuid2.toString().compareTo(uuid3.toString()) == 0);
		}
	}

	private byte[] setVersion(byte[] bytes, int version) {
		UUID uuid = BinaryCodec.INSTANCE.decode(bytes);
		uuid = UuidUtil.setVersion(uuid, version);
		return BinaryCodec.INSTANCE.encode(uuid);
	}

	private byte[] rearrange(byte[] bytes) {
		UUID uuid = BinaryCodec.INSTANCE.decode(bytes);
		uuid = UuidUtil.setVersion(uuid, 6);
		uuid = TimeOrderedCodec.INSTANCE.decode(uuid);
		uuid = UuidUtil.setVersion(uuid, 1);
		return BinaryCodec.INSTANCE.encode(uuid);
	}
}
