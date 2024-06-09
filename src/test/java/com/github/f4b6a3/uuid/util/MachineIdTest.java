package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.UUID;

public class MachineIdTest {

	// echo -n "hostname 11-11-11-11-11-11 222.222.222.222" | sha256sum
	// 1fe0fc1ba619e998770438b4568fb652e84bf20939f0b08c73843ba98ebddde9
	private static final String string = "hostname 11-11-11-11-11-11 222.222.222.222";
	private static final byte[] hash = MachineId.getMachineHash(string);
	private static final long id = 0x1fe0fc1ba619e998L;
	private static final UUID uuid = UUID.fromString("1fe0fc1b-a619-4998-b704-38b4568fb652");
	private static final String hexa = "1fe0fc1ba619e998770438b4568fb652e84bf20939f0b08c73843ba98ebddde9";

	@Test
	public void testGetMachineId() {

		long machineId1 = MachineId.getMachineId();
		long machineId2 = MachineId.getMachineId();
		assertEquals("Machine ID should be consistent", machineId1, machineId2);

		machineId1 = MachineId.getMachineId(hash);
		machineId2 = MachineId.getMachineId(hash);
		assertEquals(id, MachineId.getMachineId(hash));
		assertEquals("Machine ID should be consistent", machineId1, machineId2);
	}

	@Test
	public void testGetMachineUuid() {

		UUID machineUuid1 = MachineId.getMachineUuid();
		UUID machineUuid2 = MachineId.getMachineUuid();
		assertEquals("Machine UUID should be consistent", machineUuid1, machineUuid2);
		assertEquals("Version should be 4 (random)", 4, machineUuid1.version());

		machineUuid1 = MachineId.getMachineUuid(hash);
		machineUuid2 = MachineId.getMachineUuid(hash);
		assertEquals(uuid, MachineId.getMachineUuid(hash));
		assertEquals("Machine UUID should be consistent", machineUuid1, machineUuid2);
		assertEquals("Version should be 4 (random)", 4, machineUuid1.version());
	}

	@Test
	public void testGetMachineHexa() {

		String machineHexa1 = MachineId.getMachineHexa();
		String machineHexa2 = MachineId.getMachineHexa();
		assertEquals("Machine hexadecimal should be consistent", machineHexa1, machineHexa2);
		assertEquals("Machine hexadecimal length should be 64", 64, machineHexa1.length());

		machineHexa1 = MachineId.getMachineHexa(hash);
		machineHexa2 = MachineId.getMachineHexa(hash);
		assertEquals(hexa, MachineId.getMachineHexa(hash));
		assertEquals("Machine hexadecimal should be consistent", machineHexa1, machineHexa2);
		assertEquals("Machine hexadecimal length should be 64", 64, machineHexa1.length());
	}

	@Test
	public void testGetMachineHash() {

		byte[] machineHash1 = MachineId.getMachineHash();
		byte[] machineHash2 = MachineId.getMachineHash();
		assertArrayEquals("Machine hash should be consistent", machineHash1, machineHash2);
		assertEquals("Machine hash length should be 32 bytes", 32, machineHash1.length);

		machineHash1 = MachineId.getMachineHash(string);
		machineHash2 = MachineId.getMachineHash(string);
		assertArrayEquals(hash, MachineId.getMachineHash(string));
		assertArrayEquals("Machine hash should be consistent", machineHash1, machineHash2);
		assertEquals("Machine hash length should be 32 bytes", 32, machineHash1.length);
	}
}
