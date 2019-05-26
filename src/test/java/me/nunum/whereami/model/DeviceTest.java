package me.nunum.whereami.model;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeviceTest extends TestCase {


    @Test
    public void testInstanceId() {
        final Device device1 = new Device("instance1");
        final Device device2 = new Device("instance2");
        assertEquals(device1, device1);
        assertNotSame(device1, device2);
    }

    @Test
    public void testCompareTo() throws InterruptedException {
        final Device device1 = new Device("instance1");
        Thread.sleep(5);
        final Device device2 = new Device("instance2");
        assertTrue(device1.compareTo(device2) < 0);
    }

    @Test
    public void testIsInRole() {
        final Device device1 = new Device("instance1");
        final Device device2 = new Device("instance2", Stream.of("provider").map(Role::new).collect(Collectors.toList()));
        assertFalse(device1.isInRole(""));
        assertFalse(device1.isInRole("Admin"));
        assertTrue(device2.isInRole("proviDer"));
    }
}