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
 * A helper class for finding paths in a network.
 * This class uses the BGP for inter-subnet routing and Dijkstra algorithm for intra-subnet routing.
 * @author utsur
 */
public class PathFinder {
    private static final int INTER_SUBNET_WEIGHT = 1;
    private final Network network;
    private final Map<Router, BGPRouter> bgpRouters;

    /**
     * Creates a new pathfinder with the given network.
     * @param network The network to find paths in.
     */
    public PathFinder(Network network) {
        this.network = network;
        this.bgpRouters = new HashMap<>();
        initializeBGPRouters();
        exchangeBGPTables();
    }

    private void initializeBGPRouters() {
        for (Subnet subnet : network.getSubnets()) {
            Router router = subnet.getRouter();
            if (router != null) {
                bgpRouters.put(router, new BGPRouter(router));
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
                    BGPRouter bgpRouter1 = bgpRouters.get(router1);
                    BGPRouter bgpRouter2 = bgpRouters.get(router2);

                    if (bgpRouter1 != null && bgpRouter2 != null) {
                        int oldSize1 = bgpRouter1.getRoutingTable().size();
                        int oldSize2 = bgpRouter2.getRoutingTable().size();

                        bgpRouter1.updateRoutingTable(bgpRouter2);
                        bgpRouter2.updateRoutingTable(bgpRouter1);

                        if (bgpRouter1.getRoutingTable().size() > oldSize1 || bgpRouter2.getRoutingTable().size() > oldSize2) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);
    }

    /**
     * Finds the shortest path between the source and destination systems.
     * @param source The source system.
     * @param destination The destination system.
     * @return The shortest path between the systems, or null if no path is found.
     */
    public List<Systems> findShortestPath(Systems source, Systems destination) {
        if (source == null || destination == null) {
            System.out.println("Error, Invalid source or destination.");
            return null;
        }
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

        return null;
    }

    private List<Systems> findInterSubnetPath(Systems source, Systems destination) {
        List<Systems> path = new ArrayList<>();
        Router sourceRouter = source.getSubnet().getRouter();
        Router destRouter = destination.getSubnet().getRouter();

        if (sourceRouter == null || destRouter == null) {
            System.out.println("Error, Invalid source or destination router.");
            return null;
        }

        List<Systems> pathToSourceRouter = findIntraSubnetPath(source, sourceRouter);
        if (pathToSourceRouter == null) {
            System.out.println("Error, No path to source router.");
            return null;
        }
        path.addAll(pathToSourceRouter);

        BGPRouter bgpSourceRouter = bgpRouters.get(sourceRouter);
        if (bgpSourceRouter == null) {
            System.out.println("Error, BGP router not found for source router.");
            return null;
        }

        List<Router> routerPath = bgpSourceRouter.getPathTo(destination.getSubnet().getCidr());
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
