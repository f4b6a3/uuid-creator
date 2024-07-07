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

package com.github.f4b6a3.uuid.util.internal;

/**
 * Utility class that reads system properties and environment variables.
 * <p>
 * List of system properties:
 * <ul>
 * <li>uuidcreator.node
 * <li>uuidcreator.securerandom
 * </ul>
 * <p>
 * List of environment variables:
 * <ul>
 * <li>UUIDCREATOR_NODE
 * <li>UUIDCREATOR_SECURERANDOM
 * </ul>
 * <p>
 * System properties has prevalence over environment variables.
 */
public final class SettingsUtil {

	/**
	 * The property name prefix.
	 */
	protected static final String PROPERTY_PREFIX = "uuidcreator";

	/**
	 * The property name for the node number.
	 */
	public static final String PROPERTY_NODE = "node";

	/**
	 * The property name for the secure random algorithm.
	 */
	public static final String PROPERTY_SECURERANDOM = "securerandom";

	/**
	 * Default constructor.
	 */
	protected SettingsUtil() {
	}

	/**
	 * Get the node identifier.
	 * 
	 * @return a number
	 */
	public static Long getNodeIdentifier() {
		String value = getProperty(PROPERTY_NODE);
		if (value == null) {
			return null;
		}
		try {
			return Long.decode(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Set the node identifier
	 * 
	 * @param node a number
	 */
	public static void setNodeIdentifier(Long node) {
		String value = Long.toString(node);
		setProperty(PROPERTY_NODE, value);
	}

	/**
	 * Get the secure random algorithm.
	 * 
	 * @return a string
	 */
	public static String getSecureRandom() {
		return getProperty(PROPERTY_SECURERANDOM);
	}

	/**
	 * Set the secure random algorithm
	 * 
	 * @param algorithm a string
	 */
	public static void setSecureRandom(String algorithm) {
		setProperty(PROPERTY_SECURERANDOM, algorithm);
	}

	/**
	 * Get a property.
	 * 
	 * @param name the name
	 * @return a string
	 */
	public static String getProperty(String name) {

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

	/**
	 * Set a property.
	 * 
	 * @param key   the key
	 * @param value the value
	 */
	public static void setProperty(String key, String value) {
		System.setProperty(getPropertyName(key), value);
	}

	/**
	 * Clear a property.
	 * 
	 * @param key the key
	 */
	public static void clearProperty(String key) {
		System.clearProperty(getPropertyName(key));
	}

	/**
	 * Get a property name.
	 * 
	 * @param key a key
	 * @return a string
	 */
	protected static String getPropertyName(String key) {
		return String.join(".", PROPERTY_PREFIX, key);
	}

	/**
	 * Get an environment variable name.
	 * 
	 * @param key a key
	 * @return a string
	 */
	protected static String getEnvinronmentName(String key) {
		return String.join("_", PROPERTY_PREFIX, key).toUpperCase().replace(".", "_");
	}

	private static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}
}
