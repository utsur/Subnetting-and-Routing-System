package edu.kit.kastel.util;

import edu.kit.kastel.model.Connection;
import edu.kit.kastel.model.Network;
import edu.kit.kastel.model.Systems;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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
     * Finds the shortest path between two systems in the network.
     * This method uses the Dijkstra algorithm to find the shortest path.
     * The weight of the connections is used as the distance between systems.
     * If no weight is specified, the distance is set to 1.
     * If no path is found, null is returned.
     * @param source The source system.
     * @param destination The destination system.
     * @return The shortest path between the source and destination systems.
     */
    public List<Systems> findShortestPath(Systems source, Systems destination) {
        Map<Systems, Integer> distances = new HashMap<>();
        Map<Systems, Systems> previousSystems = new HashMap<>();
        PriorityQueue<Systems> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (Systems system : network.getSystems().values()) {
            distances.put(system, Integer.MAX_VALUE);
        }
        distances.put(source, 0);
        queue.add(source);

        while (!queue.isEmpty()) {
            Systems current = queue.poll();
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

                if (neighbor != null) {
                    int weight = connection.getWeight() != null ? connection.getWeight() : 1;
                    int alternativeDistance = distances.get(current) + weight;

                    if (alternativeDistance < distances.get(neighbor)) {
                        distances.put(neighbor, alternativeDistance);
                        previousSystems.put(neighbor, current);
                        queue.remove(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }

        return null; // No path found
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
