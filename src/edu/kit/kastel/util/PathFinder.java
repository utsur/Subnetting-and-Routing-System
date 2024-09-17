package edu.kit.kastel.util;

import edu.kit.kastel.model.Connection;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A helper class for finding paths in a network.
 * This class uses the Dijkstra algorithm to find the shortest path between systems in the network.
 * @author utsur
 */
public class PathFinder {
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
                break;  // No path found
            }
            unvisitedSystems.remove(current);

            if (current.equals(destination)) {
                return reconstructPath(previousSystems, destination);
            }

            for (Connection connection : network.getConnections()) {
                Systems neighbor = null;
                if (connection.getSystem1().equals(current)) {
                    neighbor = connection.getSystem2();
                } else if (connection.getSystem2().equals(current)) {
                    neighbor = connection.getSystem1();
                }

                if (neighbor != null && unvisitedSystems.contains(neighbor)) {
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

    private List<Systems> reconstructPath(Map<Systems, Systems> previousSystems, Systems destination) {
        LinkedList<Systems> path = new LinkedList<>();
        Systems current = destination;

        while (current != null) {
            path.addFirst(current);
            current = previousSystems.get(current);
        }

        return path;
    }
}
