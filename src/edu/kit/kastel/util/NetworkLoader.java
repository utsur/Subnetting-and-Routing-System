package edu.kit.kastel.util;

import edu.kit.kastel.model.Computer;
import edu.kit.kastel.model.Connection;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Router;
import edu.kit.kastel.model.Subnet;
import edu.kit.kastel.model.Systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class is responsible for loading a network from a text file.
 * It creates the network, subnets, systems, and connections.
 * @author utsur
 */
public class NetworkLoader {
    private static final String GRAPH_START = "graph";
    private static final String SUBGRAPH_PREFIX = "subgraph";
    private static final String SUBGRAPH_END = "end";
    private static final String SYSTEM_DELIMITER = "[";
    private static final String CONNECTION_DELIMITER = "<-->";
    private static final String ROUTER_IDENTIFIER = "Router";
    private static final String ERROR_PARSE_SUBNET = "Error parsing subnet: ";
    private static final String ERROR_PARSE_SYSTEM = "Error parsing system: ";
    private static final String ERROR_PARSE_CONNECTION = "Error parsing connection: ";

    /**
     * Load a network from a file.
     * It reads the file line by line and creates the network, subnets, systems, and connections.
     * Uses the helper methods in this class to handle the different network parts.
     * @param filePath the path to the file
     * @return the network
     */
    public Network loadNetwork(String filePath) {
        Network network = new Network();
        List<String> outputLines = new ArrayList<>();
        outputLines.add(GRAPH_START);

        List<String> lines = FileHelper.readAllLines(filePath);
        Subnet currentSubnet = null;
        List<String> currentSubnetLines = new ArrayList<>();

        for (String originalLine : lines) {
            String line = originalLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith(SUBGRAPH_PREFIX)) {
                if (currentSubnet != null) {
                    finalizeSubnet(currentSubnetLines, outputLines);
                }
                currentSubnet = parseSubnet(line, network);
                currentSubnetLines = new ArrayList<>();
                currentSubnetLines.add("    " + line);
            } else if (line.contains(SYSTEM_DELIMITER)) {
                parseSystem(line, currentSubnet, network);
                currentSubnetLines.add("        " + line);
            } else if (line.contains(CONNECTION_DELIMITER)) {
                parseConnection(line, network);
                currentSubnetLines.add("        " + line);
            } else if (line.equals(SUBGRAPH_END)) {
                currentSubnetLines.add("    " + line);
            } else {
                outputLines.add(line);
            }
        }

        if (currentSubnet != null) {
            finalizeSubnet(currentSubnetLines, outputLines);
        }

        return network;
    }

    private void finalizeSubnet(List<String> subnetLines, List<String> outputLines) {
        List<String> systemLines = new ArrayList<>();
        List<String> connectionLines = new ArrayList<>();

        for (String line : subnetLines) {
            if (line.contains(SYSTEM_DELIMITER)) {
                systemLines.add(line);
            } else if (line.contains(CONNECTION_DELIMITER)) {
                connectionLines.add(line);
            } else {
                outputLines.add(line);
            }
        }

        Collections.sort(systemLines, new SystemComparator());
        outputLines.addAll(systemLines);
        outputLines.addAll(connectionLines);
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

    private void parseSystem(String line, Subnet subnet, Network network) {
        String[] parts = line.split("\\[|\\]");
        if (parts.length != 2) {
            System.out.println(ERROR_PARSE_SYSTEM + line);
            return;
        }

        String name = parts[0].trim();
        String ip = parts[1].trim();

        Systems system;
        if (name.contains(ROUTER_IDENTIFIER)) {
            system = new Router(name, ip, subnet);
        } else {
            system = new Computer(name, ip, subnet);
        }

        subnet.addSystem(system);
        network.addSystem(system);
    }

    private void parseConnection(String line, Network network) {
        String[] parts = line.split("<-->", 2);
        if (parts.length != 2) {
            System.out.println(ERROR_PARSE_CONNECTION + line);
            return;
        }

        String system1Name = parts[0].trim();
        String system2NameAndWeight = parts[1].trim();

        String system2Name;
        Integer weight = null;

        if (system2NameAndWeight.startsWith("|")) {
            int endWeightIndex = system2NameAndWeight.indexOf("|", 1);
            if (endWeightIndex == -1) {
                System.out.println(ERROR_PARSE_CONNECTION + line);
                return;
            }
            try {
                weight = Integer.parseInt(system2NameAndWeight.substring(1, endWeightIndex).trim());
                system2Name = system2NameAndWeight.substring(endWeightIndex + 1).trim();
            } catch (NumberFormatException e) {
                System.out.println(ERROR_PARSE_CONNECTION + line);
                return;
            }
        } else {
            system2Name = system2NameAndWeight;
        }

        Systems system1 = network.getSystemByName(system1Name);
        Systems system2 = network.getSystemByName(system2Name);

        if (system1 != null && system2 != null) {
            network.addConnection(new Connection(system1, system2, weight));
        } else {
            System.out.println(ERROR_PARSE_CONNECTION + line);
        }
    }

    private static final class SystemComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            boolean isRouter1 = s1.contains(ROUTER_IDENTIFIER);
            boolean isRouter2 = s2.contains(ROUTER_IDENTIFIER);

            if (isRouter1 && !isRouter2) {
                return -1;
            } else if (!isRouter1 && isRouter2) {
                return 1;
            } else {
                return s1.compareTo(s2);
            }
        }
    }
}
