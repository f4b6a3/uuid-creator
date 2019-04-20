package com.github.f4b6a3.uuid.util;

public class SettingsUtil {

	public static final String PROPERTY_PREFIX = "uuidcreator";
	public static final String PROPERTY_CLOCKSEQ = "clockseq";
	public static final String PROPERTY_NODEID = "nodeid";

	public static long getNodeIdentifier() {
		String nodeid = getProperty(PROPERTY_NODEID);
		if (nodeid == null) {
			return 0;
		}
		return toNumber(nodeid) & 0x0000FFFFFFFFFFFFL;
	}

	public static int getClockSequence() {
		String clockseq = getProperty(PROPERTY_CLOCKSEQ);
		if (clockseq == null) {
			return 0;
		}
		return ((int) toNumber(clockseq)) & 0x00003FFF;
	}

	private static String getProperty(String name) {
		
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

	private static String getPropertyName(String key) {
		return String.join(".", PROPERTY_PREFIX, key);
	}

	private static String getEnvinronmentName(String key) {
		return String.join("_", PROPERTY_PREFIX, key).toUpperCase();
	}

	private static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	private static long toNumber(String value) {
		try {
			return Long.decode(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
