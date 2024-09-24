package edu.kit.kastel.commands.connection;

import edu.kit.kastel.commands.Command;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Systems;

/**
 * This class represents the remove connection command.
 * It removes a connection between two systems in the network.
 * @author utsur
 */
public class RemoveConnection implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'remove connection <ip1> <ip2>'";
    private static final String ERROR_INVALID_IP = "Error, Invalid IP address.";
    private static final String ERROR_NO_CONNECTION = "Error, No connection exists between the specified systems.";
    private static final int EXPECTED_ARGS = 4;
    private static final int IP1 = 2;
    private static final int IP2 = 3;
    private final Network network;

    /**
     * Creates a new remove connection command.
     * @param network The network to remove the connection from.
     */
    public RemoveConnection(Network network) {
        this.network = network;
    }

    @Override
    public String execute(String[] args) {
        if (args.length != EXPECTED_ARGS) {
            return ERROR_FORMAT;
        }
        // Get the systems by their IP addresses.
        String ip1 = args[IP1];
        String ip2 = args[IP2];
        Systems system1 = network.getSystemByIp(ip1);
        Systems system2 = network.getSystemByIp(ip2);
        // Check if the systems exist.
        if (system1 == null || system2 == null) {
            return ERROR_INVALID_IP;
        }
        // Check if a connection exists between the two systems.
        if (!network.connectionExists(system1, system2)) {
            return ERROR_NO_CONNECTION;
        }
        // Remove the connection between the two systems.
        network.removeConnection(system1, system2);
        return null;
    }
}
