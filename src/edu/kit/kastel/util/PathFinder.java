package edu.kit.kastel.util;

import edu.kit.kastel.model.Connection;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Router;
import edu.kit.kastel.model.Subnet;
import edu.kit.kastel.model.Systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PathFinder class is responsible for finding the shortest path between two systems in a network.
 * It uses Dijkstra's algorithm to find the shortest path between systems in the same subnet and
 * BGP tables to find the shortest path between systems in different subnets.
 * @author utsur
 */
public class PathFinder {
    private static final int INTER_SUBNET_WEIGHT = 1;
    private final Network network;
    private final Map<Router, Map<String, List<Router>>> bgpTables;

    /**
     * Constructor for PathFinder class.
     * @param network Network object representing the network.
     */
    public PathFinder(Network network) {
        this.network = network;
        this.bgpTables = new HashMap<>();
        initializeBGPTables();
        exchangeBGPTables();
    }

    private void initializeBGPTables() {
        for (Subnet subnet : network.getSubnets()) {
            Router router = subnet.getRouter();
            if (router != null) {
                Map<String, List<Router>> routingTable = new HashMap<>();
                routingTable.put(subnet.getCidr(), new ArrayList<>(List.of(router)));
                bgpTables.put(router, routingTable);
            }
        }
    }

    private void exchangeBGPTables() {
        boolean changed;
        do {
            changed = false;
            for (Router router : bgpTables.keySet()) {
                for (Connection conn : network.getConnections()) {
                    if (conn.getSystem1() instanceof Router && conn.getSystem2() instanceof Router) {
                        Router neighbor = (Router) (conn.getSystem1() == router ? conn.getSystem2() : conn.getSystem1());
                        changed |= updateBGPTable(router, neighbor);
                    }
                }
            }
        } while (changed);
    }

