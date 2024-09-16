package edu.kit.kastel.commands.list;

import edu.kit.kastel.commands.Command;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Subnet;
import edu.kit.kastel.model.Systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a command to list all systems in a subnet.
 * The command takes a network and a subnet as input and returns a list of all systems in the subnet.
 * @author utsur
 */
public class ListSystemsCommand implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'list systems <subnet>'";
    private static final String ERROR_SUBNET = "Error, Subnet not found.";
    private final Network network;

    /**
     * This constructor creates a new ListSystemsCommand object with the given network.
     * @param network The network to list systems from.
     */
    public ListSystemsCommand(Network network) {
        this.network = network;
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 3) {
            return ERROR_FORMAT;
        }
        String subnetCidr = args[2];
        Subnet subnet = network.getSubnetByCidr(subnetCidr);
        if (subnet == null) {
            return ERROR_SUBNET;
        }
        List<String> ipAddresses = new ArrayList<>();
        for (Systems system : subnet.getSystems()) {
            ipAddresses.add(system.getIpAddress());
        }
        Collections.sort(ipAddresses);
        return String.join(" ", ipAddresses);
    }
}
