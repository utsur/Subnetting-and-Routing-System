package edu.kit.kastel.commands.computer;

import edu.kit.kastel.commands.Command;
import edu.kit.kastel.model.Computer;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Subnet;
import edu.kit.kastel.model.Systems;

/**
 * This class represents the command to remove a computer from the network.
 * The computer is removed from the specified subnet with the given IP address.
 * If the IP address does not belong to a computer, an error message is returned.
 * If the subnet or IP address is invalid, an error message is returned.
 * If the command format is invalid, an error message is returned.
 * @author utsur
 */
public class RemoveComputerCommand implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'remove computer <subnet> <ip>'";
    private static final String ERROR_INVALID_SUBNET = "Error, Invalid subnet.";
    private static final String ERROR_INVALID_IP = "Error, Invalid IP address.";
    private static final String ERROR_NOT_COMPUTER = "Error, The specified IP does not belong to a computer.";
    private static final String ERROR_NOT_IN_SUBNET = "Error, The specified IP does not exist in the given subnet.";
    private static final int EXPECTED_ARGS = 4;
    private final Network network;

    /**
     * Creates a new RemoveComputerCommand with the given network.
     * @param network The network to remove the computer from.
     */
    public RemoveComputerCommand(Network network) {
        this.network = network;
    }

    @Override
    public String execute(String[] args) {
        if (args.length != EXPECTED_ARGS) {
            return ERROR_FORMAT;
        }

        String subnetCidr = args[2];
        String ip = args[3];

        Subnet subnet = network.getSubnetByCidr(subnetCidr);
        if (subnet == null) {
            return ERROR_INVALID_SUBNET;
        }

        Systems system = network.getSystemByIp(ip);
        if (system == null) {
            return ERROR_INVALID_IP;
        }

        if (!(system instanceof Computer)) {
            return ERROR_NOT_COMPUTER;
        }

        if (system.getSubnet() != subnet) {
            return ERROR_NOT_IN_SUBNET;
        }

        network.removeSystem(system);
        subnet.removeSystem(system);

        return null;
    }
}
