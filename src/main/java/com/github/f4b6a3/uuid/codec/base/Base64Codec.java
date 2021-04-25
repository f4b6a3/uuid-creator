/*
 * MIT License
 * 
 * Copyright (c) 2018-2021 Fabio Lima
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
 * Codec for base-64 as defined in RFC-4648.
 * 
 * It is case SENSITIVE.
 * 
 * The only difference between base-64 and base-64-url is that the second
 * substitutes the chars '+' and '/' with '-' and '_'.
 * 
 * See: https://tools.ietf.org/html/rfc4648
 */
public final class Base64Codec extends BaseNCodec {

	/**
	 * A shared immutable instance.
	 */
	public static final Base64Codec INSTANCE = new Base64Codec();

	public Base64Codec() {
		super(BaseN.BASE_64);
	}
}
