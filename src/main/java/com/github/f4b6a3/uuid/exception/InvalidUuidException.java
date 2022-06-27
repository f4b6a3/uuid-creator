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

package com.github.f4b6a3.uuid.exception;

import java.util.Arrays;

public final class InvalidUuidException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidUuidException(String message) {
		super(message);
	}

	public InvalidUuidException(String message, Throwable cause) {
		super(message, cause);
	}

	public static InvalidUuidException newInstance(Object obj) {

		String string;
		if (obj == null) {
			string = null;
		} else if (obj instanceof char[]) {
			string = String.valueOf((char[]) obj);
		} else if (obj.getClass().isArray()) {
			string = Arrays.toString((byte[]) obj);
		} else {
			string = String.valueOf(obj);
		}

		if (string != null) {
			string = "\"" + string + "\"";
		}

		return new InvalidUuidException("Invalid UUID: " + string);
	}
}
