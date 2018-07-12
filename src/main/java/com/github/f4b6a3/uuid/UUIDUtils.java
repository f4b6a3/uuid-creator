package com.github.f4b6a3.uuid;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

public class UUIDUtils {
	
	private Random random;
	
	public UUIDUtils() {
		this.random = new Random();
	}
	
	public static long setMulticastNode(long node) {
		return node | 0x0000010000000000L;
	}

	public static boolean isMulticastNode(long node) {
		return ((node & 0x0000010000000000L) >>> 40) == 1;
	}
	
	public long getRandomNode(UUIDState state) {
		
		if(state.getNode() > 0 && isMulticastNode(state.getNode())) {
			return state.getNode();
		}
		
		long node = random.nextLong();
		return setMulticastNode(node);
	}
	
	public long getRealNode(UUIDState state) {
		
		if(state.getNode() > 0 && !isMulticastNode(state.getNode())) {
			return state.getNode();
		}
		
		try {
			NetworkInterface nic = NetworkInterface.getNetworkInterfaces().nextElement();
			byte[] realHardwareAddress = nic.getHardwareAddress();
			if (realHardwareAddress != null) {
				return toNumber(realHardwareAddress);
			}
		} catch (SocketException | NullPointerException e) {
			// If exception occurs, return a random hardware address.
		}

		return getRandomNode(state);
	}
	
	// TEMPORARY METHOD
	private long toNumber(byte[] bytes) {
		long result = 0;
		for (int i = 0; i < bytes.length; i++) {
			result = (result << 8) | (bytes[i] & 0xff);
		}
		return result;
	}
}