    private boolean updateBGPTable(Router router, Router neighbor) {
        boolean changed = false;
        Map<String, List<Router>> routerTable = bgpTables.get(router);
        Map<String, List<Router>> neighborTable = bgpTables.get(neighbor);

        for (Map.Entry<String, List<Router>> entry : neighborTable.entrySet()) {
            String subnet = entry.getKey();
            List<Router> path = new ArrayList<>(entry.getValue());

            if (!routerTable.containsKey(subnet) || (path.size() + 1 < routerTable.get(subnet).size())
                || (path.size() + 1 == routerTable.get(subnet).size()
                && neighbor.getIpAddress().compareTo(routerTable.get(subnet).get(0).getIpAddress()) < 0)) {

                List<Router> newPath = new ArrayList<>();
                newPath.add(neighbor);
                newPath.addAll(path);
                routerTable.put(subnet, newPath);
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Find the shortest path between two systems in the network.
     * @param source the source system.
     * @param destination the destination system.
     * @return the shortest path between the source and destination systems.
     */
    public List<Systems> findShortestPath(Systems source, Systems destination) {
        if (source.getSubnet().equals(destination.getSubnet())) {
            return findIntraSubnetPath(source, destination);
        } else {
            List<Systems> bgpPath = findInterSubnetPath(source, destination);
            if (bgpPath != null) {
                return bgpPath;
            }
            // Fallback to original Dijkstra if BGP path is not found
            return findGlobalShortestPath(source, destination);
        }
    }

    /**
     * Find the shortest path between two systems in the same subnet.
     * Uses Dijkstra's algorithm to find the shortest path.
     * @param source the source system.
     * @param destination the destination system.
     * @return the shortest path between the source and destination systems.
     */
    private List<Systems> findIntraSubnetPath(Systems source, Systems destination) {
        Map<Systems, Integer> distances = new HashMap<>();
        Map<Systems, Systems> previousSystems = new HashMap<>();
        List<Systems> unvisitedSystems = new ArrayList<>(source.getSubnet().getSystems());

        for (Systems system : unvisitedSystems) {
            distances.put(system, Integer.MAX_VALUE);
        }
        distances.put(source, 0);

        while (!unvisitedSystems.isEmpty()) {
            Systems current = getMinDistanceSystem(unvisitedSystems, distances);
            if (current == null) {
                break;
            }
            unvisitedSystems.remove(current);

            if (current.equals(destination)) {
                return reconstructPath(previousSystems, destination);
            }

            for (Connection connection : getConnections(current)) {
                Systems neighbor = connection.getOtherSystem(current);
                if (unvisitedSystems.contains(neighbor)) {
                    int weight = connection.getWeight() != null ? connection.getWeight() : INTER_SUBNET_WEIGHT;
                    int alternativeDistance = distances.get(current) + weight;

                    if (alternativeDistance < distances.get(neighbor)) {
                        distances.put(neighbor, alternativeDistance);
                        previousSystems.put(neighbor, current);
                    }
                }
            }
        }

        return null;
    }

    private List<Systems> findInterSubnetPath(Systems source, Systems destination) {
        List<Systems> path = new ArrayList<>();
        Router sourceRouter = source.getSubnet().getRouter();
        Router destRouter = destination.getSubnet().getRouter();

        if (sourceRouter == null || destRouter == null) {
            return null;
        }

        path.addAll(findIntraSubnetPath(source, sourceRouter));

        Map<String, List<Router>> sourceRouterTable = bgpTables.get(sourceRouter);
        if (sourceRouterTable == null) {
            return null;
        }

        List<Router> routerPath = sourceRouterTable.get(destination.getSubnet().getCidr());
        if (routerPath == null) {
            return null;
        }

        for (Router router : routerPath) {
            path.add(router);
            if (router == destRouter) {
                break;
            }
        }

        path.addAll(findIntraSubnetPath(destRouter, destination));

        return path;
    }

    private Systems getMinDistanceSystem(List<Systems> systems, Map<Systems, Integer> distances) {
        Systems minSystem = null;
        int minDistance = Integer.MAX_VALUE;

        for (Systems system : systems) {
            Integer distance = distances.get(system);
            if (distance != null && distance < minDistance) {
                minDistance = distance;
                minSystem = system;
            }
        }

        return minSystem;
    }

    private List<Connection> getConnections(Systems system) {
        List<Connection> connections = new ArrayList<>();
        for (Connection connection : network.getConnections()) {
            if (connection.getSystem1().equals(system) || connection.getSystem2().equals(system)) {
                connections.add(connection);
            }
        }
        return connections;
    }

    private List<Systems> reconstructPath(Map<Systems, Systems> previousSystems, Systems destination) {
        List<Systems> path = new ArrayList<>();
        Systems current = destination;

        while (current != null) {
            path.add(0, current);
            current = previousSystems.get(current);
        }

        return path;
    }

    private List<Systems> findGlobalShortestPath(Systems source, Systems destination) {
        Map<Systems, Integer> distances = new HashMap<>();
        Map<Systems, Systems> previousSystems = new HashMap<>();
        List<Systems> unvisitedSystems = new ArrayList<>(network.getSystems().values());

        for (Systems system : unvisitedSystems) {
            distances.put(system, Integer.MAX_VALUE);
        }
        distances.put(source, 0);

        while (!unvisitedSystems.isEmpty()) {
            Systems current = getMinDistanceSystem(unvisitedSystems, distances);
            if (current == null) {
                break; // No path found
            }
            unvisitedSystems.remove(current);

            if (current.equals(destination)) {
                return reconstructPath(previousSystems, destination);
            }

            for (Connection connection : getConnections(current)) {
                Systems neighbor = connection.getOtherSystem(current);
                if (unvisitedSystems.contains(neighbor)) {
                    int weight = connection.getWeight() != null ? connection.getWeight() : INTER_SUBNET_WEIGHT;
                    int alternativeDistance = distances.get(current) + weight;

                    if (alternativeDistance < distances.get(neighbor)) {
                        distances.put(neighbor, alternativeDistance);
                        previousSystems.put(neighbor, current);
                    }
                }
            }
        }

        return null; // No path found
    }
}
