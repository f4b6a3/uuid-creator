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

package com.github.f4b6a3.uuid.codec.slug;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.base.Base64UrlCodec;
import com.github.f4b6a3.uuid.codec.base.BaseNCodec;

/**
 * Codec for UUID Slugs.
 * 
 * A UUID Slug is a shorter string representation that can be safely included in
 * URLs and file names.
 * 
 * The {@link SlugCodec} turns a UUID into a string that does not start with
 * digits (0-9). Due to the default base-64-url alphabet, it is *case sensitive*
 * and may contain '-' and '_'.
 * 
 * The {@link Base32Codec} can be passed to the {@link SlugCodec} constructor to
 * generate base-32 slugs. Due to the base-32 alphabet, it is case insensitive
 * and it contains only letters (a-zA-Z) and digits (2-7). This encoding
 * substitution can be done to avoid the characters '-' and '_' of the
 * base-64-url encoding, but it makes the slug case insensitive.
 * 
 * To turn a UUID into a slug, the version and variant nibbles are are moved to
 * the first position of the UUID byte array. The slugs generated of the same
 * UUID version show a constant letter in the first position of the base-64-url
 * string.
 * 
 * This is how the UUID bits are rearranged:
 * 
 * <pre>
 *   00000000-0000-V000-X000-000000000000
 *                 |    |            ^
 *   ,-------------'    |   encode   |
 *   |,-----------------'      |   decode
 *   ||                        v
 *   VX000000-0000-0000-0000-000000000000
 *                              shift -->
 *   V: version nibble or character
 *   X: variant nibble or character
 * </pre>
 * 
 * This table shows the slug prefixes for each UUID version:
 * 
 * <pre>
 * VERSON  PREFIX   EXAMPLE
 *    1       G     GxA1e7vco3Ib6_mjtptP3w
 *    2       K     KryezRARVgTHLQ3zJpAXIw
 *    3       O     O9JfSS1IqIabkEWC-uXWNA
 *    4       S     S5iPSZYDt7q2w0qiIFZVwQ
 *    5       W     WY-Uv6WAY5os7Gfv4ILnvQ
 *    6       a     aMKkEoaymw0FSQNJRDL7Gw
 * </pre>
 * 
 * If you don't like the change in the UUID bytes layout before the encoding to
 * base-64-url, use the {@link Base64Codec} instead of {@link SlugCodec} to
 * generate slugs.
 * 
 *{@link SlugCodec} and {@link NcnameCodec} are very similar. The
 * difference between the two is the bit shift they do with the original UUID to
 * transform it into a string.
 * 
 * In the case someone is interested in implementing this type of slug in
 * another language, the change in the bytes layout don't have to be done with
 * bit shifting. Since a base-16 character corresponds to a nibble, the layout
 * change could be easily done by moving characters instead of by shifting bits.
 * See <code>SlugCodecTest#moveCharacters()</code>.
 * 
 * See: https://github.com/f4b6a3/uuid-creator/issues/30
 */
public final class SlugCodec implements UuidCodec<String> {

	private final BaseNCodec codec;

	public SlugCodec() {
		this(new Base64UrlCodec());
	}

	/**
	 * @param codec a base-n codec to be used (the default is base-64-url)
	 * @param shift a flag to shift the variant and variant bits to the front of the
	 *              byte array.
	 */
	public SlugCodec(BaseNCodec codec) {
		if (codec == null) {
			throw new IllegalArgumentException("Null codec");
		}
		this.codec = codec;
	}

	@Override
	public String encode(UUID uuid) {

		long long1 = uuid.getMostSignificantBits();
		long long2 = uuid.getLeastSignificantBits();

		long msb = 0;
		long lsb = 0;

		msb |= (long1 & 0x000000000000f000L) << 48; // move version nibble to bit positions 0, 1, 2, and 3
		msb |= (long2 & 0xf000000000000000L) >>> 4; // move variant nibble to bit positions 4, 5, 6, and 7
		msb |= (long1 & 0xffffffffffff0000L) >>> 8;
		msb |= (long1 & 0x0000000000000fffL) >>> 4;

		lsb |= (long1 & 0x000000000000000fL) << 60;
		lsb |= (long2 & 0x0fffffffffffffffL);

		return this.codec.encode(new UUID(msb, lsb));
	}

	@Override
	public UUID decode(String string) {

		UUID uuid = this.codec.decode(string);

		long long1 = uuid.getMostSignificantBits();
		long long2 = uuid.getLeastSignificantBits();

		long msb = 0;
		long lsb = 0;

		msb |= (long1 & 0xf000000000000000L) >>> 48; // move version nibble to its original position
		msb |= (long2 & 0xf000000000000000L) >>> 60; // move variant nibble to its original position
		msb |= (long1 & 0x00ffffffffffff00L) << 8;
		msb |= (long1 & 0x00000000000000ffL) << 4;

		lsb |= (long1 & 0x0f00000000000000L) << 4;
		lsb |= (long2 & 0x0fffffffffffffffL);

		return new UUID(msb, lsb);
	}
}
