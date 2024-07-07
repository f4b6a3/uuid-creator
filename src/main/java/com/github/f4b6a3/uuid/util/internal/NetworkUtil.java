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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Utility class that returns host name, MAC and IP.
 */
public final class NetworkUtil {

	private static String hostname;
	private static String mac;
	private static String ip;

	private NetworkUtil() {
	}

	/**
	 * Returns the host name if found.
	 * <p>
	 * Sequence of HOSTNAME search:
	 * <ol>
	 * <li>Try to find the HOSTNAME variable on LINUX environment;
	 * <li>Try to find the COMPUTERNAME variable on WINDOWS environment;
	 * <li>Try to find the host name by calling
	 * {@code InetAddress.getLocalHost().getHostName()} (the expensive way);
	 * <li>If no host name is found, return {@code null}.
	 * </ol>
	 * 
	 * @return a string containing the host name
	 */
	public static synchronized String hostname() {

		if (hostname != null) {
			return hostname;
		}

		// try to find HOSTNAME on LINUX
		hostname = System.getenv("HOSTNAME");
		if (hostname != null && !hostname.isEmpty()) {
			return hostname;
		}

		// try to find COMPUTERNAME on WINDOWS
		hostname = System.getenv("COMPUTERNAME");
		if (hostname != null && !hostname.isEmpty()) {
			return hostname;
		}

		try {
			// try to find HOSTNAME for the local host
			hostname = InetAddress.getLocalHost().getHostName();
			if (hostname != null && !hostname.isEmpty()) {
				return hostname;
			}
		} catch (UnknownHostException e) {
			return null;
		}

		// not found
		return null;
	}

	/**
	 * Returns the MAC address if found.
	 * <p>
	 * Output format: "00-00-00-00-00-00" (in upper case)
	 * 
	 * @param nic a network interface
	 * @return a string containing the MAC address
	 */
	public static synchronized String mac(NetworkInterface nic) {

		if (mac != null) {
			return mac;
		}

		try {
			if (nic != null && nic.getHardwareAddress() != null) {
				byte[] ha = nic.getHardwareAddress();
				String[] hex = new String[ha.length];
				for (int i = 0; i < ha.length; i++) {
					hex[i] = String.format("%02X", ha[i]);
				}
				mac = String.join("-", hex);
				return mac;
			}
		} catch (SocketException e) {
			return null;
		}

		// not found
		return null;
	}

	/**
	 * Returns the IP address if found.
	 * <p>
	 * Output format: "0.0.0.0" (if IPv4)
	 * 
	 * @param nic a network interface
	 * @return a string containing the IP address
	 */
	public static synchronized String ip(NetworkInterface nic) {

		if (ip != null) {
			return ip;
		}

		if (nic != null) {
			Enumeration<InetAddress> ips = nic.getInetAddresses();
			if (ips.hasMoreElements()) {
				ip = ips.nextElement().getHostAddress();
				return ip;
			}
		}

		// not found
		return null;
	}

	/**
	 * Returns a string containing host name, MAC and IP.
	 * <p>
	 * Output format: "hostname 11-11-11-11-11-11 222.222.222.222"
	 * 
	 * @return a string containing the host name, MAC and IP
	 */
	public static synchronized String getMachineString() {

		NetworkInterface nic = nic();

		String hostname = NetworkUtil.hostname();
		String mac = NetworkUtil.mac(nic);
		String ip = NetworkUtil.ip(nic);

		return String.join(" ", hostname, mac, ip);
	}

	/**
	 * Returns a network interface.
	 * <p>
	 * It tries to return the network interface associated to the host name.
	 * <p>
	 * If that network interface is not found, it tries to return the first network
	 * interface that satisfies these conditions:
	 * <ul>
	 * <li>it is up and running;
	 * <li>it is not loopback;
	 * <li>it is not virtual;
	 * <li>it has a hardware address.
	 * </ul>
	 * <p>
	 * If no acceptable network interface is found, it returns null.
	 * 
	 * @return a network interface.
	 */
	public static synchronized NetworkInterface nic() {

		try {

			InetAddress ip = null;
			NetworkInterface nic = null;
			Enumeration<NetworkInterface> enu = null;

			// try to find the network interface for the host name
			ip = InetAddress.getByName(hostname());
			nic = NetworkInterface.getByInetAddress(ip);
			if (acceptable(nic)) {
				return nic;
			}

			// try to find the first network interface
			enu = NetworkInterface.getNetworkInterfaces();
			while (enu.hasMoreElements()) {
				nic = enu.nextElement();
				if (acceptable(nic)) {
					return nic;
				}
			}

		} catch (UnknownHostException | SocketException e) {
			return null;
		}

		// NIC not found
		return null;
	}

	/**
	 * Checks if the network interface is acceptable.
	 * 
	 * @param nic a network interface
	 * @return true if acceptable
	 */
	private static synchronized boolean acceptable(NetworkInterface nic) {
		try {
			if (nic != null && nic.isUp() && !nic.isLoopback() && !nic.isVirtual()) {
				byte[] mac = nic.getHardwareAddress();
				if (mac != null && mac.length == 6) {
					return true;
				}
			}
		} catch (SocketException e) {
			return false;
		}

		return false;
	}
}