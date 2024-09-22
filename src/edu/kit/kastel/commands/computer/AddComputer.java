package edu.kit.kastel.commands.computer;

import edu.kit.kastel.model.Computer;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Subnet;

/**
 * This class represents the command to add a computer to the network.
 * The computer is added to the specified subnet with the given IP address.
 * @author utsur
 */
public class AddComputer extends AbstractComputerCommand {
    private static final String ERROR_IP_EXISTS = "Error, IP address already exists in the network.";
    private static final String ERROR_IP_NOT_IN_SUBNET = "Error, IP address is not in the specified subnet.";
    private static final String PC_NAME = "PC_";

    /**
     * Creates a new AddComputer with the given network.
     * @param network The network to add the computer to.
     */
    public AddComputer(Network network) {
        super(network);
    }

    @Override
    public String execute(String[] args) {
        Subnet subnet = validateAndGetSubnet(args);
        if (subnet == null) {
            return String.format(ERROR_FORMAT, "add");
        }

        String ip = args[3];

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
