package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.UUID;

public class MachineIdTest {

	@Test
	public void testGetMachineId() {
		long machineId1 = MachineId.getMachineId();
		long machineId2 = MachineId.getMachineId();
		assertEquals("Machine ID should be consistent", machineId1, machineId2);
	}

	@Test
	public void testGetMachineUuid() {
		UUID machineUuid1 = MachineId.getMachineUuid();
		UUID machineUuid2 = MachineId.getMachineUuid();
		assertEquals("Machine UUID should be consistent", machineUuid1, machineUuid2);
		assertEquals("Version should be 4 (random)", 4, machineUuid1.version());
	}

	@Test
	public void testGetMachineHexa() {
		String machineHexa1 = MachineId.getMachineHexa();
		String machineHexa2 = MachineId.getMachineHexa();
		assertEquals("Machine hexadecimal should be consistent", machineHexa1, machineHexa2);
		assertEquals("Machine hexadecimal length should be 64", 64, machineHexa1.length());
	}

	@Test
	public void testGetMachineHash() {
		byte[] machineHash1 = MachineId.getMachineHash();
		byte[] machineHash2 = MachineId.getMachineHash();
		assertArrayEquals("Machine hash should be consistent", machineHash1, machineHash2);
		assertEquals("Machine hash length should be 32 bytes", 32, machineHash1.length);
	}
}
