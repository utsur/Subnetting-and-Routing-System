package main.java.commands.list;

import main.java.commands.Command;
import main.java.model.Network;
import main.java.model.Subnet;

/**
 * This class represents a command to list the range of a subnet.
 * The command takes a network and a subnet as input and returns the first and last IP address of the subnet.
 * @author utsur
 */
public class ListRange implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'list range <subnet>'";
    private static final String ERROR_SUBNET = "Error, Subnet not found.";
    private static final String EMPTY_SPACE = " ";
    private static final int ARGUMENT_LENGTH = 3;
    private final Network network;

    /**
     * This constructor creates a new ListRange command with the given network.
     * @param network The network to list the range from.
     */
    public ListRange(Network network) {
        this.network = network;
    }

    @Override
    public String execute(String[] args) {
        if (args.length != ARGUMENT_LENGTH) {
            return ERROR_FORMAT;
        }
        // Get the first and last IP address of the subnet.
        String subnetCidr = args[2];
        Subnet subnet = network.getSubnetByCidr(subnetCidr);
        if (subnet == null) {
            return ERROR_SUBNET;
        }
        String firstIp = subnet.getFirstIp();
        String lastIp = subnet.getLastIp();
        return firstIp + EMPTY_SPACE + lastIp;
    }
}
