/*
 * MIT License
 * 
 * Copyright (c) 2018-2020 Fabio Lima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.f4b6a3.uuid.codec.name;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.base.Base64UrlCodec;
import com.github.f4b6a3.uuid.codec.base.BaseNCodec;
import com.github.f4b6a3.uuid.codec.slug.SlugCodec;

/**
 * Codec for UUID NCNames.
 * 
 * A UUID NCName is a shorter string representation that conforms to the
 * constraints of various other identifiers such as NCName in XML documents.
 * 
 * The {@link NcnameCodec} turns a UUID into a string that does not start with
 * digits (0-9). But due to the default base-64-url encoding, it may contain '-'
 * and '_'.
 * 
 * The {@link Base32Codec} can be passed to the {@link NcnameCodec} constructor
 * to generate base-32 NCNames. Due to the base-32 alphabet, it is case
 * insensitive and it contains only letters (a-zA-Z) and digits (2-7). This
 * encoding substitution can be done to avoid the characters '-' and '_' of the
 * base-64-url encoding, but it makes the NCName case insensitive.
 * 
 * The transformation scheme is outlined in this RFC:
 * https://tools.ietf.org/html/draft-taylor-uuid-ncname-00
 * 
 * Both {@link SlugCodec} and {@link NcnameCodec} are very similar. The
 * difference between the two is the bit shift they do with the original UUID to
 * transform it into a string.
 * 
 * See: https://github.com/f4b6a3/uuid-creator/issues/31
 */
public final class NcnameCodec implements UuidCodec<String> {

	private final int base;
	private final int length;
	private final int shift;
	private final BaseNCodec codec;

	private static final UuidCodec<byte[]> CODEC_BYTES = new BinaryCodec();

	// array 0: upper case base-64
	// array 1: lower case for base-16 and base-32
	private static final char[][] VERSION_CHARS = { //
			{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P' }, //
			{ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p' }, //
	};

	private static final long[] VERSION_MAP = new long[128];
	static {
		// initialize the array with -1
		for (int i = 0; i < VERSION_MAP.length; i++) {
			VERSION_MAP[i] = -1;
		}
		// upper case for base-16 and base-32
		VERSION_MAP['A'] = 0x0;
		VERSION_MAP['B'] = 0x1;
		VERSION_MAP['C'] = 0x2;
		VERSION_MAP['D'] = 0x3;
		VERSION_MAP['E'] = 0x4;
		VERSION_MAP['F'] = 0x5;
		VERSION_MAP['G'] = 0x6;
		VERSION_MAP['H'] = 0x7;
		VERSION_MAP['I'] = 0x8;
		VERSION_MAP['J'] = 0x9;
		VERSION_MAP['K'] = 0xa;
		VERSION_MAP['L'] = 0xb;
		VERSION_MAP['M'] = 0xc;
		VERSION_MAP['N'] = 0xd;
		VERSION_MAP['O'] = 0xe;
		VERSION_MAP['P'] = 0xf;
		// lower case for base-64
		VERSION_MAP['a'] = 0x0;
		VERSION_MAP['b'] = 0x1;
		VERSION_MAP['c'] = 0x2;
		VERSION_MAP['d'] = 0x3;
		VERSION_MAP['e'] = 0x4;
		VERSION_MAP['f'] = 0x5;
		VERSION_MAP['g'] = 0x6;
		VERSION_MAP['h'] = 0x7;
		VERSION_MAP['i'] = 0x8;
		VERSION_MAP['j'] = 0x9;
		VERSION_MAP['k'] = 0xa;
		VERSION_MAP['l'] = 0xb;
		VERSION_MAP['m'] = 0xc;
		VERSION_MAP['n'] = 0xd;
		VERSION_MAP['o'] = 0xe;
		VERSION_MAP['p'] = 0xf;
	}

	// padding used to circumvent the decoder's length validation
	private static final char DECODER_PADDING = 'A'; // 'A' = 0

	public NcnameCodec() {
		this(new Base64UrlCodec());
	}

	public NcnameCodec(BaseNCodec codec) {

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

		// if base is 64, use upper case version, else use lower case
		char v = this.base == 64 ? VERSION_CHARS[0][version] : VERSION_CHARS[1][version];

		return v + encoded;
	}

	@Override
	public UUID decode(String ncname) {

		int version = (int) VERSION_MAP[ncname.charAt(0)];
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

	private static int[] toInts(byte[] bytes) {
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

	private static byte[] fromInts(int[] ints) {
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
}
