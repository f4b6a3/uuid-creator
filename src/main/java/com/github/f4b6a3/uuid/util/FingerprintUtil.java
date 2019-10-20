/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
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

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FingerprintUtil {

	private static MessageDigest messageDigest = getMessageDigest();

	private FingerprintUtil() {
	}

	/**
	 * Returns a host fingerprint calculated from a list of system properties:
	 * OS + JVM + network details + system resources + locale + timezone.
	 * 
	 * It uses these information to generate the node identifier: operating
	 * system (name, version, arch), java virtual machine (vendor, version,
	 * runtime, VM), network settings (IP, MAC, host name, domain name), system
	 * resources (CPU cores, memory), locale (language, charset) and timezone.
	 * These information are concatenated and passed to a message digest.
	 * It returns the last six bytes of the resulting hash.
	 * 
	 * Read: https://en.wikipedia.org/wiki/Device_fingerprint
	 * 
	 * @return a system identifier
	 */
	public static long getFingerprint() {
		String hash = getSystemDataHash();
		long number = ByteUtil.toNumber(hash) & 0x0000FFFFFFFFFFFFL;
		return NodeIdentifierUtil.setMulticastNodeIdentifier(number);
	}

	/**
	 * Returns a hash string generated from all the system data: OS + JVM +
	 * network details + system resources.
	 * 
	 * @return a string
	 */
	protected static String getSystemDataHash() {

		String os = getOperatingSystem();
		String jvm = getJavaVirtualMachine();
		String net = getNetwork();
		String loc = getLocalization();
		String res = getResources();
		String string = String.join(" ", os, jvm, net, loc, res);

		byte[] bytes = string.getBytes();
		byte[] hash = messageDigest.digest(bytes);

		return ByteUtil.toHexadecimal(hash);
	}

	/**
	 * Returns a string of the OS details.
	 * 
	 * @return a string
	 */
	protected static String getOperatingSystem() {
		String name = System.getProperty("os.name");
		String version = System.getProperty("os.version");
		String arch = System.getProperty("os.arch");
		return String.join(" ", name, version, arch);
	}

	/**
	 * Returns a string of the JVM details.
	 * 
	 * @return a string
	 */
	protected static String getJavaVirtualMachine() {
		String vendor = System.getProperty("java.vendor");
		String version = System.getProperty("java.version");
		String rtName = System.getProperty("java.runtime.name");
		String rtVersion = System.getProperty("java.runtime.version");
		String vmName = System.getProperty("java.vm.name");
		String vmVersion = System.getProperty("java.vm.version");
		return String.join(" ", vendor, version, rtName, rtVersion, vmName, vmVersion);
	}

	/**
	 * Return a string with locale, charset, encoding and timezone.
	 * 
	 * @return a string
	 */
	protected static String getLocalization() {
		String locale = Locale.getDefault().toString();
		String charset = Charset.defaultCharset().toString();
		String encoding = System.getProperty("file.encoding");
		String timezone = TimeZone.getDefault().getID();
		return String.join(" ", locale, charset, encoding, timezone);
	}

	/**
	 * Returns a string of CPU cores and maximum memory available.
	 * 
	 * @return a string
	 */
	protected static String getResources() {
		int procs = Runtime.getRuntime().availableProcessors();
		long memory = Runtime.getRuntime().maxMemory();
		return String.join(" ", procs + " processors", memory + " bytes");
	}

	/**
	 * Returns a string of the network details.
	 * 
	 * It's done in three two steps:
	 * 
	 * 1. it tries to find the network data associated with the host name;
	 * 
	 * 2. otherwise, it iterates through all interfaces to return the first one
	 * that is up and running.
	 * 
	 * @return a string
	 */
	protected static String getNetwork() {

		NetworkData networkData = NetworkData.getNetworkData();

		if (networkData == null) {
			List<NetworkData> networkDataList = NetworkData.getNetworkDataList();
			if (networkDataList != null && !networkDataList.isEmpty()) {
				networkData = networkDataList.get(0);
			}
		}

		if (networkData == null) {
			return null;
		}

		return networkData.toString();
	}

	private static MessageDigest getMessageDigest() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new InternalError("Message digest algorithm not supported.", e);
		}
	}
}
