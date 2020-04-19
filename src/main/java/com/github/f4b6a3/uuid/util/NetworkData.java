/*
 * MIT License
 * 
 * Copyright (c) 2018-2020 Fabio Lima
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

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class NetworkData {

	private String hostName;
	private String hostCanonicalName;
	private String interfaceName;
	private String interfaceDisplayName;
	private String interfaceHardwareAddress;
	private List<String> interfaceAddresses;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostCanonicalName() {
		return hostCanonicalName;
	}

	public void setHostCanonicalName(String hostCanonicalName) {
		this.hostCanonicalName = hostCanonicalName;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getInterfaceDisplayName() {
		return interfaceDisplayName;
	}

	public void setInterfaceDisplayName(String interfaceDisplayName) {
		this.interfaceDisplayName = interfaceDisplayName;
	}

	public String getInterfaceHardwareAddress() {
		return interfaceHardwareAddress;
	}

	public void setInterfaceHardwareAddress(String interfaceHardwareAddress) {
		this.interfaceHardwareAddress = interfaceHardwareAddress;
	}

	public List<String> getInterfaceAddresses() {
		return interfaceAddresses;
	}

	public void setInterfaceAddresses(List<String> interfaceAddresses) {
		this.interfaceAddresses = interfaceAddresses;
	}

	@Override
	public String toString() {

		String interfaceAddressesString = null;
		if (this.interfaceAddresses != null) {
			interfaceAddressesString = String.join(" ", this.interfaceAddresses);
		}

		return String.join(" ", this.interfaceHardwareAddress, this.hostName, this.hostCanonicalName,
				this.interfaceName, this.interfaceDisplayName, interfaceAddressesString);
	}

	/**
	 * Returns a {@link NetworkData}.
	 * 
	 * This method returns the network data associated to the host name.
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
	 * Returns a list of {@link NetworkData}.
	 * 
	 * This method iterates over all the network interfaces to return those that
	 * are up and running.
	 * 
	 * NOTE: it may be VERY EXPENSIVE on Windows systems, because that OS
	 * creates a lot of virtual network interfaces.
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
			return Collections.emptyList();
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
}
