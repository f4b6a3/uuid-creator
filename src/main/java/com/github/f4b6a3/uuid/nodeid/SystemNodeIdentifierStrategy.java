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
import java.util.Random;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.NodeIdentifierUtil;

public class SystemNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;
	protected Random random;
	protected static MessageDigest md;

	public SystemNodeIdentifierStrategy() {
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new InternalError("Message digest algorithm not supported.", e);
		}
		this.random = new Xorshift128PlusRandom();
		this.nodeIdentifier = getSystemNodeIdentifier();
	}

	/**
	 * Get a node identifier generated from system information.
	 * 
	 * It uses these information to generate the node identifier: hostname, IP,
	 * MAC, operating system and JVM. These information are concatenated and
	 * passed to a message digest. It returns the last six bytes of the
	 * resulting hash.
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
	 * @return a node identifier
	 */
	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}

	/**
	 * 
	 * {@link SystemNodeIdentifierStrategy#getNodeIdentifier()}
	 * 
	 * @return a system id number
	 */
	protected long getSystemNodeIdentifier() {

		byte[] bytes = null;
		byte[] hash = null;

		String hn = getHostName();
		String ha = getNetworkInterface();
		String os = getOperatingSystem();
		String vm = getJavaVirtualMachine();
		String string = String.format("%s %s %s %s", hn, ha, os, vm);

		bytes = string.getBytes();
		hash = md.digest(bytes);

		return NodeIdentifierUtil.setMulticastNodeIdentifier(ByteUtil.toNumber(hash));
	}

	protected String getHostName() {
		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			if ((hostname == null) || (hostname.isEmpty())) {
				return getRandomHexadecimal();
			}
			return hostname;
		} catch (UnknownHostException e) {
			return getRandomHexadecimal();
		}
	}

	protected String getOperatingSystem() {
		String name = System.getProperty("os.name");
		String version = System.getProperty("os.version");
		String arch = System.getProperty("os.arch");
		return String.format("%s %s %s", name, version, arch);
	}

	protected String getJavaVirtualMachine() {
		String vmName = System.getProperty("java.vm.name");
		String vmVersion = System.getProperty("java.vm.version");
		String rtName = System.getProperty("java.runtime.name");
		String rtVersion = System.getProperty("java.runtime.version");
		return String.format("%s %s %s %s", vmName, vmVersion, rtName, rtVersion);
	}

	protected String getNetworkInterface() {

		try {
			byte[] mac;
			NetworkInterface nic;
			List<InterfaceAddress> list;
			Enumeration<NetworkInterface> enm;

			String hw;
			String ip;

			enm = NetworkInterface.getNetworkInterfaces();
			while (enm.hasMoreElements()) {
				nic = enm.nextElement();
				list = nic.getInterfaceAddresses();
				mac = nic.getHardwareAddress();
				if ((mac != null) && (!list.isEmpty()) && !(nic.isLoopback() || nic.isVirtual())) {
					hw = ByteUtil.toHexadecimal(mac);
					ip = list.get(0).getAddress().getHostAddress();
					return String.format("%s %s", hw, ip);
				}
			}

		} catch (SocketException | NullPointerException e) {
			return getRandomHexadecimal();
		}

		return getRandomHexadecimal();
	}

	protected String getRandomHexadecimal() {
		return ByteUtil.toHexadecimal(random.nextLong());
	}
}
