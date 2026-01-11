package commands.list;

import commands.Command;
import model.Network;
import model.Subnet;
import helpers.IpAddressComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a command to list all subnets in a network.
 * The command takes a network as input and returns a list of all subnets in the network.
 * The subnets are sorted by their CIDR.
 */
public class ListSubnets implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'list subnets'";
    private static final String SUBNETS_STRING = "subnets";
    private static final String EMPTY_SPACE = " ";
    private final Network network;

    /**
     * This constructor creates a new ListSubnets command with the given network.
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
        // Get the CIDRs of all subnets in the network.
        List<String> subnetCIDRs = new ArrayList<>();
        for (Subnet subnet : network.getSubnets()) {
            subnetCIDRs.add(subnet.getCidr());
        }
        // Sort the subnets by their CIDR and return them.
        subnetCIDRs.sort(IpAddressComparator::compareSubnetCIDRs);
        return String.join(EMPTY_SPACE, subnetCIDRs);
    }
}
