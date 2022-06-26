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

package com.github.f4b6a3.uuid.enums;

/**
 * UUID versions defined by RFC-4122.
 */
public enum UuidVersion {

	VERSION_UNKNOWN(0), //
	VERSION_TIME_BASED(1), //
	VERSION_DCE_SECURITY(2), //
	VERSION_NAME_BASED_MD5(3), //
	VERSION_RANDOM_BASED(4), //
	VERSION_NAME_BASED_SHA1(5), //
	VERSION_TIME_ORDERED(6), //
	VERSION_TIME_ORDERED_EPOCH(7), //
	VERSION_CUSTOM(8); //

	private final int value;

	UuidVersion(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static UuidVersion getVersion(int value) {
		for (UuidVersion version : UuidVersion.values()) {
			if (version.getValue() == value) {
				return version;
			}
		}
		return null;
	}
}
