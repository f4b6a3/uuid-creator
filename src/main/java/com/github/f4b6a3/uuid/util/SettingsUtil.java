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

package com.github.f4b6a3.uuid.util;

public final class SettingsUtil {

	protected static final String PROPERTY_PREFIX = "uuidcreator";
	protected static final String PROPERTY_NODEID = "nodeid";

	protected SettingsUtil() {
	}

	public static long getNodeIdentifier() {
		String value = getProperty(PROPERTY_NODEID);

		if (value == null || !value.matches("^(0x|0X)?[0-9A-Fa-f]+$")) {
			return 0;
		}

		if (!value.toLowerCase().startsWith("0x")) {
			value = "0x" + value;
		}

		try {
			return Long.decode(value) & 0x0000FFFFFFFFFFFFL;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static void setNodeIdentifier(long nodeid) {
		String value = Long.toHexString(nodeid & 0x0000FFFFFFFFFFFFL);
		setProperty(PROPERTY_NODEID, value);
	}

	protected static String getProperty(String name) {

		String fullName = getPropertyName(name);
		String value = System.getProperty(fullName);
		if (!isEmpty(value)) {
			return value;
		}

		fullName = getEnvinronmentName(name);
		value = System.getenv(fullName);
		if (!isEmpty(value)) {
			return value;
		}

		return null;
	}

	protected static void setProperty(String key, String value) {
		System.setProperty(getPropertyName(key), value);
	}

	protected static void clearProperty(String key) {
		System.clearProperty(getPropertyName(key));
	}

	protected static String getPropertyName(String key) {
		return String.join(".", PROPERTY_PREFIX, key);
	}

	protected static String getEnvinronmentName(String key) {
		return String.join("_", PROPERTY_PREFIX, key).toUpperCase().replace(".", "_");
	}

	private static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}
}
