package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.util.internal.ByteUtil;

public class UuidBuilderTest {

	private static final int DEFAULT_LOOP_MAX = 1_000;
	private static final Random seeder = new Random(1L);

	@Test
	public void testVersionNumber() {

		assertEquals(0x0L, new UuidBuilder().put((long) 0L).put((long) 0L).build().version());
		assertEquals(0xfL, new UuidBuilder().put((long) -1L).put((long) -1L).build().version());

		for (int i = 0; i < 15; i++) {
			Random random = new Random(seeder.nextLong());
			assertEquals(i, new UuidBuilder(i).put(random.nextLong()).put(random.nextLong()).build().version());
		}
	}

	@Test
	public void testPutTwoLongsWithoutVersionNumber() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Random random = new Random(seeder.nextLong());
			long msb = random.nextLong();
			long lsb = random.nextLong();

			UUID expected = new UUID(msb, lsb);
			UUID actual = new UuidBuilder().put(msb).put(lsb).build();

			assertEquals(expected, actual);
		}
	}

	@Test
	public void testPutTwoLongsWithVersion4() {

		int version = 4;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Random random = new Random(seeder.nextLong());

			long msb = random.nextLong();
			long lsb = random.nextLong();

			UUID expected = new UUID(version(msb, version), variant(lsb));
			UUID actual = new UuidBuilder(version).put(msb).put(lsb).build();

			assertEquals(expected, actual);
		}
	}

	@Test
	public void testPutFourIntsWithVersion4() {

		int version = 4;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Random random = new Random(seeder.nextLong());

			int part1 = random.nextInt();
			int part2 = random.nextInt();
			int part3 = random.nextInt();
			int part4 = random.nextInt();

			long msb = (((long) part1) << 32) | (long) (part2 & 0xffffffffL);
			long lsb = (((long) part3) << 32) | (long) (part4 & 0xffffffffL);

			UUID expected = new UUID(version(msb, version), variant(lsb));
			UUID actual = new UuidBuilder(version).put(part1).put(part2).put(part3).put(part4).build();

			assertEquals(expected, actual);
		}
	}

	@Test
	public void testPutSixteenBytesWithVersion4() {

		int version = 4;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Random random = new Random(seeder.nextLong());

			byte[] bytes1 = new byte[8];
			random.nextBytes(bytes1);

			byte[] bytes2 = new byte[8];
			random.nextBytes(bytes2);

			long msb = ByteUtil.toNumber(bytes1);
			long lsb = ByteUtil.toNumber(bytes2);
			UUID expected = new UUID(version(msb, version), variant(lsb));
			UUID actual = new UuidBuilder(version).put(bytes1).put(bytes2).build();

			assertEquals(expected, actual);
		}
	}

	@Test
	public void testPutTwoIntsFourShortsWithVersion8() {

		int version = 8;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Random random = new Random(seeder.nextLong());

			int p1 = random.nextInt();
			short p2 = (short) random.nextInt();
			short p3 = (short) random.nextInt();
			int p4 = random.nextInt();
			short p5 = (short) random.nextInt();
			short p6 = (short) random.nextInt();

			long msb = ((long) p1) << 32 | ((long) p2 & 0xffffL) << 16 | (long) (p3 & 0xffffL);
			long lsb = ((long) p4) << 32 | ((long) p5 & 0xffffL) << 16 | (long) (p6 & 0xffffL);

			UUID expected = new UUID(version(msb, version), variant(lsb));
			UUID actual = new UuidBuilder(version).put(p1).put(p2).put(p3).put(p4).put(p5).put(p6).build();

			assertEquals(expected, actual);
		}
	}

	@Test
	public void testIllegalArgumentException() {

		try {
			new UuidBuilder(-1);
			fail("Should throw illegal argument exception");
		} catch (IllegalArgumentException e) {
			// success
		}

		try {
			new UuidBuilder(16);
			fail("Should throw illegal argument exception");
		} catch (IllegalArgumentException e) {
			// success
		}
	}

	@Test
	public void testBufferUnderflowException() {

		try {
			new UuidBuilder().build();
			fail("Should throw buffer underflow exception");
		} catch (BufferUnderflowException e) {
			// success
		}

		try {
			new UuidBuilder().put((long) 0).put((int) 0).build();
			fail("Should throw buffer underflow exception");
		} catch (BufferUnderflowException e) {
			// success
		}

		try {
			new UuidBuilder().put((long) 0).put((int) 0).put((short) 0L).build();
			fail("Should throw buffer underflow exception");
		} catch (BufferUnderflowException e) {
			// success
		}
	}

	@Test
	public void testBufferOverflowException() {

		try {
			new UuidBuilder().put((byte) 0).put((long) 0).put((long) 0).build();
			fail("Should throw buffer overflow exception");
		} catch (BufferOverflowException e) {
			// success
		}

		try {
			new UuidBuilder().put((byte) 0).put((long) 0).put((int) 0).put((short) 0L).put((short) 0L).build();
			fail("Should throw buffer overflow exception");
		} catch (BufferOverflowException e) {
			// success
		}
	}

	private static long version(long msb, int version) {
		return (msb & 0xffff_ffff_ffff_0fffL) | (version & 0xf) << 12;
	}

	private static long variant(long lsb) {
		return (lsb & 0x3fff_ffff_ffff_ffffL) | 0x8000_0000_0000_0000L;
	}
}