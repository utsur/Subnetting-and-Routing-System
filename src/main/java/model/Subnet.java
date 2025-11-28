package main.java.model;

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
    private static final int BITS_IN_IP = BYTES_IN_IP * BITS_IN_BYTE;
    private static final String IP_DELIMITER = "\\.";
    private static final String CIDR_DELIMITER = "/";
    private static final String IP_DOT = ".";
    private static final int LAST_OCTET_INDEX = 3;
    private static final int MAX_OCTET_VALUE = 255;
    private static final long ALL_BITS_SET = 0xffffffffL;
    private final String cidr;
    private final Set<SystemNode> systems;
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
    public void addSystem(SystemNode system) {
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
        String[] parts = this.cidr.split(CIDR_DELIMITER);
        return parts[0];  // This is the network address
    }

    /**
     * This method returns the first usable host IP address of the subnet.
     * @return the first usable host IP address of the subnet.
     */
    public String getFirstUsableIp() {
        String[] parts = this.cidr.split(CIDR_DELIMITER);
        String networkAddress = parts[0];
        String[] octets = networkAddress.split(IP_DELIMITER);
        // Increment the last octet by 1 to get the first usable host IP
        int lastOctet = Integer.parseInt(octets[LAST_OCTET_INDEX]);
        octets[LAST_OCTET_INDEX] = String.valueOf(lastOctet + 1);

        return String.join(IP_DOT, octets);
    }

    /**
     * This method returns the first IP address of the subnet as type long.
     * @return the first IP address of the subnet as type long.
     */
    public long getFirstIpAsLong() {
        return ipToLong(getFirstIp());
    }

    /**
     * Calculates the last IP address in this subnet and returns it.
     * This method takes into account the network prefix length to determine the last usable IP address in the subnet range.
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
                ipInts[i] = MAX_OCTET_VALUE;
                hostBits -= BITS_IN_BYTE;
            } else if (hostBits > 0) {
                ipInts[i] |= (1 << hostBits) - 1;
                break;
            } else {
                break;
            }
        }

        return ipInts[0] + IP_DOT + ipInts[1] + IP_DOT + ipInts[2] + IP_DOT + ipInts[LAST_OCTET_INDEX];
    }

    /**
     * This method returns the last IP address of the subnet as type long.
     * @return the last IP address of the subnet as type long.
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

        long mask = ALL_BITS_SET << (BITS_IN_IP - prefixLength);

        return (networkIp & mask) == (inputIp & mask);
    }

    private long ipToLong(String ip) {
        String[] octets = ip.split(IP_DELIMITER); // Split the IP address into octets.
        long result = 0;
        for (int i = 0; i < BYTES_IN_IP; i++) {
            result <<= BITS_IN_BYTE;
            result |= Long.parseLong(octets[i]);
        }
        return result;
    }

    /**
     * This method removes a system from the subnet.
     * @param system The system to remove.
     */
    public void removeSystem(SystemNode system) {
        systems.remove(system);
    }

    /**
     * This method returns all the systems in the subnet.
     * @return a set of systems in the subnet.
     */
    public Set<SystemNode> getSystems() {
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
