package edu.kit.kastel.commands.list;

import edu.kit.kastel.commands.Command;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Subnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a command to list all subnets in a network.
 * The command takes a network as input and returns a list of all subnets in the network.
 * The subnets are sorted by their CIDR.
 * @author utsur
 */
public class ListSubnetsCommand implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'list subnets'";
    private static final String SUBNETS_STRING = "subnets";
    private static final String EMPTY_SPACE = " ";
    private final Network network;

    /**
     * This constructor creates a new ListSubnetsCommand object with the given network.
     * @param network The network to list subnets from.
     */
    public ListSubnetsCommand(Network network) {
        this.network = network;
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 2 || !args[1].equals(SUBNETS_STRING)) {
            return ERROR_FORMAT;
        }
        List<String> subnetCidrs = new ArrayList<>();
        for (Subnet subnet : network.getSubnets()) {
            subnetCidrs.add(subnet.getCidr());
        }
        Collections.sort(subnetCidrs, this::compareSubnetCidrs);
        return String.join(EMPTY_SPACE, subnetCidrs);
    }

    private int compareSubnetCidrs(String cidr1, String cidr2) {
        String[] parts1 = cidr1.split("\\.|/");
        String[] parts2 = cidr2.split("\\.|/");
        for (int i = 0; i < 4; i++) {
            int octet1 = Integer.parseInt(parts1[i]);
            int octet2 = Integer.parseInt(parts2[i]);
            if (octet1 != octet2) {
                return Integer.compare(octet1, octet2);
            }
        }
        return Integer.compare(Integer.parseInt(parts1[4]), Integer.parseInt(parts2[4]));
    }
}
