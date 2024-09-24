package edu.kit.kastel.commands.computer;

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
public class RemoveComputer extends AbstractComputerCommand {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'remove computer <subnet> <ip>'";
    private static final String ERROR_INVALID_IP = "Error, Invalid IP address.";
    private static final String ERROR_NOT_COMPUTER = "Error, The specified IP does not belong to a computer.";
    private static final String ERROR_NOT_IN_SUBNET = "Error, The specified IP does not exist in the given subnet.";
    private static final String REMOVE = "remove";
    private static final int NUMBER_OF_ARGUMENTS = 3;

    /**
     * Creates a new RemoveComputer with the given network.
     * @param network The network to remove the computer from.
     */
    public RemoveComputer(Network network) {
        super(network);
    }

    @Override
    public String execute(String[] args) {
        Subnet subnet = validateAndGetSubnet(args);
        if (subnet == null) {
            return String.format(ERROR_FORMAT, REMOVE);
        }

        String ip = args[NUMBER_OF_ARGUMENTS];
        // Check if the IP address is valid.
        Systems system = network.getSystemByIp(ip);
        if (system == null) {
            return ERROR_INVALID_IP;
        }
        // Check if the system is a computer.
        if (!(system instanceof Computer)) {
            return ERROR_NOT_COMPUTER;
        }
        // Check if the IP address is in the subnet.
        if (system.getSubnet() != subnet) {
            return ERROR_NOT_IN_SUBNET;
        }
        // Remove the computer from the network and subnet.
        network.removeSystem(system);
        subnet.removeSystem(system);
        // Return null to indicate that the command was executed successfully.
        return null;
    }
}
