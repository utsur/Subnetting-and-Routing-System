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
import java.util.stream.Collectors;

/**
 * A helper class for finding paths in a network.
 * This class uses the BGP for inter-subnet routing and Dijkstra algorithm for intra-subnet routing.
 * @author utsur
 */
public class PathFinder {
    private static final int INTER_SUBNET_WEIGHT = 1;
    private static final String ERROR_NO_ROUTER = "Error, Source or destination subnet does not have a router.";
    private static final String ERROR_NO_PATH_TO_ROUTER = "Error, No path to source router.";
    private static final String ERROR_NO_BGP_TABLE = "Error, No BGP table for source router.";
    private static final String ERROR_NO_BGP_PATH = "Error, No BGP path found.";
    private static final String ERROR_NO_PATH_FROM_ROUTER = "Error, No path from destination router to destination.";

    private final Network network;
    private final Map<Router, Map<String, List<Router>>> bgpTables;

    /**
     * Creates a new pathfinder with the given network.
     * @param network The network to find paths in.
     */
    public PathFinder(Network network) {

        this.network = network;
        this.bgpTables = new HashMap<>();
        initializeBGPTables();
        exchangeBGPTables();
        debugPrintBGPTables(); // Temporäre Debugging-Ausgabe
    }

    private void initializeBGPTables() {
        for (Subnet subnet : network.getSubnets()) {
            Router router = subnet.getRouter();
            if (router != null) {
                Map<String, List<Router>> routingTable = new HashMap<>();
                routingTable.put(subnet.getCidr(), List.of(router));
                bgpTables.put(router, routingTable);
            } else {
                System.out.println("Warning: Subnet " + subnet.getCidr() + " has no router.");
            }
        }
    }

    private void exchangeBGPTables() {
        boolean changed;
        do {
            changed = false;
            for (Connection conn : network.getConnections()) {
                if (conn.getSystem1() instanceof Router && conn.getSystem2() instanceof Router) {
                    Router router1 = (Router) conn.getSystem1();
                    Router router2 = (Router) conn.getSystem2();
                    changed |= updateBGPTable(router1, router2);
                    changed |= updateBGPTable(router2, router1);
                }
            }
        } while (changed);
    }

    private boolean updateBGPTable(Router router, Router neighbor) {
        boolean changed = false;
        Map<String, List<Router>> routerTable = bgpTables.get(router);
        Map<String, List<Router>> neighborTable = bgpTables.get(neighbor);

        if (routerTable == null || neighborTable == null) {
            System.out.println("Warning: BGP table missing for router " + router.getIpAddress() + " or " + neighbor.getIpAddress());
            return false;
        }

        for (Map.Entry<String, List<Router>> entry : neighborTable.entrySet()) {
            String subnet = entry.getKey();
            List<Router> path = entry.getValue();

            if (!routerTable.containsKey(subnet)
                || (path.size() + 1 < routerTable.get(subnet).size()) || (path.size() + 1 == routerTable.get(subnet).size()
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

    private void debugPrintBGPTables() {
        for (Map.Entry<Router, Map<String, List<Router>>> entry : bgpTables.entrySet()) {
            System.out.println("BGP table for router " + entry.getKey().getIpAddress() + ":");
            for (Map.Entry<String, List<Router>> routeEntry : entry.getValue().entrySet()) {
                System.out.println("  " + routeEntry.getKey() + " -> "
                    + routeEntry.getValue().stream().map(Router::getIpAddress).collect(Collectors.joining(" -> ")));
            }
        }
    }

    /**
     * Finds the shortest path between two systems in the network.
     * This method uses the BGP for inter-subnet routing and Dijkstra algorithm for intra-subnet routing.
     * @param source The source system.
     * @param destination The destination system.
     * @return The shortest path between the source and destination systems.
     */
    public List<Systems> findShortestPath(Systems source, Systems destination) {
        if (source.getSubnet().equals(destination.getSubnet())) {
            return findIntraSubnetPath(source, destination);
        } else {
            return findInterSubnetPath(source, destination);
        }
    }

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

        return null; // no path found
    }

    private List<Systems> findInterSubnetPath(Systems source, Systems destination) {
        List<Systems> path = new ArrayList<>();
        Router sourceRouter = source.getSubnet().getRouter();
        Router destRouter = destination.getSubnet().getRouter();

        if (sourceRouter == null || destRouter == null) {
            System.out.println("Error, Source or destination subnet does not have a router.");
            return null;
        }

        List<Systems> pathToSourceRouter = findIntraSubnetPath(source, sourceRouter);
        if (pathToSourceRouter == null) {
            System.out.println("Error, No path to source router.");
            return null;
        }
        path.addAll(pathToSourceRouter);

        Map<String, List<Router>> sourceRouterTable = bgpTables.get(sourceRouter);
        if (sourceRouterTable == null) {
            System.out.println("Error, No BGP table for source router.");
            return null;
        }

        List<Router> routerPath = sourceRouterTable.get(destination.getSubnet().getCidr());
        if (routerPath == null) {
            System.out.println("Error, No BGP path found.");
            return null;
        }
        path.addAll(routerPath);

        List<Systems> pathFromDestRouter = findIntraSubnetPath(destRouter, destination);
        if (pathFromDestRouter == null) {
            System.out.println("Error, No path from destination router to destination.");
            return null;
        }
        path.addAll(pathFromDestRouter);

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
}
