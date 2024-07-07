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
