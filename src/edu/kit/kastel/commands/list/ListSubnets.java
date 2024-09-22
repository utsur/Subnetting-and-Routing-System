package edu.kit.kastel.commands.list;

import edu.kit.kastel.commands.Command;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Subnet;
import edu.kit.kastel.util.IpAddressComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a command to list all subnets in a network.
 * The command takes a network as input and returns a list of all subnets in the network.
 * The subnets are sorted by their CIDR.
 * @author utsur
 */
public class ListSubnets implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'list subnets'";
    private static final String SUBNETS_STRING = "subnets";
    private static final String EMPTY_SPACE = " ";
    private final Network network;

    /**
     * This constructor creates a new ListSubnets object with the given network.
     * @param network The network to list subnets from.
     */
    public ListSubnets(Network network) {
        this.network = network;
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 2 || !args[1].equals(SUBNETS_STRING)) {
            return ERROR_FORMAT;
        }
        List<String> subnetCIDRs = new ArrayList<>();
        for (Subnet subnet : network.getSubnets()) {
            subnetCIDRs.add(subnet.getCidr());
        }
        subnetCIDRs.sort(IpAddressComparator::compareSubnetCidrs);
        return String.join(EMPTY_SPACE, subnetCIDRs);
    }
}
