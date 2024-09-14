package edu.kit.kastel.model;

/**
 * Represents a connection between two systems in the network.
 * A connection has a weight that represents the cost of the connection.
 * The weight is used to calculate the shortest path between two systems.
 * Connections between routers are not weighted.
 * @author utsur
 */
public class Connection {
    private final Systems system1;
    private final Systems system2;
    private final Integer weight;

    /**
     * Creates a new connection between two systems with the given weight.
     * @param system1 the first system in the connection.
     * @param system2 the second system in the connection.
     * @param weight the weight of the connection.
     */
    public Connection(Systems system1, Systems system2, Integer weight) {
        this.system1 = system1;
        this.system2 = system2;
        this.weight = weight;
    }

    /**
     * Returns the first system in the connection.
     * @return the first system in the connection.
     */
    public Systems getSystem1() {
        return system1;
    }

    /**
     * Returns the second system in the connection.
     * @return the second system in the connection.
     */
    public Systems getSystem2() {
        return system2;
    }

    /**
     * Returns the weight of the connection.
     * @return the weight of the connection.
     */
    public Integer getWeight() {
        return weight;
    }
}
