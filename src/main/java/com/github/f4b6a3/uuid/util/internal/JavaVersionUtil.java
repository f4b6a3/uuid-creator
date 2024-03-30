package com.github.f4b6a3.uuid.util.internal;

public class JavaVersionUtil {

    /**
     * Returns the java major version number.
     *
     * @see <a href= "https://www.java.com/releases/">JDK Releases</a>
     * @return major version number
     */
    public static int getJavaVersion() {
        try {

            String property = System.getProperty("java.version");

            if (property != null) {
                String[] version = property.split("\\.");
                if (version[0].equals("1")) {
                    return Integer.parseInt(version[1]);
                } else {
                    return Integer.parseInt(version[0]);
                }
            } else {
                return 8;
            }

        } catch (NullPointerException | NumberFormatException | IndexOutOfBoundsException e) {
            return 8;
        }
    }
}
