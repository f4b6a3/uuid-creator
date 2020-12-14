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
import com.github.f4b6a3.uuid.codec.base.UuidBase64UrlCodec;
import com.github.f4b6a3.uuid.codec.base.UuidBaseNCodec;

public class UuidSlugCodec implements UuidCodec<String> {

	private final UuidBaseNCodec codec;
	private final boolean shift;

	public UuidSlugCodec() {
		this(new UuidBase64UrlCodec(), /* shift = */ false);
	}

	public UuidSlugCodec(boolean shift) {
		this(new UuidBase64UrlCodec(), shift);
	}

	public UuidSlugCodec(UuidBaseNCodec codec) {
		this(codec, /* shift = */ false);
	}

	public UuidSlugCodec(UuidBaseNCodec codec, boolean shift) {
		this.codec = codec;
		this.shift = shift;
	}

	@Override
	public String encode(UUID uuid) {

		if (shift) {

			long long1 = uuid.getMostSignificantBits();
			long long2 = uuid.getLeastSignificantBits();

			long msb = 0;
			long lsb = 0;

			msb |= (long2 & 0xc000000000000000L); // move variant bits to positions 0 and 1
			msb |= (long1 & 0x000000000000f000L) << 46; // move version bits to positions 2 to 5
			msb |= (long1 & 0xffffffffffff0000L) >>> 6;
			msb |= (long1 & 0x0000000000000fffL) >>> 2;

			lsb |= (long1 & 0x0000000000000003L) << 62;
			lsb |= (long2 & 0x3fffffffffffffffL);

			return this.codec.encode(new UUID(msb, lsb));
		}

		return this.codec.encode(uuid);
	}

	@Override
	public UUID decode(String string) {

		if (shift) {

			UUID uuid = this.codec.decode(string);

			long long1 = uuid.getMostSignificantBits();
			long long2 = uuid.getLeastSignificantBits();

			long msb = 0;
			long lsb = 0;

			msb |= (long2 & 0xc000000000000000L) >>> 62;
			msb |= (long1 & 0x3c00000000000000L) >>> 46;
			msb |= (long1 & 0x03fffffffffffc00L) << 6;
			msb |= (long1 & 0x00000000000003ffL) << 2;

			lsb |= (long1 & 0xc000000000000000L);
			lsb |= (long2 & 0x3fffffffffffffffL);

			return new UUID(msb, lsb);
		}

		return this.codec.decode(string);
	}
}
