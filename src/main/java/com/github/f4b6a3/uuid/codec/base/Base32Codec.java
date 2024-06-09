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

package com.github.f4b6a3.uuid.codec.base;

import com.github.f4b6a3.uuid.codec.base.function.Base32Decoder;
import com.github.f4b6a3.uuid.codec.base.function.Base32Encoder;

/**
 * Codec for base-32 as defined in RFC-4648.
 * <p>
 * It is case insensitive, so it decodes from lower and upper case, but encodes
 * to lower case only.
 * <p>
 * This codec complies with RFC-4648, encoding a byte array sequentially. If you
 * need a codec that encodes integers using the remainder operator (modulus),
 * use the static factory {@link BaseNCodec#newInstance(BaseN)}.
 * 
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4648">RFC-4648</a>
 */
public final class Base32Codec extends BaseNCodec {

	private static final BaseN BASE_N = new BaseN("a-z2-7");

	/**
	 * A shared immutable instance.
	 */
	public static final Base32Codec INSTANCE = new Base32Codec();

	/**
	 * Default constructor.
	 */
	public Base32Codec() {
		super(BASE_N, new Base32Encoder(BASE_N), new Base32Decoder(BASE_N));
	}
}
