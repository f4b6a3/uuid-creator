package com.github.f4b6a3.uuid.codec.base.function;

import java.math.BigInteger;
import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UuidBytesCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.base.UuidBaseN;

public class UuidBase62Decoder extends UuidBaseNDecoder {

	// TODO: zero pad
	public UuidBase62Decoder(char[] alphabet) {
		super(UuidBaseN.BASE_62, alphabet);
	}

	@Override
	public UUID apply(String string) {

		BigInteger number = BigInteger.ZERO;
		BigInteger b = BigInteger.valueOf(base.getNumber());

		for (char c : string.toCharArray()) {
			int d = map(c, alphabet);
			number = b.multiply(number).add(BigInteger.valueOf(d));
		}

		byte[] bytes16 = new byte[16];
		byte[] bytes17 = number.toByteArray();
		System.arraycopy(bytes17, 1, bytes16, 0, 16);
		return LazyHolder.CODEC.decode(bytes16);
	}

	protected static int map(char c, char[] alphabet) {
		for (int i = 0; i < alphabet.length; i++) {
			if (alphabet[i] == c) {
				return (byte) i;
			}
		}
		return (byte) '0';
	}

	private static class LazyHolder {
		protected static final UuidCodec<byte[]> CODEC = new UuidBytesCodec();
	}
}