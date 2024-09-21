package edu.kit.kastel.util;

import edu.kit.kastel.model.Router;
import edu.kit.kastel.model.Subnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class for managing BGP routers.
 * This class is used to manage the routing table of a BGP router.
 * It can update the routing table with the routing table of a neighbor BGP router.
 * It can also return the path to a specified subnet.
 * @author utsur
 */
public class BGPRouter {
    private final Router router;
    private final Map<String, List<Router>> routingTable;

    /**
     * Creates a new BGP router with the specified router.
     * @param router The router to create the BGP router with.
     */
    public BGPRouter(Router router) {
        this.router = router;
        this.routingTable = new HashMap<>();
        initializeRoutingTable();
    }

    private void initializeRoutingTable() {
        Subnet subnet = router.getSubnet();
        routingTable.put(subnet.getCidr(), new ArrayList<>(List.of(router)));
    }

    /**
     * Updates the routing table of this BGP router with the routing table of the specified neighbor.
     * @param neighbor The neighbor to update the routing table with.
     */
    public void updateRoutingTable(BGPRouter neighbor) {
        Map<String, List<Router>> neighborTable = neighbor.getRoutingTable();
        for (Map.Entry<String, List<Router>> entry : neighborTable.entrySet()) {
            String subnet = entry.getKey();
            List<Router> path = entry.getValue();

            if (!routingTable.containsKey(subnet) || (path.size() + 1 < routingTable.get(subnet).size())
                || (path.size() + 1 == routingTable.get(subnet).size()
                && neighbor.getRouter().getIpAddress().compareTo(routingTable.get(subnet).get(0).getIpAddress()) < 0)) {

                List<Router> newPath = new ArrayList<>();
                newPath.add(neighbor.getRouter());
                newPath.addAll(path);
                routingTable.put(subnet, newPath);
            }
        }
    }

    /**
     * Returns the router associated with this BGP router.
     * @return The router associated with this BGP router.
     */
    public Router getRouter() {
        return router;
    }

    /**
     * Returns a copy of the routing table.
     * @return A copy of the routing table.
     */
    public Map<String, List<Router>> getRoutingTable() {
        return new HashMap<>(routingTable);
    }

    /**
     * Returns the path to the specified subnet.
     * @param subnetCidr The CIDR of the subnet to get the path to.
     * @return The path to the specified subnet, or null if no path is found.
     */
    public List<Router> getPathTo(String subnetCidr) {
        return routingTable.get(subnetCidr);
    }
}
