/*
 * MIT License
 * 
 * Copyright (c) 2018-2022 Fabio Lima
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
 * Utility that returns HOSTNAME, MAC and IP.
 */
public final class NetworkUtil {

	private static String hostname;
	private static String mac;
	private static String ip;

	private NetworkUtil() {
	}

	/**
	 * Returns the host name if found.
	 * 
	 * Sequence of HOSTNAME search:
	 * 
	 * 1. Try to find the HOSTNAME variable in LINUX environment;
	 * 
	 * 2. Try to find the COMPUTERNAME variable in WINDOWS environment;
	 * 
	 * 3. Try to find the host name by calling {code
	 * InetAddress.getLocalHost().getHostName()} (the hard way);
	 * 
	 * @return a string
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
		} catch (UnknownHostException | NullPointerException e) {
			// do nothing
		}

		// not found
		return null;
	}

	/**
	 * Returns the MAC address if found.
	 * 
	 * Output format: "00-00-00-00-00-00" (in upper case)
	 * 
	 * @return a string
	 */
	public static synchronized String mac(NetworkInterface nic) {

		if (mac != null) {
			return mac;
		}

		try {
			if (nic != null) {
				byte[] ha = nic.getHardwareAddress();
				String[] hex = new String[ha.length];
				for (int i = 0; i < ha.length; i++) {
					hex[i] = String.format("%02X", ha[i]);
				}
				mac = String.join("-", hex);
				return mac;
			}
		} catch (SocketException | NullPointerException e) {
			// do nothing
		}

		// not found
		return null;
	}

	/**
	 * Returns the IP address if found.
	 * 
	 * Output format: "0.0.0.0" (if IPv4)
	 * 
	 * @return a string
	 */
	public static synchronized String ip(NetworkInterface nic) {

		if (ip != null) {
			return ip;
		}

		try {
			if (nic != null) {
				Enumeration<InetAddress> ips = nic.getInetAddresses();
				if (ips.hasMoreElements()) {
					ip = ips.nextElement().getHostAddress();
					return ip;
				}
			}
		} catch (NullPointerException e) {
			// do nothing
		}

		// not found
		return null;
	}

	/**
	 * Returns a string containing host name, MAC and IP.
	 * 
	 * Output format: "hostname123 11-22-33-44-55-66 123.123.123.123"
	 * 
	 * @return a string
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
	 * 
	 * It tries to return the network interface associated to the host name.
	 * 
	 * If that network interface is not found, it tries to return the first network
	 * interface that satisfies these conditions:
	 * 
	 * - it is up and running;
	 * 
	 * - it is not loopback;
	 * 
	 * - it is not virtual;
	 * 
	 * - it has a hardware address.
	 * 
	 * If no acceptable network interface is found, it returns null.
	 * 
	 * @return a network interface.
	 */
	public static synchronized NetworkInterface nic() {

		NetworkInterface nic;

		try {

			InetAddress ip = null;
			NetworkInterface ni = null;
			Enumeration<NetworkInterface> enu = null;

			// try to find the network interface for the host name
			ip = InetAddress.getByName(hostname());
			ni = NetworkInterface.getByInetAddress(ip);
			if (acceptable(ni)) {
				nic = ni;
				return nic;
			}

			// try to find the first network interface
			enu = NetworkInterface.getNetworkInterfaces();
			while (enu.hasMoreElements()) {
				ni = enu.nextElement();
				if (acceptable(ni)) {
					nic = ni;
					return nic;
				}
			}

		} catch (UnknownHostException | SocketException | NullPointerException e) {
			// do nothing
		}

		// NIC not found
		return null;
	}

	/**
	 * Checks if the network interface is acceptable.
	 * 
	 * @param ni a network interface
	 * @return true if acceptable
	 */
	private static synchronized boolean acceptable(NetworkInterface ni) {
		try {
			if (ni != null && ni.isUp() && !ni.isLoopback() && !ni.isVirtual()) {
				byte[] mac = ni.getHardwareAddress();
				if (mac != null && mac.length == 6) {
					return true;
				}
			}
		} catch (SocketException | NullPointerException e) {
			// do nothing
		}

		return false;
	}
}