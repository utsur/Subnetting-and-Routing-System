package edu.kit.kastel.util;

import edu.kit.kastel.model.Computer;
import edu.kit.kastel.model.Connection;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Router;
import edu.kit.kastel.model.Subnet;
import edu.kit.kastel.model.Systems;

import java.util.List;

/**
 * This class is responsible for loading a network from a text file.
 * It creates the network, subnets, systems, and connections.
 * @author utsur
 */
public class NetworkLoader {
    private static final String ERROR_PARSE_SUBNET = "Error, parsing subnet: ";
    private static final String ERROR_PARSE_SYSTEM = "Error, parsing system: ";
    private static final String ERROR_PARSE_CONNECTION = "Error, parsing connection: ";
    private static final String ERROR_IP_NOT_IN_SUBNET = "Error, IP address %s is not in subnet %s";
    private static final String ERROR_OUTSIDE_SUBNET = "Error, system outside subnet: ";
    private static final String ERROR_INVALID_SUBNET = "Error, Invalid subnet: ";
    private static final String ERROR_OVERLAPPING_SUBNET = "Error, Overlapping subnet: ";
    private static final String ERROR_ROUTER_NOT_FIRST_IP = "Error, Router must have the first IP address in the subnet: ";
    private static final String SUBGRAPH_PREFIX = "subgraph";
    private static final String SYSTEM_DELIMITER = "[";
    private static final String CONNECTION_DELIMITER = "<-->";
    private static final String WEIGHT_DELIMITER = "|";
    private static final String EMPTY_SPACE = " ";
    private static final String ROUTER_IDENTIFIER = "Router";
    private static final String OVERLAPPING_SUBNET_MESSAGE = " overlaps with ";
    private static final String SYSTEM_NAME_IP_DELIMITER = "\\[|\\]";
    private static final String ROUTER_IP_ERROR_FORMAT = "%s%s (should be %s)";

    /**
     * Load a network from a file.
     * It reads the file line by line and creates the network, subnets, systems, and connections.
     * Uses the helper methods in this class to handle the different network parts.
     * @param filePath the path to the file
     * @return the network
     */
    public Network loadNetwork(String filePath) {
        Network network = new Network();
        List<String> lines = FileHelper.readAllLines(filePath);
        Subnet currentSubnet = null;

        for (String originalLine : lines) {
            String line = originalLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith(SUBGRAPH_PREFIX)) {
                currentSubnet = parseSubnet(line, network);
                if (currentSubnet == null) {
                    return null;  // Error message was already printed in parseSubnet.
                }
            } else if (line.contains(SYSTEM_DELIMITER)) {
                if (currentSubnet == null) {
                    System.out.println(ERROR_OUTSIDE_SUBNET + line);
                    return null;
                }
                if (!parseSystem(line, currentSubnet, network)) {
                    return null;  // Error message was already printed in parseSystem.
                }
            } else if (line.contains(CONNECTION_DELIMITER)) {
                String errorMessage = parseConnection(line, network);
                if (errorMessage != null) {
                    System.out.println(errorMessage);
                    return null;
                }
            }
        }

        return network;
    }

    // Helper methods to parse the different network parts.
    private Subnet parseSubnet(String line, Network network) {
        String[] parts = line.split(EMPTY_SPACE);
        if (parts.length != 2) {
            System.out.println(ERROR_PARSE_SUBNET + line);
            return null;
        } // Check if the subnet is valid.
        String cidr = parts[1];
        if (!NetworkValidator.isValidSubnet(cidr)) {
            System.out.println(ERROR_INVALID_SUBNET + cidr);
            return null;
        }
        Subnet newSubnet = new Subnet(cidr);
        // Check for overlapping subnets
        for (Subnet existingSubnet : network.getSubnets()) {
            if (NetworkValidator.isOverlapping(newSubnet, existingSubnet)) {
                System.out.println(ERROR_OVERLAPPING_SUBNET + cidr + OVERLAPPING_SUBNET_MESSAGE + existingSubnet.getCidr());
                return null;
            }
        }
        // Add the subnet to the network.
        network.addSubnet(newSubnet);
        return newSubnet;
    }

    private boolean parseSystem(String line, Subnet subnet, Network network) {
        String[] parts = line.split(SYSTEM_NAME_IP_DELIMITER);
        if (parts.length != 2) {
            System.out.println(ERROR_PARSE_SYSTEM + line);
            return false;
        }

        String name = parts[0].trim();
        String ip = parts[1].trim();
        // Check if the IP is in the subnet.
        if (!subnet.isIpInSubnet(ip)) {
            System.out.println(String.format(ERROR_IP_NOT_IN_SUBNET, ip, subnet.getCidr()));
            return false;
        }
        // Create the system.
        Systems system;
        if (name.contains(ROUTER_IDENTIFIER)) {
            String firstUsableIp = subnet.getFirstUsableIp();
            if (!ip.equals(firstUsableIp)) {
                System.out.println(String.format(ROUTER_IP_ERROR_FORMAT, ERROR_ROUTER_NOT_FIRST_IP, ip, firstUsableIp));
            }
            system = new Router(name, ip, subnet);
        } else {
            system = new Computer(name, ip, subnet);
        }
        // Add the system to the subnet and network.
        subnet.addSystem(system);
        network.addSystem(system);
        return true;
    }

    private String parseConnection(String line, Network network) {
        String[] parts = line.split(CONNECTION_DELIMITER, 2);
        if (parts.length != 2) {
            return ERROR_PARSE_CONNECTION + line;
        }

        String system1Name = parts[0].trim();
        String system2NameAndWeight = parts[1].trim();

        String system2Name;
        Integer weight = null;
        // Check if the connection is weighted.
        if (system2NameAndWeight.startsWith(WEIGHT_DELIMITER)) {
            int endWeightIndex = system2NameAndWeight.indexOf(WEIGHT_DELIMITER, 1);
            if (endWeightIndex == -1) {
                return ERROR_PARSE_CONNECTION + line;
            }
            try {
                weight = Integer.parseInt(system2NameAndWeight.substring(1, endWeightIndex).trim());
                system2Name = system2NameAndWeight.substring(endWeightIndex + 1).trim();
            } catch (NumberFormatException e) {
                return ERROR_PARSE_CONNECTION + line;
            }
        } else {
            system2Name = system2NameAndWeight;
        }

        Systems system1 = network.getSystemByName(system1Name);
        Systems system2 = network.getSystemByName(system2Name);
        // Check if the systems exist.
        if (system1 != null && system2 != null) {
            String errorMessage = NetworkValidator.isValidConnection(system1, system2, weight);
            if (errorMessage != null) {
                return errorMessage;
            } // Add the connection to the network.
            network.addConnection(new Connection(system1, system2, weight));
            return null;
        }
        // If the systems do not exist, return an error message.
        return ERROR_PARSE_CONNECTION + line;
    }
}
