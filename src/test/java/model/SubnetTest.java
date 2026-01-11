package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SubnetTest {

    @Test
    public void testIsIpInSubnet() {
        Subnet subnet = new Subnet("192.168.1.0/24");
        assertTrue(subnet.isIpInSubnet("192.168.1.1"));
        assertTrue(subnet.isIpInSubnet("192.168.1.254"));
        assertFalse(subnet.isIpInSubnet("192.168.2.1"));
    }

    @Test
    public void testGetFirstAndLastIp() {
        Subnet subnet = new Subnet("192.168.1.0/24");
        assertEquals("192.168.1.0", subnet.getFirstIp());
        assertEquals("192.168.1.255", subnet.getLastIp());
    }

    @Test
    public void testSmallSubnet() {
        Subnet subnet = new Subnet("10.0.0.0/30");
        assertEquals("10.0.0.0", subnet.getFirstIp());
        assertEquals("10.0.0.3", subnet.getLastIp());
        assertTrue(subnet.isIpInSubnet("10.0.0.1"));
        assertTrue(subnet.isIpInSubnet("10.0.0.2"));
        assertFalse(subnet.isIpInSubnet("10.0.0.4"));
    }
}
