/*
 * MIT License
 * 
 * Copyright (c) 2018-2024 Fabio Lima
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

package com.github.f4b6a3.uuid.codec.other;

import java.util.Arrays;
import java.util.UUID;

import com.github.f4b6a3.uuid.codec.StandardBinaryCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.base.Base32Codec;
import com.github.f4b6a3.uuid.codec.base.Base64UrlCodec;
import com.github.f4b6a3.uuid.codec.base.BaseNCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidValidator;
import com.github.f4b6a3.uuid.util.immutable.ByteArray;
import com.github.f4b6a3.uuid.util.immutable.CharArray;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

/**
 * Codec for UUID NCNames.
 * <p>
 * A UUID NCName is a shorter string representation that conforms to the
 * constraints of various other identifiers such as NCName in XML documents.
 * <p>
 * The {@link NcnameCodec} turns a UUID into a string that does not start with
 * digits (0-9). But due to the default base-64-url encoding, it is <b>case
 * sensitive</b> and may contain '-' and '_'.
 * <p>
 * The {@link Base32Codec} can be passed to the {@link NcnameCodec} constructor
 * to generate base-32 NCNames. Due to the base-32 alphabet, it is case
 * insensitive and it contains only letters (a-zA-Z) and digits (2-7). This
 * encoding substitution can be done to avoid the characters '-' and '_' of the
 * base-64-url encoding, but it makes the NCName case insensitive.
 * <p>
 * The transformation scheme is outlined in this RFC:
 * https://tools.ietf.org/html/draft-taylor-uuid-ncname-00. The draft describes
 * schemes for base-64-url and base-32.
 * <p>
 * {@link SlugCodec} and {@link NcnameCodec} are very similar. The difference
 * between the two is the bit shift they do with the original UUID to transform
 * it into a string.
 * 
 * @see <a href="https://github.com/f4b6a3/uuid-creator/issues/31">UUID
 *      NCNames</a>
 */
public final class NcnameCodec implements UuidCodec<String> {

	/**
	 * A shared immutable instance using `base64url`
	 */
	public static final NcnameCodec INSTANCE = new NcnameCodec();

	private final int radix;
	private final int length;
	private final int shift;
	private final char padding;
	private final BaseNCodec codec;

	private static final CharArray VERSION_UPPERCASE = CharArray.from("ABCDEFGHIJKLMNOP".toCharArray());
	private static final CharArray VERSION_LOWERCASE = CharArray.from("abcdefghijklmnop".toCharArray());

	private static final ByteArray VERSION_MAP;
	static {
		
		// initialize the array with -1
		byte[] mapping = new byte[256];
		Arrays.fill(mapping, (byte) -1);

		// upper case for base-64
		mapping['A'] = 0x0;
		mapping['B'] = 0x1;
		mapping['C'] = 0x2;
		mapping['D'] = 0x3;
		mapping['E'] = 0x4;
		mapping['F'] = 0x5;
		mapping['G'] = 0x6;
		mapping['H'] = 0x7;
		mapping['I'] = 0x8;
		mapping['J'] = 0x9;
		mapping['K'] = 0xa;
		mapping['L'] = 0xb;
		mapping['M'] = 0xc;
		mapping['N'] = 0xd;
		mapping['O'] = 0xe;
		mapping['P'] = 0xf;
		// lower case for base-16 and base-32
		mapping['a'] = 0x0;
		mapping['b'] = 0x1;
		mapping['c'] = 0x2;
		mapping['d'] = 0x3;
		mapping['e'] = 0x4;
		mapping['f'] = 0x5;
		mapping['g'] = 0x6;
		mapping['h'] = 0x7;
		mapping['i'] = 0x8;
		mapping['j'] = 0x9;
		mapping['k'] = 0xa;
		mapping['l'] = 0xb;
		mapping['m'] = 0xc;
		mapping['n'] = 0xd;
		mapping['o'] = 0xe;
		mapping['p'] = 0xf;

		VERSION_MAP = ByteArray.from(mapping);
	}

