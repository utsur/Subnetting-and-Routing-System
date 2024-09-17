package edu.kit.kastel.commands.connection;

import edu.kit.kastel.commands.Command;
import edu.kit.kastel.model.Connection;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Router;
import edu.kit.kastel.model.Systems;

/**
 * This class represents the add connection command.
 * It adds a connection between two systems in the network.
 * @author utsur
 */
public class AddConnectionCommand implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'add connection <ip1> <ip2> [<weight>]'";
    private static final String ERROR_INVALID_IP = "Error, Invalid IP address.";
    private static final String ERROR_SAME_IP = "Error, Cannot create a connection to the same IP address.";
    private static final String ERROR_SAME_SUBNET = "Error, Connections between routers in the same subnet are not allowed.";
    private static final String ERROR_DIFFERENT_SUBNET = "Error, Only routers can have connections to other subnets.";
    private static final String ERROR_CONNECTION_EXISTS = "Error, Connection already exists.";
    private static final int MIN_ARGS = 4;
    private static final int MAX_ARGS = 5;
    private final Network network;

    /**
     * Creates a new add connection command.
     * @param network The network to add the connection to.
     */
    public AddConnectionCommand(Network network) {
        this.network = network;
    }

    @Override
    public String execute(String[] args) {
        if (args.length < MIN_ARGS || args.length > MAX_ARGS) {
            return ERROR_FORMAT;
        }

        String ip1 = args[2];
        String ip2 = args[3];

        if (ip1.equals(ip2)) {
            return ERROR_SAME_IP;
        }

        Integer weight = null;

        if (args.length == MAX_ARGS) {
            try {
                weight = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                return ERROR_FORMAT;
            }
        }

        Systems system1 = network.getSystemByIp(ip1);
        Systems system2 = network.getSystemByIp(ip2);

        if (system1 == null || system2 == null) {
            return ERROR_INVALID_IP;
        }

        if (network.connectionExists(system1, system2)) {
            return ERROR_CONNECTION_EXISTS;
        }

        if (system1.getSubnet() == system2.getSubnet()) {
            if (weight == null) {
                return ERROR_FORMAT;
            }
        } else {
            if (!(system1 instanceof Router && system2 instanceof Router)) {
                return ERROR_DIFFERENT_SUBNET;
            }
            weight = null;
        }

        network.addConnection(new Connection(system1, system2, weight));
        return null;
    }
}
