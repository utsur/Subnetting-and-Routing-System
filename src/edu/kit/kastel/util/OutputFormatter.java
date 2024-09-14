package edu.kit.kastel.util;

import edu.kit.kastel.model.Connection;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Subnet;
import edu.kit.kastel.model.Systems;

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
        StringBuilder sb = new StringBuilder("graph\n");

        for (Subnet subnet : network.getSubnets()) {
            sb.append(SUBGRAPH_START).append(subnet.getCidr()).append("\n");

            for (Systems system : subnet.getSystems()) {
                sb.append("        ").append(system.getName()).append("[").append(system.getIpAddress()).append("]\n");
            }

            for (Connection conn : network.getConnections()) {
                if (conn.getSystem1().getSubnet() == subnet && conn.getSystem2().getSubnet() == subnet) {
                    sb.append("        ").append(conn.getSystem1().getName())
                        .append(" <-->")
                        .append(conn.getWeight() != null ? "|" + conn.getWeight() + "|" : "")
                        .append(conn.getSystem2().getName()).append("\n");
                }
            }
            sb.append("    end\n");
        }

        for (Connection conn : network.getConnections()) {
            if (conn.getSystem1().getSubnet() != conn.getSystem2().getSubnet()) {
                sb.append("    ").append(conn.getSystem1().getName())
                    .append(" <--> ").append(conn.getSystem2().getName()).append("\n");
            }
        }

        return sb.toString();
    }
}
