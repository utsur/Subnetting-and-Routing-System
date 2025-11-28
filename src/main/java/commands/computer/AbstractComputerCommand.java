package main.java.commands.computer;

import main.java.commands.Command;
import main.java.model.Network;
import main.java.model.Subnet;

/**
 * This abstract class provides shared functionality for computer-related commands.
 * @author utsur
 */
public abstract class AbstractComputerCommand implements Command {
    protected static final String ERROR_FORMAT = "Error, Invalid command format. Use '%s computer <subnet> <ip>'";
    protected static final int EXPECTED_ARGS = 4;

    protected final Network network;

    /**
     * Creates a new AbstractComputerCommand with the given network.
     * @param network The network to add or remove the computer from.
     */
    protected AbstractComputerCommand(Network network) {
        this.network = network;
    }

    /**
     * Validates the subnet and returns it if it is valid.
     * If the IP address does not belong to a computer, the subnet or IP address is invalid,
     * or the command format is invalid, an error message is returned.
     * @param args The arguments of the command.
     * @return The subnet if it is valid, null otherwise.
     */
    protected Subnet validateAndGetSubnet(String[] args) {
        if (args.length != EXPECTED_ARGS) {
            return null;
        }

        String subnetCidr = args[2];
        return network.getSubnetByCidr(subnetCidr);
    }
}
