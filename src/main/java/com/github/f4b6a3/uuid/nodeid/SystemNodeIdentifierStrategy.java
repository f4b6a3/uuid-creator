package com.github.f4b6a3.uuid.nodeid;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Random;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.UuidUtil;

public class SystemNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier = 0;

	protected static Random random = new Xorshift128PlusRandom();

	protected static MessageDigest md = null;

	public SystemNodeIdentifierStrategy() {
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new InternalError("Message digest algorithm not supported.", e);
		}
	}

	/**
	 * Get a node identifier generated from system information.
	 * 
	 * It uses these information to generate the node identifier: hostname, IP,
	 * operating system and JVM. These information are concatenated and passed
	 * to a message digest. It returns the last six bytes of the resulting hash.
	 * 
	 * ### RFC-4122 - 4.5. Node IDs that Do Not Identify the Host
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
	 * @return
	 */
	@Override
	public long getNodeIdentifier() {

		if (this.nodeIdentifier != 0) {
			return this.nodeIdentifier;
		}

		byte[] bytes = null;
		byte[] hash = null;

		String lh = getLocalHost();
		String ha = getHardwareAddress();
		String os = getOperatingSystem();
		String vm = getJavaVirtualMachine();
		String string = String.format("%s %s %s %s", lh, ha, os, vm);

		bytes = string.getBytes();
		hash = md.digest(bytes);

		this.nodeIdentifier = ByteUtil.toNumber(hash);
		this.nodeIdentifier = UuidUtil.setMulticastNodeIdentifier(this.nodeIdentifier);
		return this.nodeIdentifier;
	}

	protected static String getLocalHost() {
		String hostname = "";
		String hostip = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			hostip = InetAddress.getLocalHost().getHostAddress();
			if ((hostname == null || hostip == null) || (hostname.isEmpty() || hostip.isEmpty())) {
				return ByteUtil.toHexadecimal(ByteUtil.toBytes(random.nextLong()));
			}
			return String.format("%s %s", hostname, hostip);
		} catch (UnknownHostException e) {
			return ByteUtil.toHexadecimal(ByteUtil.toBytes(random.nextLong()));
		}
	}

	protected static String getOperatingSystem() {
		String name = System.getProperty("os.name");
		String version = System.getProperty("os.version");
		String arch = System.getProperty("os.arch");
		return String.format("%s %s %s", name, version, arch);
	}

	protected static String getJavaVirtualMachine() {
		String vmName = System.getProperty("java.vm.name");
		String vmVersion = System.getProperty("java.vm.version");
		String rtName = System.getProperty("java.runtime.name");
		String rtVersion = System.getProperty("java.runtime.version");
		return String.format("%s %s %s %s", vmName, vmVersion, rtName, rtVersion);
	}

	protected static String getHardwareAddress() {

		try {

			Enumeration<NetworkInterface> list;
			NetworkInterface nic;
			byte[] mac;

			// Return the first real MAC that is up and running.
			list = NetworkInterface.getNetworkInterfaces();
			while (list.hasMoreElements()) {
				nic = list.nextElement();
				mac = nic.getHardwareAddress();
				if ((mac != null) && nic.isUp() && !(nic.isLoopback() || nic.isVirtual())) {
					return ByteUtil.toHexadecimal(mac);
				}
			}

			// Or return the first MAC found.
			list = NetworkInterface.getNetworkInterfaces();
			while (list.hasMoreElements()) {
				nic = list.nextElement();
				mac = nic.getHardwareAddress();
				if (mac != null) {
					return ByteUtil.toHexadecimal(mac);
				}
			}

		} catch (SocketException | NullPointerException e) {
			return ByteUtil.toHexadecimal(ByteUtil.toBytes(random.nextLong()));
		}

		return getRandomHexadecimal();
	}

	protected static String getRandomHexadecimal() {
		return ByteUtil.toHexadecimal(ByteUtil.toBytes(random.nextLong()));
	}
}
