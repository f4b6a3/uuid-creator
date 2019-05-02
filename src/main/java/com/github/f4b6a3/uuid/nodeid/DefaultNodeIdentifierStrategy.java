package com.github.f4b6a3.uuid.nodeid;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;

import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.NodeIdentifierUtil;
import com.github.f4b6a3.uuid.util.RandomUtil;
import com.github.f4b6a3.uuid.util.SettingsUtil;

public class DefaultNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;
	protected static MessageDigest md;

	public DefaultNodeIdentifierStrategy() {
		
		long preferedNodeIdentifier = SettingsUtil.getNodeIdentifier();
		if (preferedNodeIdentifier != 0) {
			this.nodeIdentifier = preferedNodeIdentifier;
		} else {
			this.nodeIdentifier = getSystemNodeIdentifier();
		}
	}

	/**
	 * Get a node identifier generated from system information.
	 * 
	 * It uses these information to generate the node identifier: host name, IP,
	 * MAC, operating system and JVM. These information are concatenated and
	 * passed to a message digest. It returns the last six bytes of the
	 * resulting hash.
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
	 * @return a node identifier
	 */
	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}

	/**
	 * 
	 * {@link DefaultNodeIdentifierStrategy#getNodeIdentifier()}
	 * 
	 * @return a system id number
	 */
	protected long getSystemNodeIdentifier() {

		md = getMessageDigest();

		byte[] bytes = null;
		byte[] hash = null;
		long number = 0;

		String hn = getHostName();
		String ni = getNetworkInterfaces();
		String os = getOperatingSystem();
		String vm = getJavaVirtualMachine();
		String string = String.join(" ", hn, ni, os, vm);

		bytes = string.getBytes();
		hash = md.digest(bytes);
		number = ByteUtil.toNumber(hash) & 0x0000FFFFFFFFFFFFL;

		return NodeIdentifierUtil.setMulticastNodeIdentifier(number);
	}

	public static String getHostName() {
		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			String canonical = InetAddress.getLocalHost().getCanonicalHostName();
			return String.join(" ", hostname, canonical);
		} catch (UnknownHostException | NullPointerException e) {
			return RandomUtil.nextLongHexadecimal();
		}
	}

	public static String getNetworkInterfaces() {

		try {

			byte[] mac;

			NetworkInterface nic;
			List<InterfaceAddress> addrList;
			Enumeration<NetworkInterface> enumeration;

			TreeSet<String> sortedSet = new TreeSet<>();
			StringBuffer buffer = new StringBuffer();

			enumeration = NetworkInterface.getNetworkInterfaces();
			while (enumeration.hasMoreElements()) {

				nic = enumeration.nextElement();
				mac = nic.getHardwareAddress();
				addrList = nic.getInterfaceAddresses();

				if ((mac != null) && !addrList.isEmpty() && !(nic.isLoopback() || nic.isVirtual())) {

					String hwAddr = ByteUtil.toHexadecimal(mac);
					String name = nic.getName();
					String dispName = nic.getDisplayName();
					String ipAddr = null;

					for (InterfaceAddress addr : addrList) {
						if (ipAddr == null) {
							ipAddr = addr.getAddress().getHostAddress();
						} else {
							ipAddr += " " + addr.getAddress().getHostAddress();
						}
					}

					sortedSet.add(String.join(" ", hwAddr, name, dispName, ipAddr));
				}
			}

			for (Object item : sortedSet.toArray()) {
				buffer.append(" " + (String) item);
			}

			return buffer.toString().trim();

		} catch (SocketException | NullPointerException e) {
			return RandomUtil.nextLongHexadecimal();
		}
	}

	public static String getOperatingSystem() {
		String name = System.getProperty("os.name");
		String version = System.getProperty("os.version");
		String arch = System.getProperty("os.arch");
		return String.join(" ", name, version, arch);
	}

	public static String getJavaVirtualMachine() {
		String vendor = System.getProperty("java.vendor");
		String version = System.getProperty("java.version");
		String home = System.getProperty("java.home");
		String sName = System.getProperty("java.specification.name");
		String sVersion = System.getProperty("java.specification.version");
		String vmName = System.getProperty("java.vm.name");
		String vmVersion = System.getProperty("java.vm.version");
		String rtName = System.getProperty("java.runtime.name");
		String rtVersion = System.getProperty("java.runtime.version");
		return String.join(" ", vendor, version, home, sName, sVersion, vmName, vmVersion, rtName, rtVersion);
	}

	private MessageDigest getMessageDigest() {
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
