package com.github.f4b6a3.uuid.codec.base.function;

import java.math.BigInteger;
import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UuidBytesCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.base.UuidBaseN;

public class UuidBase62Encoder extends UuidBaseNEncoder {

	// TODO: zero pad
	public UuidBase62Encoder(char[] alphabet) {
		super(UuidBaseN.BASE_62, alphabet);
	}

	@Override
	public String apply(UUID uuid) {
		byte[] bytes16 = UuidBytesCodecLazyHolder.CODEC.encode(uuid);
		byte[] bytes17 = new byte[17];
		System.arraycopy(bytes16, 0, bytes17, 1, 16);
		BigInteger number = new BigInteger(bytes17);

		BigInteger n = number;
		BigInteger b = BigInteger.valueOf(base.getNumber());
		StringBuilder builder = new StringBuilder();

		while (n.compareTo(BigInteger.ZERO) > 0) {
			builder.append(alphabet[n.remainder(b).intValue()]);
			n = n.divide(b);
		}

		return builder.reverse().toString();
	}

	private static class UuidBytesCodecLazyHolder {
		protected static final UuidCodec<byte[]> CODEC = new UuidBytesCodec();
	}
}
