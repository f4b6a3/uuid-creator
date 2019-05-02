package com.github.f4b6a3.uuid.util;

public class SettingsUtil {

	public static final String PROPERTY_PREFIX = "uuidcreator";
	public static final String PROPERTY_CLOCKSEQ = "clockseq";
	public static final String PROPERTY_NODEID = "nodeid";
	public static final String PROPERTY_STATE_DIRECTORY = "state.directory";
	public static final String PROPERTY_STATE_ENABLED = "state.enabled";

	public static long getNodeIdentifier() {
		String value = getProperty(PROPERTY_NODEID);
		if (value == null) {
			return 0;
		}
		return ByteUtil.toNumber(value) & 0x0000FFFFFFFFFFFFL;
	}

	public static int getClockSequence() {
		String value = getProperty(PROPERTY_CLOCKSEQ);
		if (value == null) {
			return 0;
		}
		return ((int) ByteUtil.toNumber(value)) & 0x00003FFF;
	}

	public static String getStateDirectory() {
		String value = getProperty(PROPERTY_STATE_DIRECTORY);
		if (value == null) {
			return System.getProperty("java.io.tmpdir");
		}
		return value;
	}
	
	public static boolean isStateEnabled() {
		String value = getProperty(PROPERTY_STATE_ENABLED);
		if (value == null) {
			return false;
		}
		return value.equals("true");
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
		return String.join("_", PROPERTY_PREFIX, key).toUpperCase().replace(".", "_");
	}

	private static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}
}
