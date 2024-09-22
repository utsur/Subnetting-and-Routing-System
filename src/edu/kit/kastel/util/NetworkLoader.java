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
    private static final String SUBGRAPH_PREFIX = "subgraph";
    private static final String SYSTEM_DELIMITER = "[";
    private static final String CONNECTION_DELIMITER = "<-->";
    private static final String ROUTER_IDENTIFIER = "Router";
    private static final String ERROR_PARSE_SUBNET = "Error, parsing subnet: ";
    private static final String ERROR_PARSE_SYSTEM = "Error, parsing system: ";
    private static final String ERROR_PARSE_CONNECTION = "Error, parsing connection: ";
    private static final String ERROR_IP_NOT_IN_SUBNET = "Error, IP address %s is not in subnet %s";
    private static final String ERROR_OUTSIDE_SUBNET = "Error, system outside subnet: ";

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
        boolean hasError = false;

        for (String originalLine : lines) {
            String line = originalLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith(SUBGRAPH_PREFIX)) {
                currentSubnet = parseSubnet(line, network);
            } else if (line.contains(SYSTEM_DELIMITER)) {
                if (currentSubnet == null) {
                    System.out.println(ERROR_OUTSIDE_SUBNET + line);
                    hasError = true;
                    continue;
                }
                if (!parseSystem(line, currentSubnet, network)) {
                    hasError = true;
                }
            } else if (line.contains(CONNECTION_DELIMITER)) {
                parseConnection(line, network);
            }
        }

        return hasError ? null : network;
    }

    private Subnet parseSubnet(String line, Network network) {
        String[] parts = line.split(" ");
        if (parts.length != 2) {
            System.out.println(ERROR_PARSE_SUBNET + line);
            return null;
        }
        Subnet subnet = new Subnet(parts[1]);
        network.addSubnet(subnet);
        return subnet;
    }

    private boolean parseSystem(String line, Subnet subnet, Network network) {
        String[] parts = line.split("\\[|\\]");
        if (parts.length != 2) {
            System.out.println(ERROR_PARSE_SYSTEM + line);
            return false;
        }

        String name = parts[0].trim();
        String ip = parts[1].trim();

        if (!subnet.isIpInSubnet(ip)) {
            System.out.println(String.format(ERROR_IP_NOT_IN_SUBNET, ip, subnet.getCidr()));
            return false;
        }

        Systems system;
        if (name.contains(ROUTER_IDENTIFIER)) {
            system = new Router(name, ip, subnet);
        } else {
            system = new Computer(name, ip, subnet);
        }

        subnet.addSystem(system);
        network.addSystem(system);
        return true;
    }

    private boolean parseConnection(String line, Network network) {
        String[] parts = line.split("<-->", 2);
        if (parts.length != 2) {
            return false;
        }

        String system1Name = parts[0].trim();
        String system2NameAndWeight = parts[1].trim();

        String system2Name;
        Integer weight = null;

        if (system2NameAndWeight.startsWith("|")) {
            int endWeightIndex = system2NameAndWeight.indexOf("|", 1);
            if (endWeightIndex == -1) {
                return false;
            }
            try {
                weight = Integer.parseInt(system2NameAndWeight.substring(1, endWeightIndex).trim());
                system2Name = system2NameAndWeight.substring(endWeightIndex + 1).trim();
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            system2Name = system2NameAndWeight;
        }

        Systems system1 = network.getSystemByName(system1Name);
        Systems system2 = network.getSystemByName(system2Name);

        if (system1 != null && system2 != null) {
            network.addConnection(new Connection(system1, system2, weight));
            return true;
        }

        return false;
    }
}
