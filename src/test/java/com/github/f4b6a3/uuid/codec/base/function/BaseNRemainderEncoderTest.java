package com.github.f4b6a3.uuid.codec.base.function;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.UUID;
import java.util.Random;
import java.util.SplittableRandom;

import org.junit.Test;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import com.github.f4b6a3.uuid.codec.base.BaseN;
import com.github.f4b6a3.uuid.codec.base.BaseNCodec.CustomDivider;

import static com.github.f4b6a3.uuid.codec.base.function.BaseNRemainderDecoderTest.toLongs;
import static com.github.f4b6a3.uuid.codec.base.function.BaseNRemainderDecoderTest.fromLongs;

public class BaseNRemainderEncoderTest {

	private static final int UUID_BYTES = 16;

	@Test
	public void testEncode() {
		SplittableRandom seeder = new SplittableRandom(1);
		for (int i = 0; i < 1000; i++) {
			byte[] bytes = new byte[UUID_BYTES];
			(new Random(seeder.nextLong())).nextBytes(bytes);
			UUID uuid = BinaryCodec.INSTANCE.decode(bytes);
			String string = Base62Codec.INSTANCE.encode(uuid);
			assertEquals(encode(Base62Codec.INSTANCE.getBase(), bytes), string);
		}
	}

	@Test
	public void testDivide() {
		SplittableRandom seeder = new SplittableRandom(1);
		for (int i = 0; i < 1000; i++) {
			byte[] bytes = new byte[UUID_BYTES];
			(new Random(seeder.nextLong())).nextBytes(bytes);
			int divisor = seeder.nextInt() & 0x7fffffff; // positive divisor

			CustomDivider divider = x -> new long[] { x / divisor, x % divisor };

			BigInteger number1 = new BigInteger(1, bytes);
			BigInteger quotient1 = number1.divide(BigInteger.valueOf(divisor));
			BigInteger reminder1 = number1.remainder(BigInteger.valueOf(divisor));

			long[] number2 = toLongs(bytes);
			long[] quotient2 = new long[] { 0, 0 };
			long remainder2 = 0;
			long[] answer0 = BaseNRemainderEncoder.divide(number2[0], divider, 0); // divide MSB
			quotient2[0] = answer0[0];
			remainder2 = answer0[1];
			long[] answer1 = BaseNRemainderEncoder.divide(number2[1], divider, remainder2); // divide LSB
			quotient2[1] = answer1[0];
			remainder2 = answer1[1];

			assertEquals(number1, new BigInteger(1, fromLongs(number2)));
			assertEquals(quotient1, new BigInteger(1, fromLongs(quotient2)));
			assertEquals(reminder1.intValue(), remainder2);
		}
	}

	private String encode(BaseN base, byte[] bytes) {

		// it must be a POSITIVE big number
		BigInteger number = new BigInteger(1, bytes);

		char[] buffer = new char[base.getLength()];
		int b = buffer.length; // buffer index

		BigInteger n = BigInteger.valueOf(base.getRadix());

		// fill in the buffer backwards using remainder operation
		while (number.compareTo(BigInteger.ZERO) > 0) {
			buffer[--b] = base.getAlphabet().get(number.remainder(n).intValue());
			number = number.divide(n);
		}

		// add padding left
		while (b > 0) {
			buffer[--b] = base.getPadding();
		}

		return new String(buffer);
	}
}
