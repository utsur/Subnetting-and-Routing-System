package commands.list;

import commands.Command;
import model.*;
import helpers.IpAddressComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a command to list all systems in a subnet.
 * The command takes a network and a subnet as input and returns a list of all systems in the subnet.
 */
public class ListSystems implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'list systems <subnet>'";
    private static final String ERROR_SUBNET = "Error, Subnet not found.";
    private static final String EMPTY_SPACE = " ";
    private static final int FORMAT_ARGS = 3;
    private final Network network;

    /**
     * This constructor creates a new ListSystems command with the given network.
     * @param network The network to list systems from.
     */
    public ListSystems(Network network) {
        this.network = network;
    }

    @Override
    public String execute(String[] args) {
        if (args.length != FORMAT_ARGS) {
            return ERROR_FORMAT;
        }
        String subnetCidr = args[2];
        Subnet subnet = network.getSubnetByCidr(subnetCidr);
        if (subnet == null) {
            return ERROR_SUBNET;
        }
        // We differentiate the routers from the computer systems.
        List<String> routerIps = new ArrayList<>();
        List<String> otherIps = new ArrayList<>();
        // Sort the systems by their IP addresses.
        for (SystemNode system : subnet.getSystems()) {
            if (system instanceof Router) {
                routerIps.add(system.getIpAddress());
            } else {
                otherIps.add(system.getIpAddress());
            }
        }

        otherIps.sort(IpAddressComparator::compareIpAddresses);
        routerIps.addAll(otherIps);
        // Return the list of systems.
        return String.join(EMPTY_SPACE, routerIps);
    }
}
