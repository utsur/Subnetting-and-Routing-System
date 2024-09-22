package edu.kit.kastel.commands.computer;

import edu.kit.kastel.commands.Command;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Subnet;

/**
 * This class represents the command to add or remove a computer from the network.
 * The computer is added or removed from the specified subnet with the given IP address.
 * If the IP address does not belong to a computer, an error message is returned.
 * If the subnet or IP address is invalid, an error message is returned.
 * If the command format is invalid, an error message is returned.
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
