package edu.kit.kastel.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a subnet in the network.
 * A subnet consists of Computers and a router.
 * Computers can only connect to systems in the same subnet.
 * Systems in different subnets can only connect via routers.
 * @author utsur
 */
public class Subnet {
    private static final int BITS_IN_BYTE = 8;
    private static final int BYTES_IN_IP = 4;
    private static final int IP_PARTS = 4;
    private static final String IP_DELIMITER = "\\.";
    private static final String CIDR_DELIMITER = "/";
    private static final String IP_DOT = ".";
    private final String cidr;
    private final Set<Systems> systems;
    private Router router;

    /**
     * This constructor creates a new Subnet object with the given CIDR.
     * @param cidr The CIDR of the subnet.
     */
    public Subnet(String cidr) {
        this.cidr = cidr;
        this.systems = new HashSet<>();
    }

    /**
     * This method adds a system to the subnet.
     * @param system The system to add.
     */
    public void addSystem(Systems system) {
        systems.add(system);
        if (system instanceof Router) {
            this.router = (Router) system;
        }
    }

    /**
     * This methode gets the CIDR of the subnet.
     * @return the CIDR of the subnet.
     */
    public String getCidr() {
        return cidr;
    }

    /**
     * This method returns the first IP address of the subnet.
     * @return the first IP address of the subnet.
     */
    public String getFirstIp() {
        String[] parts = this.cidr.split("/");
        return parts[0]; // The network address is the first IP address.
    }

    /**
     * This method returns the first IP address of the subnet as a long.
     * @return the first IP address of the subnet as a long.
     */
    public long getFirstIpAsLong() {
        return ipToLong(getFirstIp());
    }


    /**
     * This method returns the last IP address of the subnet.
     * @return the last IP address of the subnet.
     */
    public String getLastIp() {
        String[] parts = cidr.split(CIDR_DELIMITER);
        String ip = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        String[] octets = ip.split(IP_DELIMITER);
        int[] ipInts = new int[BYTES_IN_IP];
        for (int i = 0; i < BYTES_IN_IP; i++) {
            ipInts[i] = Integer.parseInt(octets[i]);
        }

        int hostBits = BYTES_IN_IP * BITS_IN_BYTE - prefixLength;
        for (int i = BYTES_IN_IP - 1; i >= 0; i--) {
            if (hostBits >= BITS_IN_BYTE) {
                ipInts[i] = 255;
                hostBits -= BITS_IN_BYTE;
            } else if (hostBits > 0) {
                ipInts[i] |= (1 << hostBits) - 1;
                break;
            } else {
                break;
            }
        }

        return ipInts[0] + IP_DOT + ipInts[1] + IP_DOT + ipInts[2] + IP_DOT + ipInts[3];
    }

    /**
     * This method returns the last IP address of the subnet as a long.
     * @return the last IP address of the subnet as a long.
     */
    public long getLastIpAsLong() {
        return ipToLong(getLastIp());
    }

    /**
     * This method checks if the given IP address is in the subnet.
     * @param ip The IP address to check.
     * @return true if the IP address is in the subnet, false otherwise.
     */
    public boolean isIpInSubnet(String ip) {
        String[] cidrParts = cidr.split(CIDR_DELIMITER);
        String networkAddress = cidrParts[0];
        int prefixLength = Integer.parseInt(cidrParts[1]);

        long networkIp = ipToLong(networkAddress);
        long inputIp = ipToLong(ip);

        long mask = 0xffffffffL << (32 - prefixLength);

        return (networkIp & mask) == (inputIp & mask);
    }

    private long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= Long.parseLong(octets[i]);
        }
        return result;
    }

    /**
     * This method removes a system from the subnet.
     * @param system The system to remove.
     */
    public void removeSystem(Systems system) {
        systems.remove(system);
    }

    /**
     * This method returns all the systems in the subnet.
     * @return a set of systems in the subnet.
     */
    public Set<Systems> getSystems() {
        return new HashSet<>(systems);
    }

    /**
     * This method gets the router of the subnet.
     * @return the router of the subnet.
     */
    public Router getRouter() {
        return router;
    }
}
