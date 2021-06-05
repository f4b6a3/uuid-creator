package com.github.f4b6a3.uuid.codec.base.function;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import com.github.f4b6a3.uuid.codec.base.BaseN;

public class BaseNRemainderDecoderTest {

	private static final int UUID_INTS = 4;
	private static final int UUID_BYTES = 16;

	@Test
	public void testDecode() {
		for (int i = 0; i < 1000; i++) {
			String string = getRandomString(BaseN.BASE_62);
			UUID uuid = Base62Codec.INSTANCE.decode(string);
			byte[] bytes = BinaryCodec.INSTANCE.encode(uuid);
			assertEquals(Arrays.toString(decode(BaseN.BASE_62, string)), Arrays.toString(bytes));
		}
	}

	@Test
	public void testMultiply() {
		Random random = new Random();

		for (int i = 0; i < 1000; i++) {
			byte[] bytes = new byte[UUID_BYTES];
			random.nextBytes(bytes);
			int multiplier = random.nextInt() & 0x7fffffff; // positive
			int addend = random.nextInt() & 0x7fffffff; // positive

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

			int[] number2 = toInts(bytes);
			int[] product2 = BaseNRemainderDecoder.multiply(number2, multiplier, addend, false);
			byte[] productBytes2 = fromInts(product2);

			assertEquals(number1, new BigInteger(1, fromInts(number2)));
			assertEquals(Arrays.toString(productBytes1), Arrays.toString(productBytes2));
		}
	}

	protected static int[] toInts(byte[] bytes) {

		int[] ints = new int[UUID_INTS];

		for (int i = 0, j = 0; i < ints.length; i++, j += 4) {
			ints[i] |= (bytes[j + 0] & 0xff) << 0x18;
			ints[i] |= (bytes[j + 1] & 0xff) << 0x10;
			ints[i] |= (bytes[j + 2] & 0xff) << 0x08;
			ints[i] |= (bytes[j + 3] & 0xff) << 0x00;
		}

		return ints;
	}

	protected static byte[] fromInts(int[] ints) {

		byte[] bytes = new byte[UUID_BYTES];

		for (int i = 0, j = 0; i < ints.length; i++, j += 4) {
			bytes[j + 0] = (byte) ((ints[i] >>> 0x18) & 0xff);
			bytes[j + 1] = (byte) ((ints[i] >>> 0x10) & 0xff);
			bytes[j + 2] = (byte) ((ints[i] >>> 0x08) & 0xff);
			bytes[j + 3] = (byte) ((ints[i] >>> 0x00) & 0xff);
		}

		return bytes;
	}

	private String getRandomString(BaseN base) {

		Random random = new Random();
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
