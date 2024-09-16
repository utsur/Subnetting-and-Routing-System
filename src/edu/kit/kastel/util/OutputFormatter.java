package edu.kit.kastel.util;

import edu.kit.kastel.model.Connection;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Router;
import edu.kit.kastel.model.Subnet;
import edu.kit.kastel.model.Systems;

import java.util.ArrayList;
import java.util.Collections;
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
     * @param network The network to convert.
     * @return The Mermaid-Syntax.
     */
    public static String toMermaid(Network network) {
        StringBuilder sb = new StringBuilder(GRAPH_START).append(NEW_LINE);

        for (Subnet subnet : network.getSubnets()) {
            sb.append(SUBGRAPH_START).append(subnet.getCidr()).append(NEW_LINE);

            List<Systems> sortedSystems = new ArrayList<>(subnet.getSystems());
            Collections.sort(sortedSystems, (s1, s2) -> {
                if (s1 instanceof Router && !(s2 instanceof Router)) {
                    return -1;
                }
                if (!(s1 instanceof Router) && s2 instanceof Router) {
                    return 1;
                }
                return s1.getName().compareTo(s2.getName());
            });

            for (Systems system : sortedSystems) {
                sb.append(String.format(SYSTEM_FORMAT, system.getName(), system.getIpAddress())).append(NEW_LINE);
            }

            for (Connection conn : network.getConnections()) {
                if (conn.getSystem1().getSubnet() == subnet && conn.getSystem2().getSubnet() == subnet) {
                    String weightPart = conn.getWeight() != null ? "|" + conn.getWeight() + "|" : "";
                    sb.append(String.format(CONNECTION_FORMAT, conn.getSystem1().getName(),
                        weightPart, conn.getSystem2().getName())).append(NEW_LINE);
                }
            }
            sb.append(SUBGRAPH_END).append(NEW_LINE);
        }

        for (Connection conn : network.getConnections()) {
            if (conn.getSystem1().getSubnet() != conn.getSystem2().getSubnet()) {
                sb.append(String.format(INTER_SUBNET_CONNECTION_FORMAT,
                    conn.getSystem1().getName(), conn.getSystem2().getName())).append(NEW_LINE);
            }
        }

        return sb.toString();
    }
}
