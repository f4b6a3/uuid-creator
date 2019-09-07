package com.github.f4b6a3.uuid.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class FingerprintUtil {

	private static MessageDigest messageDigest = getMessageDigest();

	private FingerprintUtil() {
	}

	/**
	 * Returns a host fingerprint calculated from a list of system properties:
	 * OS + JVM + network details + system resources.
	 * 
	 * It uses these information to generate the node identifier: operating
	 * system, java virtual machine, network details and system resources. These
	 * information are concatenated and passed to a message digest. It returns
	 * the last six bytes of the resulting hash.
	 * 
	 * ### RFC-4122 - 4.5. Node IDs that Do Not Identify the Host
	 * 
	 * This section describes how to generate a version 1 UUID if an IEEE 802
	 * address is not available, or its use is not desired.
	 * 
	 * In addition, items such as the computer's name and the name of the
	 * operating system, while not strictly speaking random, will help
	 * differentiate the results from those obtained by other systems.
	 * 
	 * The exact algorithm to generate a node ID using these data is system
	 * specific, because both the data available and the functions to obtain
	 * them are often very system specific. A generic approach, however, is to
	 * accumulate as many sources as possible into a buffer, use a message
	 * digest such as MD5 [4] or SHA-1 [8], take an arbitrary 6 bytes from the
	 * hash value, and set the multicast bit as described above.
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
		String res = getResources();
		String string = String.join(" ", os, jvm, net, res);

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
	 * Returns a string of CPU cores and maximum memory available.
	 * 
	 * @return a string
	 */
	protected static String getResources() {
		int procs = Runtime.getRuntime().availableProcessors();
		long memory = Runtime.getRuntime().maxMemory();
		return String.join(" ", procs + " processors ", memory + " bytes");
	}

	/**
	 * Returns a string of the network details.
	 * 
	 * It's done in three two steps:
	 * 
	 * 1. it tries to find the network data associated with the host name;
	 * 
	 * 2. otherwise, it iterates throw all interfaces to return the first one
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
