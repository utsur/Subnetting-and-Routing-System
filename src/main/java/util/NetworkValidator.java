package main.java.util;

import main.java.model.Router;
import main.java.model.Subnet;
import main.java.model.SystemNode;

/**
 * This class is responsible for validating the network.
 * It checks if the IP, subnet, and connection are valid.
 * It also checks if the subnets are overlapping.
 * It is used by the NetworkLoader class to validate the network before creating it.
 * @author utsur
 */
public final class NetworkValidator {
    private static final String ERROR_UNWEIGHTED_CONNECTION = "Error, Connection inside subnet must be weighted: ";
    private static final String ERROR_WEIGHTED_INTER_SUBNET = "Error, Connection between routers must not be weighted: ";
    private static final String ERROR_PARSE_CONNECTION = "Error, parsing connection: ";
    private static final String DEFAULT_GATEWAY = "0.0.0.0";
    private static final String IP_DELIMITER = "\\.";
    private static final String CIDR_DELIMITER = "/";
    private static final String CONNECTION_DELIMITER = "<-->";
    private static final int IP_OCTET_COUNT = 4;
    private static final int MAX_IP_OCTET = 255;
    private static final int MIN_SUBNET_MASK = 0;
    private static final int MAX_SUBNET_MASK = 31;

    private NetworkValidator() {
        // Utility class.
    }

    /**
     * Check if the IP is valid.
     * It checks if the IP is in the correct format, if the octets are valid, and if the IP is not in the reserved range.
     * @param ip the IP to check
     * @return true if the IP is valid, false otherwise
     */
    public static boolean isValidIp(String ip) {
        if (ip.equals(DEFAULT_GATEWAY)) { // Allow the default gateway.
            return true;
        } // Check if the IP is in the correct format.
        String[] octets = ip.split(IP_DELIMITER);
        if (octets.length != IP_OCTET_COUNT) {
            return false;
        } // Check if the octets are valid.
        for (String octet : octets) {
            try {
                int value = Integer.parseInt(octet);
                if (value < 0 || value > MAX_IP_OCTET) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        } // Check if the IP is not in the reserved range.
        return true;
    }

    /**
     * Check if the subnet is valid.
     * It checks if the IP and mask are valid by calling the isValidIp method and if the mask is in the valid range.
     * @param cidr the subnet to check.
     * @return true if the subnet is valid, false otherwise.
     */
    public static boolean isValidSubnet(String cidr) {
        String[] parts = cidr.split(CIDR_DELIMITER);
        if (parts.length != 2) {
            return false;
        } // Check if the IP and mask are valid.
        String ip = parts[0];
        int mask;
        try {
            mask = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        return NetworkValidator.isValidIp(ip) && mask >= MIN_SUBNET_MASK && mask <= MAX_SUBNET_MASK;
    }

    /**
     * Check if the subnet is valid.
     * @param subnet1 the first subnet
     * @param subnet2 the second subnet
     * @return true if the subnets are valid, false otherwise
     */
    public static boolean isOverlapping(Subnet subnet1, Subnet subnet2) {
        long start1 = subnet1.getFirstIpAsLong();
        long end1 = subnet1.getLastIpAsLong();
        long start2 = subnet2.getFirstIpAsLong();
        long end2 = subnet2.getLastIpAsLong();
        // Check if the ranges overlap.
        return (start1 <= end2) && (start2 <= end1);
    }

    /**
     * Check if the connection is valid.
     * It checks if the systems are in the same subnet, if the systems are routers, and if the connection is weighted.
     * @param system1 the first system
     * @param system2 the second system
     * @param weight the weight of the connection
     * @return an error message if the connection is invalid, null otherwise
     */
    public static String isValidConnection(SystemNode system1, SystemNode system2, Integer weight) {
        // If both systems are in the same subnet, the connection must be weighted.
        if (system1.getSubnet().equals(system2.getSubnet())) {
            if (weight == null) {
                return ERROR_UNWEIGHTED_CONNECTION + system1.getName() + CONNECTION_DELIMITER + system2.getName();
            }
            return null;
        }
        // If both systems are routers, the connection is valid but must not be weighted.
        if (system1 instanceof Router && system2 instanceof Router) {
            if (weight != null) {
                return ERROR_WEIGHTED_INTER_SUBNET + system1.getName() + CONNECTION_DELIMITER + system2.getName();
            }
            return null;
        }
        // Otherwise, the connection is invalid.
        return ERROR_PARSE_CONNECTION + system1.getName() + CONNECTION_DELIMITER + system2.getName();
    }
}
