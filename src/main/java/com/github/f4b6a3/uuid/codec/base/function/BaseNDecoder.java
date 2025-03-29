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

package com.github.f4b6a3.uuid.codec.base.function;

import java.util.UUID;
import java.util.function.Function;

import com.github.f4b6a3.uuid.codec.base.BaseN;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.immutable.ByteArray;

/**
 * Abstract function to be extended by all decoder functions of this package.
 * <p>
 * If the base-n is case insensitive, it decodes in lower case and upper case.
 */
public abstract class BaseNDecoder implements Function<String, UUID> {

	/**
	 * The base-n.
	 */
	protected final BaseN base;

	/**
	 * The base-n map.
	 */
	protected final ByteArray map;

	/**
	 * @param base an enumeration that represents the base-n encoding
	 */
	public BaseNDecoder(BaseN base) {
		this.base = base;
		this.map = base.getMap();
	}

	protected long get(String string, int i) {

		final int chr = string.charAt(i);
		if (chr > 255) {
			throw InvalidUuidException.newInstance(string);
		}

		final byte value = map.get(chr);
		if (value < 0) {
			throw InvalidUuidException.newInstance(string);
		}
		return value & 0xffL;
	}
}