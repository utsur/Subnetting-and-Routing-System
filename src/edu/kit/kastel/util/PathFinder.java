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
 * This class uses the Dijkstra algorithm to find the shortest path between systems in the network.
 * @author utsur
 */
public class PathFinder {
    private static final int INTER_SUBNET_WEIGHT = 1;
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
     * This method uses the Dijkstra algorithm to find the shortest path.
     * If no path is found, null is returned.
     * @param source The source system.
     * @param destination The destination system.
     * @return The shortest path between the systems, or null if no path is found.
     */
    public List<Systems> findShortestPath(Systems source, Systems destination) {
        if (source.getSubnet().equals(destination.getSubnet())) {
            return findIntraSubnetPath(source, destination);
        } else {
            return findInterSubnetPath(source, destination);
        }
    }

    /*
    public List<Systems> findShortestPath(Systems source, Systems destination) {
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

            List<Connection> connections = getConnections(current);
            for (Connection connection : connections) {
                Systems neighbor = connection.getOtherSystem(current);
                if (unvisitedSystems.contains(neighbor)) {
                    int weight = connection.getWeight() != null ? connection.getWeight() : 1;
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
                break; // No path found
            }
            unvisitedSystems.remove(current);

            if (current.equals(destination)) {
                return reconstructPath(previousSystems, destination);
            }

            List<Connection> connections = getConnections(current);
            for (Connection connection : connections) {
                Systems neighbor = connection.getOtherSystem(current);
                if (unvisitedSystems.contains(neighbor) && neighbor.getSubnet().equals(source.getSubnet())) {
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


    private List<Systems> findInterSubnetPath(Systems source, Systems destination) {
        List<Systems> path = new ArrayList<>();
        Router sourceRouter = source.getSubnet().getRouter();
        Router destRouter = destination.getSubnet().getRouter();

        List<Systems> pathToSourceRouter = findIntraSubnetPath(source, sourceRouter);
        if (pathToSourceRouter == null) {
            return null; // No path to source router
        }
        path.addAll(pathToSourceRouter);

        List<Router> routerPath = findShortestRouterPath(sourceRouter, destRouter);
        if (routerPath == null) {
            return null; // No path between routers
        }
        path.addAll(new ArrayList<>(routerPath));

        List<Systems> pathFromDestRouter = findIntraSubnetPath(destRouter, destination);
        if (pathFromDestRouter == null) {
            return null; // No path from dest router to destination
        }
        path.addAll(pathFromDestRouter);

        return path;
    }

    private Systems getMinDistanceSystem(List<Systems> systems, Map<Systems, Integer> distances) {
        Systems minSystem = null;
        int minDistance = Integer.MAX_VALUE;

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

    private List<Router> findShortestRouterPath(Router source, Router destination) {
        Map<Router, Router> previousRouter = new HashMap<>();
        Map<Router, Integer> distances = new HashMap<>();
        List<Router> unvisited = new ArrayList<>();

        for (Subnet subnet : network.getSubnets()) {
            Router router = subnet.getRouter();
            distances.put(router, router == source ? 0 : Integer.MAX_VALUE);
            unvisited.add(router);
        }

        while (!unvisited.isEmpty()) {
            Router current = getMinDistanceRouter(unvisited, distances);
            if (current == null) {
                break; // No more reachable routers
            }
            unvisited.remove(current);

            if (current == destination) {
                return reconstructRouterPath(source, destination, previousRouter);
            }

            for (Connection conn : network.getConnections()) {
                if (conn.getSystem1() == current && conn.getSystem2() instanceof Router) {
                    Router neighbor = (Router) conn.getSystem2();
                    if (unvisited.contains(neighbor)) {
                        int newDist = distances.get(current) + INTER_SUBNET_WEIGHT;
                        if (newDist < distances.get(neighbor)) {
                            distances.put(neighbor, newDist);
                            previousRouter.put(neighbor, current);
                        }
                    }
                }
            }
        }

        return null; // No path found
    }

    private Router getMinDistanceRouter(List<Router> routers, Map<Router, Integer> distances) {
        Router minRouter = null;
        int minDistance = Integer.MAX_VALUE;
        for (Router router : routers) {
            Integer distance = distances.get(router);
            if (distance == null) {
                continue; // Skip routers with no distance information
            }
            if (minRouter == null || distance < minDistance ||
                (distance.equals(minDistance) && router.getIpAddress().compareTo(minRouter.getIpAddress()) < 0)) {
                minDistance = distance;
                minRouter = router;
            }
        }
        return minRouter; // This could still be null if no valid router is found
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

    private List<Router> reconstructRouterPath(Router source, Router destination, Map<Router, Router> previousRouter) {
        List<Router> path = new ArrayList<>();
        Router current = destination;

        while (current != null) {
            path.add(0, current);
            if (current.equals(source)) {
                break;
            }
            current = previousRouter.get(current);
        }

        return path;
    }

    /*
    private List<Systems> reconstructPath(Map<Systems, Systems> previousSystems, Systems destination) {
        List<Systems> path = new ArrayList<>();
        Systems current = destination;

        while (current != null) {
            path.add(0, current);
            current = previousSystems.get(current);
        }

        return path;
    }
    */
}
