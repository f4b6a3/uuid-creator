package com.github.f4b6a3.uuid.util;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class SystemDataUtil {

	protected static MessageDigest md;

	/**
	 * Returns a system hash ID generated from all the system details
	 * concatenated: OS + JVM + Network + SALT.
	 * 
	 * Get a system identifier generated from system information.
	 * 
	 * It uses these information to generate the node identifier: operating
	 * system, java virtual machine and network details. These information are
	 * concatenated and passed to a message digest. It returns the last six
	 * bytes of the resulting hash.
	 * 
	 * ### RFC-4122 - 4.5. Node IDs that Do Not Identify the Host
	 * 
	 * (1) This section describes how to generate a version 1 UUID if an IEEE
	 * 802 address is not available, or its use is not desired.
	 * 
	 * (4) In addition, items such as the computer's name and the name of the
	 * operating system, while not strictly speaking random, will help
	 * differentiate the results from those obtained by other systems.
	 * 
	 * (5) The exact algorithm to generate a node ID using these data is system
	 * specific, because both the data available and the functions to obtain
	 * them are often very system specific. A generic approach, however, is to
	 * accumulate as many sources as possible into a buffer, use a message
	 * digest such as MD5 [4] or SHA-1 [8], take an arbitrary 6 bytes from the
	 * hash value, and set the multicast bit as described above.
	 * 
	 * @param salt
	 *            an arbitrary string to change the final hash
	 * @return a system identifier
	 */
	public static long getSystemId(String salt) {
		String hash = getSystemDataHash(salt);
		long number = ByteUtil.toNumber(hash) & 0x0000FFFFFFFFFFFFL;
		return NodeIdentifierUtil.setMulticastNodeIdentifier(number);
	}

	/**
	 * Returns a hash string generated from all the system data concatenated:
	 * OS + JVM + Network + Resources + SALT.
	 * 
	 * @param salt
	 *            an arbitrary string to change the final hash
	 * @return a string
	 */
	public static String getSystemDataHash(String salt) {

		md = getMessageDigest();

		String os = getOperatingSystem();
		String jvm = getJavaVirtualMachine();
		String net = getNetwork();
		String res =  getResources();
		String string = String.join(" ", os, jvm, net, res, salt);

		byte[] bytes = string.getBytes();
		byte[] hash = md.digest(bytes);

		return ByteUtil.toHexadecimal(hash);
	}

	/**
	 * Returns a string of the OS details.
	 * 
	 * @return a string
	 */
	public static String getOperatingSystem() {
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
	public static String getJavaVirtualMachine() {
		String vendor = System.getProperty("java.vendor");
		String version = System.getProperty("java.version");
		String rtName = System.getProperty("java.runtime.name");
		String rtVersion = System.getProperty("java.runtime.version");
		String vmName = System.getProperty("java.vm.name");
		String vmVersion = System.getProperty("java.vm.version");
		return String.join(" ", vendor, version, rtName, rtVersion, vmName, vmVersion);
	}

	public static String getResources() {
		int procs = getAvailableProcessors();
		long memory = getMaxMemory();
		return String.join(" ", procs + " processors ", memory + " bytes");
	}
	
	public static int getAvailableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}
	
	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}
	
	/**
	 * Returns a string of the network details.
	 * 
	 * @return a string
	 */
	public static String getNetwork() {

		NetworkData networkData = getNetworkData();

		if (networkData == null) {
			List<NetworkData> networkDataList = getNetworkDataList();
			if (networkDataList != null && !networkDataList.isEmpty()) {
				networkData = networkDataList.get(0);
			}
		}

		if (networkData == null) {
			return null;
		}
		
		return networkData.toString();
	}

	/**
	 * Returns a {@link NetworkData}
	 * 
	 * @return a {@link NetworkData}
	 */
	public static NetworkData getNetworkData() {

		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
			return buildNetworkData(networkInterface, inetAddress);
		} catch (UnknownHostException | SocketException e) {
			return null;
		}
	}

	/**
	 * Returns a list of {@link NetworkData}
	 * 
	 * This method iterates over all the network interfaces. It may be VERY
	 * EXPENSIVE on Windows systems, because it creates a lot of virtual network
	 * interfaces.
	 * 
	 * @return a list of {@link NetworkData}
	 */
	public static List<NetworkData> getNetworkDataList() {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

			HashSet<NetworkData> networkDataHashSet = new HashSet<>();
			for (NetworkInterface networkInterface : networkInterfaces) {
				NetworkData networkData = buildNetworkData(networkInterface, inetAddress);
				if (networkData != null) {
					networkDataHashSet.add(networkData);
				}
			}
			return new ArrayList<>(networkDataHashSet);
		} catch (SocketException | NullPointerException | UnknownHostException e) {
			return null;
		}
	}

	private static NetworkData buildNetworkData(NetworkInterface networkInterface, InetAddress inetAddress)
			throws SocketException {
		if (isPhysicalNetworkInterface(networkInterface)) {

			String hostName = inetAddress != null ? inetAddress.getHostName() : null;
			String hostCanonicalName = inetAddress != null ? inetAddress.getCanonicalHostName() : null;
			String interfaceName = networkInterface.getName();
			String interfaceDisplayName = networkInterface.getDisplayName();
			String interfaceHardwareAddress = ByteUtil.toHexadecimal(networkInterface.getHardwareAddress());
			List<String> interfaceAddresses = getInterfaceAddresses(networkInterface);

			NetworkData networkData = new NetworkData();
			networkData.setHostName(hostName);
			networkData.setHostCanonicalName(hostCanonicalName);
			networkData.setInterfaceName(interfaceName);
			networkData.setInterfaceDisplayName(interfaceDisplayName);
			networkData.setInterfaceHardwareAddress(interfaceHardwareAddress);
			networkData.setInterfaceAddresses(interfaceAddresses);

			return networkData;
		}
		return null;
	}

	private static boolean isPhysicalNetworkInterface(NetworkInterface networkInterface) {
		try {
			return networkInterface != null && networkInterface.isUp()
					&& !(networkInterface.isLoopback() || networkInterface.isVirtual());
		} catch (SocketException e) {
			return false;
		}
	}

	private static List<String> getInterfaceAddresses(NetworkInterface networkInterface) {
		HashSet<String> addresses = new HashSet<>();
		List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
		if (interfaceAddresses != null && !interfaceAddresses.isEmpty()) {
			for (InterfaceAddress addr : interfaceAddresses) {
				if (addr.getAddress() != null) {
					addresses.add(addr.getAddress().getHostAddress());
				}
			}
		}
		return new ArrayList<>(addresses);
	}

	private static MessageDigest getMessageDigest() {
		if (md == null) {
			try {
				return MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				throw new InternalError("Message digest algorithm not supported.", e);
			}
		}
		return md;
	}
}
