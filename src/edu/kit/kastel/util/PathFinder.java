package edu.kit.kastel.util;

import edu.kit.kastel.model.Connection;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Router;
import edu.kit.kastel.model.Systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class for finding paths in a network.
 * This class uses the Dijkstra algorithm for intra-subnet routing and BGP tables for inter-subnet routing.
 * @author utsur
 */
public class PathFinder {
    private static final int INITIAL_DISTANCE = 0;
    private static final int MAX_DISTANCE = Integer.MAX_VALUE;
    private final Network network;

    /**
     * Creates a new pathfinder with the given network.
     * @param network The network to find paths in.
     */
    public PathFinder(Network network) {
        this.network = network;
    }

    /**
     * Finds the shortest path between the source and destination systems.
     * If no path is found, null is returned.
     * @param source The source system.
     * @param destination The destination system.
     * @return The shortest path between the systems, or null if no path is found.
     */
    public List<Systems> findShortestPath(Systems source, Systems destination) {
        if (source.getSubnet().equals(destination.getSubnet())) {
            return findPathInSubnet(source, destination);
        } else {
            return findPathAcrossSubnets(source, destination);
        }
    }

    private List<Systems> findPathInSubnet(Systems source, Systems destination) {
        Map<Systems, Integer> distances = new HashMap<>();
        Map<Systems, Systems> previousSystems = new HashMap<>();
        List<Systems> unvisitedSystems = new ArrayList<>(source.getSubnet().getSystems());

        for (Systems system : unvisitedSystems) {
            distances.put(system, MAX_DISTANCE);
        }
        distances.put(source, INITIAL_DISTANCE);

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
                if (neighbor.getSubnet().equals(source.getSubnet())) {
                    int alternativeDistance = distances.get(current) + connection.getWeight();
                    if (alternativeDistance < distances.get(neighbor)) {
                        distances.put(neighbor, alternativeDistance);
                        previousSystems.put(neighbor, current);
                    }
                }
            }
        }

        return null; // No path found
    }

    private List<Systems> findPathAcrossSubnets(Systems source, Systems destination) {
        List<Systems> path = new ArrayList<>();
        // Find path from source to source subnets router
        List<Systems> sourceToRouter = findPathInSubnet(source, source.getSubnet().getRouter());
        if (sourceToRouter == null) {
            return null;
        }
        path.addAll(sourceToRouter);
        // Find path between routers using BGP tables
        Router currentRouter = source.getSubnet().getRouter();
        Router destinationRouter = destination.getSubnet().getRouter();

        while (!currentRouter.equals(destinationRouter)) {
            List<String> routerPath = currentRouter.getRoutingTable().get(destination.getSubnet().getCidr());
            if (routerPath == null || routerPath.size() < 2) {
                return null; // No path found
            }
            String nextRouterIp = routerPath.get(1); // Get the next hop
            Router nextRouter = (Router) network.getSystemByIp(nextRouterIp);
            if (nextRouter == null) {
                return null; // Invalid next hop
            }
            path.add(nextRouter);
            currentRouter = nextRouter;
        }
        // Find path from destination subnets router to destination
        List<Systems> routerToDestination = findPathInSubnet(destination.getSubnet().getRouter(), destination);
        if (routerToDestination == null) {
            return null;
        }
        path.addAll(routerToDestination.subList(1, routerToDestination.size())); // Exclude the router as it's already in the path

        return path;
    }

    private Systems getMinDistanceSystem(List<Systems> systems, Map<Systems, Integer> distances) {
        Systems minSystem = null;
        int minDistance = MAX_DISTANCE;

        for (Systems system : systems) {
            int distance = distances.get(system);
            if (distance < minDistance) {
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
