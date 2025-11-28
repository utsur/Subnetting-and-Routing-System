package main.java.commands.connection;

import main.java.commands.Command;
import main.java.model.Connection;
import main.java.model.Network;
import main.java.model.Router;
import main.java.model.SystemNode;

/**
 * This class represents the add connection command.
 * It adds a connection between two systems in the network.
 * @author utsur
 */
public class AddConnection implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'add connection <ip1> <ip2> [<weight>]'";
    private static final String ERROR_INVALID_IP = "Error, Invalid IP address.";
    private static final String ERROR_SAME_IP = "Error, Cannot create a connection to the same IP address.";
    private static final String ERROR_DIFFERENT_SUBNET = "Error, Only routers can have connections to other subnets.";
    private static final String ERROR_CONNECTION_EXISTS = "Error, Connection already exists.";
    private static final int MIN_ARGS = 4;
    private static final int MAX_ARGS = 5;
    private static final int WEIGHT_ARGS = 4;
    private static final int IP1 = 2;
    private static final int IP2 = 3;
    private final Network network;

    /**
     * Creates a new add connection command.
     * @param network The network to add the connection to.
     */
    public AddConnection(Network network) {
        this.network = network;
    }

    @Override
    public String execute(String[] args) {
        if (args.length < MIN_ARGS || args.length > MAX_ARGS) {
            return ERROR_FORMAT;
        }

        String ip1 = args[IP1];
        String ip2 = args[IP2];
        // Check if the IP addresses are the same.
        if (ip1.equals(ip2)) {
            return ERROR_SAME_IP;
        }
        // Check if the weight is provided using the parseWeight helper method.
        Integer weight = parseWeight(args);
        if (weight == null && args.length == MAX_ARGS) {
            return ERROR_FORMAT;
        }
        // Get the systems by their IP addresses.
        SystemNode system1 = network.getSystemByIp(ip1);
        SystemNode system2 = network.getSystemByIp(ip2);
        // Check if the systems exist.
        if (system1 == null || system2 == null) {
            return ERROR_INVALID_IP;
        }
        // Check if a connection already exists between the two systems.
        if (network.connectionExists(system1, system2)) {
            return ERROR_CONNECTION_EXISTS;
        }
        // Checks if the connection is valid.
        String validationError = validateConnection(system1, system2, weight);
        if (validationError != null) {
            return validationError;
        }
        // Add the connection between the two systems.
        network.addConnection(new Connection(system1, system2, weight));
        return null;
    }
    // Helper method to parse the weight from the arguments.
    private Integer parseWeight(String[] args) {
        if (args.length == MAX_ARGS) {
            try {
                return Integer.parseInt(args[WEIGHT_ARGS]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    // Helper method to validate the connection.
    private String validateConnection(SystemNode system1, SystemNode system2, Integer weight) {
        // Check if the systems are in the same subnet. If they are, a weight must be provided.
        if (system1.getSubnet() == system2.getSubnet()) {
            if (weight == null) {
                return ERROR_FORMAT;
            }
        } else {
            // Check if the systems are routers. Only routers can have connections to other subnets.
            if (!(system1 instanceof Router && system2 instanceof Router)) {
                return ERROR_DIFFERENT_SUBNET;
            }
        }
        // No validation error.
        return null;
    }
}
