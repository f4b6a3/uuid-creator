package com.github.f4b6a3.uuid.util;

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

}
