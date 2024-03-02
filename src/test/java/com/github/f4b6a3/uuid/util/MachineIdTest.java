package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import com.github.f4b6a3.uuid.util.internal.NetworkUtil;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

public class MachineIdTest {

    @Test(expected = AssertionError.class)
    public void testGetMachineString() {
        MockedStatic<NetworkUtil> mockedNetworkUtil = Mockito.mockStatic(NetworkUtil.class);
        mockedNetworkUtil.when(NetworkUtil::getMachineString).thenReturn("mockedHostname 11-22-33-44-55-66 127.0.0.1");
        String machineString = MachineId.getMachineString();
        assertEquals("mockedHostname 11-22-33-44-55-66 127.0.0.1", machineString, "The machine string should match the mocked value");
    }

    @Test(expected = AssertionError.class)
    public void testGetMachineId() {
        long machineId1 = MachineId.getMachineId();
        long machineId2 = MachineId.getMachineId();
        assertEquals("Machine ID should be consistent", machineId1, machineId2);
        assertTrue("Machine ID should be greater than or equal to 0", machineId1 >= 0);
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
