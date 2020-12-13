package com.github.f4b6a3.uuid.codec.name;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UuidBytesCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.base.UuidBase64UrlCodec;
import com.github.f4b6a3.uuid.codec.base.UuidBaseNCodec;

public class UuidNcnameCodec implements UuidCodec<String> {

	private final int base;
	private final int length;
	private final int shift;
	private final UuidBaseNCodec codec;

	private static final UuidCodec<byte[]> CODEC_BYTES = new UuidBytesCodec();

	protected static final char[] VERSION_CHARS = //
			{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P' };

	protected static final long[] VERSION_VALUES = new long[128];
	static {
		initArrayValues(VERSION_VALUES);
		// Numbers
		VERSION_VALUES['A'] = 0x0;
		VERSION_VALUES['B'] = 0x1;
		VERSION_VALUES['C'] = 0x2;
		VERSION_VALUES['D'] = 0x3;
		VERSION_VALUES['E'] = 0x4;
		VERSION_VALUES['F'] = 0x5;
		VERSION_VALUES['G'] = 0x6;
		VERSION_VALUES['H'] = 0x7;
		VERSION_VALUES['I'] = 0x8;
		VERSION_VALUES['J'] = 0x9;
		VERSION_VALUES['K'] = 0xa;
		VERSION_VALUES['L'] = 0xb;
		VERSION_VALUES['M'] = 0xc;
		VERSION_VALUES['N'] = 0xd;
		VERSION_VALUES['O'] = 0xe;
		VERSION_VALUES['P'] = 0xf;
	}

	// padding used to circumvent the decoder's length validation
	protected static final char DECODER_PADDING = 'A'; // 'A' = 0

	public UuidNcnameCodec() {
		this(new UuidBase64UrlCodec());
	}

	public UuidNcnameCodec(UuidBaseNCodec codec) {

		this.codec = codec;
		this.base = codec.getBase().getNumber();
		this.length = codec.getBase().getLength();

		switch (this.base) {
		case 32:
			this.shift = 1;
			break;
		case 64:
			this.shift = 2;
			break;
		default:
			this.shift = 0; // unspecified
		}
	}

	@Override
	public String encode(final UUID uuid) {

		int version = uuid.version();
		byte[] bytes = CODEC_BYTES.encode(uuid);
		int[] ints = toInts(bytes);

		int variant = (ints[2] & 0xf0000000) >>> 24;

		ints[1] = (ints[1] & 0xffff0000) | ((ints[1] & 0x00000fff) << 4) | ((ints[2] & 0x0fffffff) >>> 24);
		ints[2] = (ints[2] & 0x00ffffff) << 8 | (ints[3] >>> 24);
		ints[3] = (ints[3] << 8) | variant;

		bytes = fromInts(ints);
		bytes[15] = (byte) ((bytes[15] & 0xff) >>> this.shift);

		UUID uuuu = CODEC_BYTES.decode(bytes);
		String encoded = this.codec.encode(uuuu).substring(0, this.length - 1);
		return VERSION_CHARS[version] + encoded;
	}

	@Override
	public UUID decode(String ncname) {

		int version = (int) VERSION_VALUES[ncname.charAt(0)];
		String substring = ncname.substring(1, ncname.length());
		UUID uuid = this.codec.decode(substring + DECODER_PADDING);

		byte[] bytes = CODEC_BYTES.encode(uuid);
		bytes[15] <<= this.shift;

		version &= 0xf;

		int[] ints = toInts(bytes);

		int variant = (ints[3] & 0xf0) << 24;

		ints[3] >>>= 8;
		ints[3] |= ((ints[2] & 0xff) << 24);
		ints[2] >>>= 8;
		ints[2] |= ((ints[1] & 0xf) << 24) | variant;
		ints[1] = (ints[1] & 0xffff0000) | (version << 12) | ((ints[1] >>> 4) & 0xfff);

		bytes = fromInts(ints);

		return CODEC_BYTES.decode(bytes);
	}

	protected static int[] toInts(byte[] bytes) {
		int[] ints = new int[4];
		ints[0] |= (bytes[0x0] & 0xffL) << 24;
		ints[0] |= (bytes[0x1] & 0xffL) << 16;
		ints[0] |= (bytes[0x2] & 0xffL) << 8;
		ints[0] |= (bytes[0x3] & 0xffL);
		ints[1] |= (bytes[0x4] & 0xffL) << 24;
		ints[1] |= (bytes[0x5] & 0xffL) << 16;
		ints[1] |= (bytes[0x6] & 0xffL) << 8;
		ints[1] |= (bytes[0x7] & 0xffL);
		ints[2] |= (bytes[0x8] & 0xffL) << 24;
		ints[2] |= (bytes[0x9] & 0xffL) << 16;
		ints[2] |= (bytes[0xa] & 0xffL) << 8;
		ints[2] |= (bytes[0xb] & 0xffL);
		ints[3] |= (bytes[0xc] & 0xffL) << 24;
		ints[3] |= (bytes[0xd] & 0xffL) << 16;
		ints[3] |= (bytes[0xe] & 0xffL) << 8;
		ints[3] |= (bytes[0xf] & 0xffL);
		return ints;
	}

	protected static byte[] fromInts(int[] ints) {
		byte[] bytes = new byte[16];
		bytes[0x0] = (byte) (ints[0] >>> 24);
		bytes[0x1] = (byte) (ints[0] >>> 16);
		bytes[0x2] = (byte) (ints[0] >>> 8);
		bytes[0x3] = (byte) (ints[0]);
		bytes[0x4] = (byte) (ints[1] >>> 24);
		bytes[0x5] = (byte) (ints[1] >>> 16);
		bytes[0x6] = (byte) (ints[1] >>> 8);
		bytes[0x7] = (byte) (ints[1]);
		bytes[0x8] = (byte) (ints[2] >>> 24);
		bytes[0x9] = (byte) (ints[2] >>> 16);
		bytes[0xa] = (byte) (ints[2] >>> 8);
		bytes[0xb] = (byte) (ints[2]);
		bytes[0xc] = (byte) (ints[3] >>> 24);
		bytes[0xd] = (byte) (ints[3] >>> 16);
		bytes[0xe] = (byte) (ints[3] >>> 8);
		bytes[0xf] = (byte) (ints[3]);
		return bytes;
	}

	/**
	 * Method used to initiate array values with -1.
	 * 
	 * @param array an array of values
	 */
	private static void initArrayValues(long[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] = -1;
		}
	}
}
