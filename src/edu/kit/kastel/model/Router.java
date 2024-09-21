package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a router in the network.
 * @author utsur
 */
public class Router extends Systems {
    private Map<String, List<String>> routingTable;

    /**
     * This constructor creates a new Router object with the given IP address and subnet.
     * @param name The name of the router.
     * @param ipAddress The IP address of the router.
     * @param subnet The subnet to which the router belongs.
     */
    public Router(String name, String ipAddress, Subnet subnet) {
        super(name, ipAddress, subnet);
        this.routingTable = new HashMap<>();
        initializeRoutingTable();
    }

    private void initializeRoutingTable() {
        routingTable.put(this.getSubnet().getCidr(), Arrays.asList(this.getIpAddress()));
    }

    /**
     * Updates the routing table of the router with the new routes.
     * @param newRoutes The new routes to be added to the routing table.
     */
    public void updateRoutingTable(Map<String, List<String>> newRoutes) {
        for (Map.Entry<String, List<String>> entry : newRoutes.entrySet()) {
            String subnet = entry.getKey();
            List<String> path = new ArrayList<>(entry.getValue());
            path.add(0, this.getIpAddress());

            if (!routingTable.containsKey(subnet) || path.size() < routingTable.get(subnet).size()) {
                routingTable.put(subnet, path);
            } else if (path.size() == routingTable.get(subnet).size() && path.get(1).compareTo(routingTable.get(subnet).get(1)) < 0) {
                routingTable.put(subnet, path);
            }
        }
    }

    /**
     * Returns the routing table of the router.
     * @return The routing table of the router.
     */
    public Map<String, List<String>> getRoutingTable() {
        return new HashMap<>(routingTable);
    }
}
