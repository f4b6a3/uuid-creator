/*
 * MIT License
 * 
 * Copyright (c) 2018-2025 Fabio Lima
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

/**
 * Codec for base-58.
 * <p>
 * It is case SENSITIVE.
 * <p>
 * It encodes using remainder operator (modulus).
 * <p>
 * The alphabet for this codec is the same used in Bitcoin (BTC).
 * 
 * @see <a href="https://tools.ietf.org/html/draft-msporny-base58-03">The Base58 Encoding Scheme</a>
 */
public final class Base58BtcCodec extends BaseNCodec {

	private static final BaseN BASE_N = new BaseN("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");

	/**
	 * A shared immutable instance.
	 */
	public static final Base58BtcCodec INSTANCE = new Base58BtcCodec();

	/**
	 * Default constructor.
	 */
	public Base58BtcCodec() {
		super(BASE_N);
	}
}
