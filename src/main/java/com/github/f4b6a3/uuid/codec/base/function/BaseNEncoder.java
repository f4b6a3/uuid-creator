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
import com.github.f4b6a3.uuid.util.immutable.CharArray;

/**
 * Abstract function to be extended by all encoder functions of this package.
 * <p>
 * If the base-n is case insensitive, it encodes in lower case only.
 */
public abstract class BaseNEncoder implements Function<UUID, String> {

	/**
	 * The base-n.
	 */
	protected final BaseN base;

	/**
	 * The base-n alphabet.
	 */
	protected final CharArray alphabet;

	/**
	 * @param base an object that represents the base-n encoding
	 */
	public BaseNEncoder(BaseN base) {
		this.base = base;
		this.alphabet = base.getAlphabet();
	}

	protected char get(final long index) {
		return alphabet.get((int) index);
	}
}
