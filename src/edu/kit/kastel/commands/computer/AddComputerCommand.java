package edu.kit.kastel.commands.computer;

import edu.kit.kastel.commands.Command;
import edu.kit.kastel.model.Computer;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Subnet;

/**
 * This class represents the command to add a computer to the network.
 * The computer is added to the specified subnet with the given IP address.
 * @author utsur
 */
public class AddComputerCommand implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'add computer <subnet> <ip>'";
    private static final String ERROR_INVALID_SUBNET = "Error, Invalid subnet.";
    private static final String ERROR_INVALID_IP = "Error, Invalid IP address.";
    private static final String ERROR_IP_EXISTS = "Error, IP address already exists in the network.";
    private static final String ERROR_IP_NOT_IN_SUBNET = "Error, IP address is not in the specified subnet.";
    private static final String PC_NAME = "PC_";
    private static final int EXPECTED_ARGS = 4;
    private final Network network;

    /**
     * Creates a new AddComputerCommand with the given network.
     * @param network The network to add the computer to.
     */
    public AddComputerCommand(Network network) {
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

        if (network.getSystemByIp(ip) != null) {
            return ERROR_IP_EXISTS;
        }

        if (!subnet.isIpInSubnet(ip)) {
            return ERROR_IP_NOT_IN_SUBNET;
        }

        Computer computer = new Computer(PC_NAME + ip.replace('.', '_'), ip, subnet);
        network.addSystem(computer);
        subnet.addSystem(computer);

        return null;
    }
}
