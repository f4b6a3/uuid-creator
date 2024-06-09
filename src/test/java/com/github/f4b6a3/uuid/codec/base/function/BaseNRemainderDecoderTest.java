package com.github.f4b6a3.uuid.codec.base.function;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import com.github.f4b6a3.uuid.codec.base.BaseN;

public class BaseNRemainderDecoderTest {

	private static final int UUID_BYTES = 16;

	@Test
	public void testDecode() {
		for (int i = 0; i < 1000; i++) {
			String string = getRandomString(Base62Codec.INSTANCE.getBase());
			UUID uuid = Base62Codec.INSTANCE.decode(string);
			byte[] bytes = BinaryCodec.INSTANCE.encode(uuid);
			assertEquals(Arrays.toString(decode(Base62Codec.INSTANCE.getBase(), string)), Arrays.toString(bytes));
		}
	}

	@Test
	public void testMultiply() {

		SplittableRandom seeder = new SplittableRandom(1);
		for (int i = 0; i < 1000; i++) {
			byte[] bytes = new byte[UUID_BYTES];
			(new Random(seeder.nextLong())).nextBytes(bytes);
			long multiplier = seeder.nextInt() & 0x7fffffff; // positive
			long addend = seeder.nextInt() & 0x7fffffff; // positive

			BigInteger number1 = new BigInteger(1, bytes);
			BigInteger product1 = number1.multiply(BigInteger.valueOf(multiplier)).add(BigInteger.valueOf(addend));

			// truncate BigInteger product!
			byte[] temp0 = product1.toByteArray();
			byte[] temp1 = new byte[UUID_BYTES];
			int t0 = temp0.length;
			int t1 = temp1.length;
			while (t0 > 0 && t1 > 0) {
				temp1[--t1] = temp0[--t0];
			}
			byte[] productBytes1 = temp1;

			long[] number2 = toLongs(bytes);
			long[] product2 = new long[] { 0, 0 };
			long overflow2 = 0;
			long[] answer0 = BaseNRemainderDecoder.multiply(number2[1], multiplier, addend); // multiply LSB
			product2[1] = answer0[0];
			overflow2 = answer0[1];
			long[] answer1 = BaseNRemainderDecoder.multiply(number2[0], multiplier, overflow2); // multiply MSB
			product2[0] = answer1[0];
			overflow2 = answer1[1];
			byte[] productBytes2 = fromLongs(product2);

			assertEquals(number1, new BigInteger(1, fromLongs(number2)));
			assertEquals(Arrays.toString(productBytes1), Arrays.toString(productBytes2));
		}
	}

	protected static long[] toLongs(byte[] bytes) {
		UUID uuid = BinaryCodec.INSTANCE.decode(bytes);
		return new long[] { uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() };
	}

	protected static byte[] fromLongs(long[] longs) {
		UUID uuid = new UUID(longs[0], longs[1]);
		return BinaryCodec.INSTANCE.encode(uuid);
	}

	private String getRandomString(BaseN base) {

		SplittableRandom random = new SplittableRandom(1);
		char[] chars = new char[base.getLength()];

		chars[0] = base.getPadding(); // to avoid overflow
		for (int i = 1; i < chars.length; i++) {
			chars[i] = base.getAlphabet().get(random.nextInt(base.getRadix()));
		}

		return new String(chars);
	}

	private byte[] decode(BaseN base, String string) {

		char[] chars = string.toCharArray();
		BigInteger number = BigInteger.ZERO;

		BigInteger n = BigInteger.valueOf(base.getRadix());

		for (int c : chars) {
			final long value = base.getMap().get(c);
			number = n.multiply(number).add(BigInteger.valueOf(value));
		}

		// prepare a byte buffer
		byte[] result = number.toByteArray();
		byte[] buffer = new byte[UUID_BYTES];
		int r = result.length; // result index
		int b = buffer.length; // buffer index

		// fill in the byte buffer
		while (b > 0 && r > 0) {
			buffer[--b] = result[--r];
		}

		return buffer;
	}
}
