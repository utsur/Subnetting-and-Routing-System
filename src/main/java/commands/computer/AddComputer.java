package commands.computer;

import model.Computer;
import model.Network;
import model.Subnet;

/**
 * This class represents the command to add a computer to the network.
 * The computer is added to the specified subnet with the given IP address.
 */
public class AddComputer extends AbstractComputerCommand {
    private static final String ERROR_IP_EXISTS = "Error, IP address already exists in the network.";
    private static final String ERROR_IP_NOT_IN_SUBNET = "Error, IP address is not in the specified subnet.";
    private static final String PC_NAME = "PC_";
    private static final String ADD = "add";
    private static final String DOT = ".";
    private static final String UNDERLINE = "_";
    private static final int NUMBER_OF_ARGUMENTS = 3;

    /**
     * Creates a new AddComputer command with the given network.
     * @param network The network to add the computer to.
     */
    public AddComputer(Network network) {
        super(network);
    }

    @Override
    public String execute(String[] args) {
        Subnet subnet = validateAndGetSubnet(args);
        if (subnet == null) {
            return String.format(ERROR_FORMAT, ADD);
        }

        String ip = args[NUMBER_OF_ARGUMENTS];
        // Check if the IP address already exists in the network.
        if (network.getSystemByIp(ip) != null) {
            return ERROR_IP_EXISTS;
        }
        // Check if the IP address is in the subnet.
        if (!subnet.isIpInSubnet(ip)) {
            return ERROR_IP_NOT_IN_SUBNET;
        }
        // Create a new computer and add it to the network and subnet.
        Computer computer = new Computer(PC_NAME + ip.replace(DOT, UNDERLINE), ip, subnet);
        network.addSystem(computer);
        subnet.addSystem(computer);
        // Return null to indicate that the command was executed successfully.
        return null;
    }
}
