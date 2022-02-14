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
 * UUID variants defined by RFC-4122.
 */
public enum UuidVariant {

	VARIANT_RESERVED_NCS(0), //
	VARIANT_RFC_4122(2), //
	VARIANT_RESERVED_MICROSOFT(6), //
	VARIANT_RESERVED_FUTURE(7); //

	private final int value;

	UuidVariant(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static UuidVariant getVariant(int value) {
		for (UuidVariant variant : UuidVariant.values()) {
			if (variant.getValue() == value) {
				return variant;
			}
		}
		return null;
	}
}
