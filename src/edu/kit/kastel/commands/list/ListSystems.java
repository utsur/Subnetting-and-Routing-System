package edu.kit.kastel.commands.list;

import edu.kit.kastel.commands.Command;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Router;
import edu.kit.kastel.model.Subnet;
import edu.kit.kastel.model.Systems;
import edu.kit.kastel.util.IpAddressComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a command to list all systems in a subnet.
 * The command takes a network and a subnet as input and returns a list of all systems in the subnet.
 * @author utsur
 */
public class ListSystems implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'list systems <subnet>'";
    private static final String ERROR_SUBNET = "Error, Subnet not found.";
    private static final String EMPTY_SPACE = " ";
    private static final int FORMAT_ARGS = 3;
    private final Network network;

    /**
     * This constructor creates a new ListSystems object with the given network.
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

        List<String> ipAddresses = new ArrayList<>();
        // Add router IP first if it exists
        for (Systems system : subnet.getSystems()) {
            if (system instanceof Router) {
                ipAddresses.add(system.getIpAddress());
                break;
            }
        }
        // Add other IPs.
        for (Systems system : subnet.getSystems()) {
            if (!(system instanceof Router)) {
                ipAddresses.add(system.getIpAddress());
            }
        }
        // Sort non-router IPs.
        if (ipAddresses.size() > 1) {
            ipAddresses.subList(1, ipAddresses.size()).sort(IpAddressComparator::compareIpAddresses);
        }

        return String.join(EMPTY_SPACE, ipAddresses);
    }
}
