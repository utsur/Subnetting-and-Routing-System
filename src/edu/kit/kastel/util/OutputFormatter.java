package edu.kit.kastel.util;

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
 * This class provides a method to convert a network to a mermaid graph.
 * The mermaid graph can be used to visualize the network.
 * @author utsur
 */
public final class OutputFormatter {
    private static final String GRAPH_START = "graph";
    private static final String SUBGRAPH_START = "    subgraph ";
    private static final String SUBGRAPH_END = "    end";
    private static final String SYSTEM_FORMAT = "        %s[%s]";
    private static final String CONNECTION_FORMAT = "        %s <-->%s%s";
    private static final String INTER_SUBNET_CONNECTION_FORMAT = "    %s <--> %s";
    private static final String NEW_LINE = "\n";

    private OutputFormatter() {
        // private constructor to prevent instantiation
    }

    /**
     * Converts a network to a mermaid graph.
     * The mermaid graph can be used to visualize the network.
     *
     * @param network The network to convert.
     * @return The Mermaid-Syntax.
     */
    public static String toMermaid(Network network) {
        StringBuilder sb = new StringBuilder(GRAPH_START).append(NEW_LINE);

        for (Subnet subnet : network.getSubnets()) {
            sb.append(SUBGRAPH_START).append(subnet.getCidr()).append(NEW_LINE);

            List<Systems> sortedSystems = new ArrayList<>(subnet.getSystems());
            Collections.sort(sortedSystems, new SystemComparator());

            for (Systems system : sortedSystems) {
                sb.append(String.format(SYSTEM_FORMAT, system.getName(), system.getIpAddress())).append(NEW_LINE);
            }

            List<Connection> subnetConnections = getSubnetConnections(network, subnet);
            Collections.sort(subnetConnections, new ConnectionComparator());

            for (Connection conn : subnetConnections) {
                sb.append(String.format(CONNECTION_FORMAT, conn.getSystem1().getName(),
                    conn.getWeight(), conn.getSystem2().getName())).append(NEW_LINE);
            }
            sb.append(SUBGRAPH_END).append(NEW_LINE);
        }

        List<Connection> interSubnetConnections = getInterSubnetConnections(network);
        for (Connection conn : interSubnetConnections) {
            sb.append(String.format(INTER_SUBNET_CONNECTION_FORMAT, conn.getSystem1().getName(),
                conn.getSystem2().getName())).append(NEW_LINE);
        }

        return sb.toString().trim(); // Trim to remove the last newline
    }

    private static List<Connection> getSubnetConnections(Network network, Subnet subnet) {
        List<Connection> subnetConnections = new ArrayList<>();
        for (Connection conn : network.getConnections()) {
            if (conn.getSystem1().getSubnet() == subnet && conn.getSystem2().getSubnet() == subnet) {
                subnetConnections.add(conn);
            }
        }
        return subnetConnections;
    }

    private static List<Connection> getInterSubnetConnections(Network network) {
        List<Connection> interSubnetConnections = new ArrayList<>();
        for (Connection conn : network.getConnections()) {
            if (conn.getSystem1().getSubnet() != conn.getSystem2().getSubnet()) {
                interSubnetConnections.add(conn);
            }
        }
        return interSubnetConnections;
    }


    private static final class SystemComparator implements Comparator<Systems> {
        @Override
        public int compare(Systems s1, Systems s2) {
            if (s1 instanceof Router && !(s2 instanceof Router)) {
                return -1;
            }
            if (!(s1 instanceof Router) && s2 instanceof Router) {
                return 1;
            }
            return s1.getName().compareTo(s2.getName());
        }
    }


    private static final class ConnectionComparator implements Comparator<Connection> {
        @Override
        public int compare(Connection c1, Connection c2) {
            boolean c1HasRouter = c1.getSystem1() instanceof Router || c1.getSystem2() instanceof Router;
            boolean c2HasRouter = c2.getSystem1() instanceof Router || c2.getSystem2() instanceof Router;

            if (c1HasRouter && !c2HasRouter) {
                return -1;
            }
            if (!c1HasRouter && c2HasRouter) {
                return 1;
            }

            return c1.getSystem1().getName().compareTo(c2.getSystem1().getName());
        }
    }
}
