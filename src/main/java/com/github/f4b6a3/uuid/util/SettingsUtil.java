package com.github.f4b6a3.uuid.util;

public class SettingsUtil {

	protected static final String PROPERTY_PREFIX = "uuidcreator";
	public static final String PROPERTY_NODEID = "nodeid";
	public static final String PROPERTY_NODEID_SALT = "nodeid.salt";
	public static final String PROPERTY_STATE_DIRECTORY = "state.directory";
	public static final String PROPERTY_STATE_ENABLED = "state.enabled";
	
	private static final String[] trueValues = { "true", "t", "yes", "y", "on", "1" };

	public static long getNodeIdentifier() {
		String value = getProperty(PROPERTY_NODEID);
		if (value == null) {
			return 0;
		}
		return ByteUtil.toNumber(value) & 0x0000FFFFFFFFFFFFL;
	}
	
	public static void setNodeIdentifier(long nodeid) {
		String value = ByteUtil.toHexadecimal(nodeid & 0x0000FFFFFFFFFFFFL);
		setProperty(PROPERTY_NODEID, value);
	}
	
	public static String getNodeIdentifierSalt() {
		return getProperty(PROPERTY_NODEID_SALT);
	}
	
	public static void setNodeIdentifierString(String salt) {
		setProperty(PROPERTY_NODEID_SALT, salt);
	}

	public static String getStateDirectory() {
		String value = getProperty(PROPERTY_STATE_DIRECTORY);
		if (value == null) {
			return System.getProperty("java.io.tmpdir");
		}
		return value;
	}
	
	public static void setStateDirectory(String directory) {
		String value = directory.replaceAll("/$", "");
		setProperty(PROPERTY_STATE_DIRECTORY, value);
	}

	public static boolean isStateEnabled() {
		String value = getProperty(PROPERTY_STATE_ENABLED);
		if (value == null) {
			return false;
		}

		return isTrue(value);
	}
	
	public static void setStateEnabled(boolean enabled) {
		setProperty(PROPERTY_STATE_ENABLED, String.valueOf(enabled));
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
	
	private static void setProperty(String key, String value) {
		System.setProperty(getPropertyName(key), value);
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
	
	private static boolean isTrue(String value) {
		for (String t : trueValues) {
			return value.toLowerCase().equals(t);
		}
		return false;
	}
}
