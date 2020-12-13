package com.github.f4b6a3.uuid.codec.base.function;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.base.UuidBaseN;

public class UuidBase64Decoder extends UuidBaseNDecoder {

	public UuidBase64Decoder(char[] alphabet) {
		super(UuidBaseN.BASE_64, alphabet);
	}

	@Override
	public UUID apply(String string) {
		
		char[] chars = toCharArray(string);

		long msb = 0;
		long lsb = 0;

		msb |= alphabetValues[chars[0x00]] << 58;
		msb |= alphabetValues[chars[0x01]] << 52;
		msb |= alphabetValues[chars[0x02]] << 46;
		msb |= alphabetValues[chars[0x03]] << 40;
		msb |= alphabetValues[chars[0x04]] << 34;
		msb |= alphabetValues[chars[0x05]] << 28;
		msb |= alphabetValues[chars[0x06]] << 22;
		msb |= alphabetValues[chars[0x07]] << 16;
		msb |= alphabetValues[chars[0x08]] << 10;
		msb |= alphabetValues[chars[0x09]] << 4;

		msb |= alphabetValues[chars[0x0a]] >>> 2;
		lsb |= alphabetValues[chars[0x0a]] << 62;

		lsb |= alphabetValues[chars[0x0b]] << 56;
		lsb |= alphabetValues[chars[0x0c]] << 50;
		lsb |= alphabetValues[chars[0x0d]] << 44;
		lsb |= alphabetValues[chars[0x0e]] << 38;
		lsb |= alphabetValues[chars[0x0f]] << 32;
		lsb |= alphabetValues[chars[0x10]] << 26;
		lsb |= alphabetValues[chars[0x11]] << 20;
		lsb |= alphabetValues[chars[0x12]] << 14;
		lsb |= alphabetValues[chars[0x13]] << 8;
		lsb |= alphabetValues[chars[0x14]] << 2;
		lsb |= alphabetValues[chars[0x15]] >>> 4;

		return new UUID(msb, lsb);
	}
}