	/**
	 * Default constructor.
	 */
	public NcnameCodec() {
		this(Base64UrlCodec.INSTANCE);
	}

	/**
	 * Constructor with a base-n codec.
	 * 
	 * @param codec a base-n codec
	 */
	public NcnameCodec(BaseNCodec codec) {

		if (!(codec instanceof Base64UrlCodec || codec instanceof Base32Codec)) {
			throw new IllegalArgumentException("Unsupported base-n codec");
		}

		this.codec = codec;
		this.radix = codec.getBase().getRadix();
		this.length = codec.getBase().getLength();
		this.padding = codec.getBase().getPadding();

		switch (this.radix) {
		case 32:
			this.shift = 1;
			break;
		case 64:
			this.shift = 2;
			break;
		default:
			this.shift = 0; // unspecified
			break;
		}
	}

	/**
	 * Get a NCName from a UUID.
	 * 
	 * @param uuid a UUID
	 * @return a NCName
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public String encode(UUID uuid) {

		UuidValidator.validate(uuid);

		int version = uuid.version();
		byte[] bytes = StandardBinaryCodec.INSTANCE.encode(uuid);
		int[] ints = ByteUtil.toInts(bytes);

		int variant = (ints[2] & 0xf0000000) >>> 24;

		ints[1] = (ints[1] & 0xffff0000) | ((ints[1] & 0x00000fff) << 4) | ((ints[2] & 0x0fffffff) >>> 24);
		ints[2] = (ints[2] & 0x00ffffff) << 8 | (ints[3] >>> 24);
		ints[3] = (ints[3] << 8) | variant;

		bytes = ByteUtil.fromInts(ints);
		bytes[15] = (byte) ((bytes[15] & 0xff) >>> this.shift);

		UUID uuuu = StandardBinaryCodec.INSTANCE.decode(bytes);
		String encoded = this.codec.encode(uuuu).substring(0, this.length - 1);

		// if base is 64, use upper case version, else use lower case
		char v = this.radix == 64 ? VERSION_UPPERCASE.get(version) : VERSION_LOWERCASE.get(version);

		return v + encoded;
	}

	/**
	 * Get a UUID from a NCName.
	 * 
	 * @param ncname a NCName
	 * @return a UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public UUID decode(String ncname) {

		if (ncname == null || ncname.length() != this.length) {
			throw new InvalidUuidException("Invalid UUID NCName: \"" + ncname + "\"");
		}

		// check if the bookends are valid chars: [A-Pa-p]
		int bookend1 = (int) VERSION_MAP.get(ncname.charAt(0));
		int bookend2 = (int) VERSION_MAP.get(ncname.charAt(ncname.length() - 1));
		if (bookend1 == -1 || bookend2 == -1) {
			throw new InvalidUuidException("Invalid UUID NCName: \"" + ncname + "\"");
		}

		int version = bookend1 & 0xf;

		String substring = ncname.substring(1, ncname.length());
		UUID uuid = this.codec.decode(substring + padding);

		byte[] bytes = StandardBinaryCodec.INSTANCE.encode(uuid);
		bytes[15] = (byte) ((bytes[15] & 0xff) << this.shift);

		int[] ints = ByteUtil.toInts(bytes);

		int variant = (ints[3] & 0xf0) << 24;

		ints[3] >>>= 8;
		ints[3] |= ((ints[2] & 0xff) << 24);
		ints[2] >>>= 8;
		ints[2] |= ((ints[1] & 0xf) << 24) | variant;
		ints[1] = (ints[1] & 0xffff0000) | (version << 12) | ((ints[1] >>> 4) & 0xfff);

		bytes = ByteUtil.fromInts(ints);

		return StandardBinaryCodec.INSTANCE.decode(bytes);
	}
}
