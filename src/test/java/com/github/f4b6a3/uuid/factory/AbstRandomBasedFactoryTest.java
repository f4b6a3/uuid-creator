package com.github.f4b6a3.uuid.factory;

import com.github.f4b6a3.uuid.factory.nonstandard.PrefixCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.ShortPrefixCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.ShortSuffixCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.SuffixCombFactory;
import com.github.f4b6a3.uuid.factory.rfc4122.RandomBasedFactory;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class AbstRandomBasedFactoryTest extends UuidFactoryTest {

	@Test
	public void testByteRandomNextLong() {
		SplittableRandom seeder = new SplittableRandom(1);
		for (int i = 0; i < 10; i++) {
			byte[] bytes = new byte[Long.BYTES];
			(new Random(seeder.nextLong())).nextBytes(bytes);
			long number = ByteBuffer.wrap(bytes).getLong();
			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.SafeRandom((x) -> bytes);
			assertEquals(number, random.nextLong());
		}

		for (int i = 0; i < 10; i++) {

			int longs = 10;
			int size = Long.BYTES * longs;

			byte[] bytes = new byte[size];
			(new Random(seeder.nextLong())).nextBytes(bytes);
			ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
			ByteBuffer buffer2 = ByteBuffer.wrap(bytes);

			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.SafeRandom((x) -> {
				byte[] octects = new byte[x];
				buffer1.get(octects);
				return octects;
			});

			for (int j = 0; j < longs; j++) {
				assertEquals(buffer2.getLong(), random.nextLong());
			}
		}
	}

	@Test
	public void testByteRandomNextBytes() {
		SplittableRandom seeder = new SplittableRandom(1);
		for (int i = 0; i < 10; i++) {
			byte[] bytes = new byte[Long.BYTES];
			(new Random(seeder.nextLong())).nextBytes(bytes);
			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.SafeRandom((x) -> bytes);
			assertEquals(Arrays.toString(bytes), Arrays.toString(random.nextBytes(Long.BYTES)));
		}

		for (int i = 0; i < 10; i++) {

			int ints = 10;
			int size = Long.BYTES * ints;

			byte[] bytes = new byte[size];
			(new Random(seeder.nextLong())).nextBytes(bytes);
			ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
			ByteBuffer buffer2 = ByteBuffer.wrap(bytes);

			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.SafeRandom((x) -> {
				byte[] octects = new byte[x];
				buffer1.get(octects);
				return octects;
			});

			for (int j = 0; j < ints; j++) {
				byte[] octects = new byte[Long.BYTES];
				buffer2.get(octects);
				assertEquals(Arrays.toString(octects), Arrays.toString(random.nextBytes(Long.BYTES)));
			}
		}
	}

	@Test
	public void testLogRandomNextLong() {
		SplittableRandom seeder = new SplittableRandom(1);
		for (int i = 0; i < 10; i++) {
			byte[] bytes = new byte[Long.BYTES];
			(new Random(seeder.nextLong())).nextBytes(bytes);
			long number = ByteBuffer.wrap(bytes).getLong();
			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.FastRandom(() -> number);
			assertEquals(number, random.nextLong());
		}

		for (int i = 0; i < 10; i++) {

			int ints = 10;
			int size = Long.BYTES * ints;

			byte[] bytes = new byte[size];
			(new Random(seeder.nextLong())).nextBytes(bytes);
			ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
			ByteBuffer buffer2 = ByteBuffer.wrap(bytes);

			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.FastRandom(() -> buffer1.getLong());

			for (int j = 0; j < ints; j++) {
				assertEquals(buffer2.getLong(), random.nextLong());
			}
		}

	}

	@Test
	public void testLogRandomNextBytes() {
		SplittableRandom seeder = new SplittableRandom(1);
		for (int i = 0; i < 10; i++) {
			byte[] bytes = new byte[Long.BYTES];
			(new Random(seeder.nextLong())).nextBytes(bytes);
			long number = ByteBuffer.wrap(bytes).getLong();
			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.FastRandom(() -> number);
			assertEquals(Arrays.toString(bytes), Arrays.toString(random.nextBytes(Long.BYTES)));
		}

		for (int i = 0; i < 10; i++) {

			int ints = 10;
			int size = Long.BYTES * ints;

			byte[] bytes = new byte[size];
			(new Random(seeder.nextLong())).nextBytes(bytes);
			ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
			ByteBuffer buffer2 = ByteBuffer.wrap(bytes);

			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.FastRandom(() -> buffer1.getLong());

			for (int j = 0; j < ints; j++) {
				byte[] octects = new byte[Long.BYTES];
				buffer2.get(octects);
				assertEquals(Arrays.toString(octects), Arrays.toString(random.nextBytes(Long.BYTES)));
			}
		}
	}

	@Test
	public void testLongRandom() {

		{
			long nextLong = 0x1122334455667788L;
			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.FastRandom(() -> nextLong);
			byte[] bytes = random.nextBytes(Long.BYTES);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			assertEquals(nextLong, buffer.getLong());
		}

		{
			long nextLong = new SplittableRandom(1).nextLong();
			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.FastRandom(() -> nextLong);
			byte[] bytes = random.nextBytes(Long.BYTES);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			assertEquals(nextLong, buffer.getLong());
		}

		{
			long[] nextLong = { //
					0x0123456789abcdefL, //
					0xfedcba9876543210L, //
					0x1122334455667788L, //
					0x8877665544332211L ///
			};

			int octects = nextLong.length * Byte.SIZE;
			ByteBuffer buffer1 = ByteBuffer.allocate(octects);
			ByteBuffer buffer2 = ByteBuffer.allocate(octects);

			AtomicInteger x = new AtomicInteger();
			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.FastRandom(
					() -> nextLong[x.getAndIncrement()]);

			for (int i = 0; i < nextLong.length; i++) {
				buffer1.putLong(nextLong[i]);
				buffer2.put(random.nextBytes(8));
			}

			// assertEquals(Arrays.toString(buffer1.array()),
			// Arrays.toString(buffer2.array()));
			assertEquals(new BigInteger(1, buffer1.array()).toString(16),
					new BigInteger(1, buffer2.array()).toString(16));
		}

		{
			SplittableRandom entropy = new SplittableRandom(1);
			long[] nextLong = { //
					entropy.nextLong(), //
					entropy.nextLong(), //
					entropy.nextLong(), //
					entropy.nextLong() ///
			};

			int octects = nextLong.length * Byte.SIZE;
			ByteBuffer buffer1 = ByteBuffer.allocate(octects);
			ByteBuffer buffer2 = ByteBuffer.allocate(octects);

			AtomicInteger x = new AtomicInteger();
			AbstRandomBasedFactory.IRandom random = new AbstRandomBasedFactory.FastRandom(
					() -> nextLong[x.getAndIncrement()]);

			for (int i = 0; i < nextLong.length; i++) {
				buffer1.putLong(nextLong[i]);
				buffer2.put(random.nextBytes(8));
			}

			assertEquals(Arrays.toString(buffer1.array()), Arrays.toString(buffer2.array()));
		}
	}

	@Test
	public void testLongRandomWithFactory() {

		{
			List<Long> list = new ArrayList<>();
			list.add(0x0123456789abcdefL);
			list.add(0xfedcba9876543210L);
			UUID uuid1 = UuidUtil.setVersion(new UUID(list.get(0), list.get(1)), 4);
			RandomBasedFactory factory = new RandomBasedFactory(() -> list.remove(0));
			UUID uuid = factory.create();
			assertEquals(uuid1, uuid);
		}

		{
			// RandomBasedFactory
			SplittableRandom random = new SplittableRandom(1);
			List<Long> list = new ArrayList<>();
			list.add(random.nextLong());
			list.add(random.nextLong());
			UUID uuid1 = UuidUtil.setVersion(new UUID(list.get(0), list.get(1)), 4);
			RandomBasedFactory factory = new RandomBasedFactory(() -> list.remove(0));
			UUID uuid2 = factory.create();
			assertEquals(uuid1, uuid2);
		}

		{

			List<Long> list = new ArrayList<>();
			list.add(0x0123456789abcdefL);
			list.add(0xfedcba9876543210L);
			UUID uuid1 = new UUID(0x0000000000004defL, 0xbedcba9876543210L);
			PrefixCombFactory factory = new PrefixCombFactory(() -> list.remove(0));
			UUID uuid2 = factory.create();
			String substring1 = uuid1.toString().substring(14);
			String substring2 = uuid2.toString().substring(14);
			assertEquals(substring1, substring2);
		}

		{
			// SuffixCombFactory
			List<Long> list = new ArrayList<>();
			list.add(0x0123456789abcdefL);
			list.add(0xfedcba9876543210L);
			UUID uuid1 = new UUID(0x0123456789ab4defL, 0xb210000000000000L);
			SuffixCombFactory factory = new SuffixCombFactory(() -> list.remove(0));
			UUID uuid2 = factory.create();
			String substring1 = uuid1.toString().substring(0, 24);
			String substring2 = uuid2.toString().substring(0, 24);
			assertEquals(substring1, substring2);
		}

		{
			// ShortPrefixCombFactory
			List<Long> list = new ArrayList<>();
			list.add(0x0123456789abcdefL);
			list.add(0xfedcba9876543210L);
			UUID uuid1 = new UUID(0x0000456789ab4defL, 0xbedcba9876543210L);
			ShortPrefixCombFactory factory = new ShortPrefixCombFactory(() -> list.remove(0));
			UUID uuid2 = factory.create();
			String substring1 = uuid1.toString().substring(14);
			String substring2 = uuid2.toString().substring(14);
			assertEquals(substring1, substring2);
		}

		{
			// ShortSuffixCombFactory
			List<Long> list = new ArrayList<>();
			list.add(0x0123456789abcdefL);
			list.add(0xfedcba9876543210L);
			UUID uuid1 = new UUID(0x0123456789ab4defL, 0xba98000076543210L);
			ShortSuffixCombFactory factory = new ShortSuffixCombFactory(() -> list.remove(0));
			UUID uuid2 = factory.create();
			String substring1 = uuid1.toString().substring(0, 24) + uuid1.toString().substring(28, 36);
			String substring2 = uuid2.toString().substring(0, 24) + uuid2.toString().substring(28, 36);
			assertEquals(substring1, substring2);
		}
	}

}
