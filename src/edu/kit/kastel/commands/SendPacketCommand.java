package edu.kit.kastel.commands;

import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Systems;
import edu.kit.kastel.util.PathFinder;

import java.util.List;

/**
 * This class represents the send packet command.
 * It sends a packet from one system to another in the network.
 * The command finds the shortest path between the systems and returns the path.
 * If no path is found, an error message is returned.
 * @author utsur
 */
public class SendPacketCommand implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'send packet <source_ip> <destination_ip>'";
    private static final String ERROR_INVALID_IP = "Error, Invalid IP address.";
    private static final String ERROR_SAME_IP = "Error, Source and destination IP addresses cannot be the same.";
    private static final String ERROR_NO_PATH = "Error, No path found between the specified systems.";
    private static final int EXPECTED_ARGS = 4;
    private final Network network;
    private final PathFinder pathFinder;

    /**
     * Creates a new send packet command with the given network.
     * @param network The network to send the packet in.
     */
    public SendPacketCommand(Network network) {
        this.network = network;
        this.pathFinder = new PathFinder(network);
    }

    @Override
    public String execute(String[] args) {
        if (args.length != EXPECTED_ARGS) {
            return ERROR_FORMAT;
        }

        String sourceIp = args[2];
        String destinationIp = args[3];

        if (sourceIp.equals(destinationIp)) {
            return ERROR_SAME_IP;
        }

        Systems source = network.getSystemByIp(sourceIp);
        Systems destination = network.getSystemByIp(destinationIp);

        if (source == null || destination == null) {
            return ERROR_INVALID_IP;
        }

        List<Systems> path = pathFinder.findShortestPath(source, destination);

        if (path == null || path.isEmpty()) {
            return ERROR_NO_PATH;
        }

        return formatPath(path);
    }

    private String formatPath(List<Systems> path) {
        return String.join(" ", path.stream().map(Systems::getIpAddress).toList());
    }
}
