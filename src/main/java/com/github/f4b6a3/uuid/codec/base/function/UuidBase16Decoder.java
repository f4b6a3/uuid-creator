package com.github.f4b6a3.uuid.codec.base.function;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.base.UuidBaseN;

public class UuidBase16Decoder extends UuidBaseNDecoder {

	public UuidBase16Decoder(char[] alphabet) {
		super(UuidBaseN.BASE_16, alphabet);
	}

	@Override
	public UUID apply(String string) {

		char[] chars = toCharArray(string);

		long msb = 0;
		long lsb = 0;

		msb |= alphabetValues[chars[0x00]] << 60;
		msb |= alphabetValues[chars[0x01]] << 56;
		msb |= alphabetValues[chars[0x02]] << 52;
		msb |= alphabetValues[chars[0x03]] << 48;
		msb |= alphabetValues[chars[0x04]] << 44;
		msb |= alphabetValues[chars[0x05]] << 40;
		msb |= alphabetValues[chars[0x06]] << 36;
		msb |= alphabetValues[chars[0x07]] << 32;
		msb |= alphabetValues[chars[0x08]] << 28;
		msb |= alphabetValues[chars[0x09]] << 24;
		msb |= alphabetValues[chars[0x0a]] << 20;
		msb |= alphabetValues[chars[0x0b]] << 16;
		msb |= alphabetValues[chars[0x0c]] << 12;
		msb |= alphabetValues[chars[0x0d]] << 8;
		msb |= alphabetValues[chars[0x0e]] << 4;
		msb |= alphabetValues[chars[0x0f]];

		lsb |= alphabetValues[chars[0x10]] << 60;
		lsb |= alphabetValues[chars[0x11]] << 56;
		lsb |= alphabetValues[chars[0x12]] << 52;
		lsb |= alphabetValues[chars[0x13]] << 48;
		lsb |= alphabetValues[chars[0x14]] << 44;
		lsb |= alphabetValues[chars[0x15]] << 40;
		lsb |= alphabetValues[chars[0x16]] << 36;
		lsb |= alphabetValues[chars[0x17]] << 32;
		lsb |= alphabetValues[chars[0x18]] << 28;
		lsb |= alphabetValues[chars[0x19]] << 24;
		lsb |= alphabetValues[chars[0x1a]] << 20;
		lsb |= alphabetValues[chars[0x1b]] << 16;
		lsb |= alphabetValues[chars[0x1c]] << 12;
		lsb |= alphabetValues[chars[0x1d]] << 8;
		lsb |= alphabetValues[chars[0x1e]] << 4;
		lsb |= alphabetValues[chars[0x1f]];

		return new UUID(msb, lsb);
	}
}