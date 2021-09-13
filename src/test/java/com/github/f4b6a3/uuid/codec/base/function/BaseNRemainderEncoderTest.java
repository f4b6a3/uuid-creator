package com.github.f4b6a3.uuid.codec.base.function;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import com.github.f4b6a3.uuid.codec.base.BaseN;

import static com.github.f4b6a3.uuid.codec.base.function.BaseNRemainderDecoderTest.toInts;
import static com.github.f4b6a3.uuid.codec.base.function.BaseNRemainderDecoderTest.fromInts;

public class BaseNRemainderEncoderTest {

	private static final int UUID_BYTES = 16;

	@Test
	public void testEncode() {
		Random random = new Random();
		for (int i = 0; i < 1000; i++) {
			byte[] bytes = new byte[UUID_BYTES];
			random.nextBytes(bytes);
			UUID uuid = BinaryCodec.INSTANCE.decode(bytes);
			String string = Base62Codec.INSTANCE.encode(uuid);
			assertEquals(encode(Base62Codec.INSTANCE.getBase(), bytes), string);
		}
	}

	@Test
	public void testReminder() {
		Random random = new Random();

		for (int i = 0; i < 1000; i++) {
			byte[] bytes = new byte[UUID_BYTES];
			random.nextBytes(bytes);
			int divisor = random.nextInt() & 0x7fffffff; // positive divisor

			BigInteger number1 = new BigInteger(1, bytes);
			BigInteger quotient1 = number1.divide(BigInteger.valueOf(divisor));
			BigInteger reminder1 = number1.remainder(BigInteger.valueOf(divisor));

			int[] number2 = toInts(bytes);
			int[] quotient2 = new int[UUID_BYTES / Integer.BYTES];
			int remainder2 = BaseNRemainderEncoder.remainder(number2, divisor, quotient2);

			assertEquals(number1, new BigInteger(1, fromInts(number2)));
			assertEquals(quotient1, new BigInteger(1, fromInts(quotient2)));
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
