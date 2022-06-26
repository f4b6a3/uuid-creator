/*
 * MIT License
 * 
 * Copyright (c) 2018-2022 Fabio Lima
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

package com.github.f4b6a3.uuid.codec;

import java.util.UUID;

import com.github.f4b6a3.uuid.exception.InvalidUuidException;

/**
 * Interface to be implemented by all codecs of this package.
 * 
 * All implementations of this interface throw {@link InvalidUuidException} if
 * an invalid argument argument is given.
 * 
 * The {@link RuntimeException} cases that can be detected beforehand are
 * translated into an {@link InvalidUuidException}.
 * 
 * @param <T> the type encoded to and decoded from.
 */
public interface UuidCodec<T> {

	/**
	 * Get a generic type from a UUID.
	 * 
	 * @param uuid a UUID
	 * @return a generic type
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public T encode(UUID uuid);

	/**
	 * Get a UUID from a generic type.
	 * 
	 * @param type a generic type
	 * @return a UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public UUID decode(T type);
}