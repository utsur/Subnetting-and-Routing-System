package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a network of subnets and systems.
 * A network consists of subnets, systems and connections between systems.
 * The components of the network are specified in their respective classes.
 * @author utsur
 */
public class Network {
    private final List<Subnet> subnets;
    private final Map<String, Systems> systemsByIp;
    private final Map<String, Systems> systemsByName;
    private final Set<Connection> connections;

    /**
     * Creates a new network.
     * The network is initially empty.
     * Subnets, systems and connections can with the commands by the user.
     */
    public Network() {
        this.subnets = new ArrayList<>();
        this.systemsByIp = new HashMap<>();
        this.systemsByName = new HashMap<>();
        this.connections = new HashSet<>();
    }

    /**
     * Adds a subnet to the network.
     * @param subnet The subnet to add.
     */
    public void addSubnet(Subnet subnet) {
        subnets.add(subnet);
    }

    /**
     * Adds a system to the network.
     * @param system The system to add.
     */
    public void addSystem(Systems system) {
        systemsByIp.put(system.getIpAddress(), system);
        systemsByName.put(system.getName(), system);
    }

    /**
     * Adds a connection between two systems.
     * @param connection The connection to add.
     */
    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    /**
     * Returns the subnets of the network.
     * @return The subnets of the network.
     */
    public List<Subnet> getSubnets() {
        return new ArrayList<>(subnets);
    }

    /**
     * Returns the systems of the network.
     * @return The systems of the network.
     */
    public Map<String, Systems> getSystems() {
        return new HashMap<>(systemsByIp);
    }

    /**
     * Returns the connections of the network.
     * @return The connections of the network.
     */
    public Set<Connection> getConnections() {
        return new HashSet<>(connections);
    }

    /**
     * Returns the system by its name.
     * @param name The name of the system.
     * @return The system with the given name.
     */
    public Systems getSystemByName(String name) {
        return systemsByName.get(name);
    }

    /**
     * This method updates the BGP tables of all routers in the network.
     * It is called whenever a system is added or removed from the network.
     * The BGP tables are updated based on the connections between the routers.
     * @param other The network to update from.
     */
    public void updateFrom(Network other) {
        this.subnets.clear();
        this.subnets.addAll(other.subnets);
        this.systemsByIp.clear();
        this.systemsByIp.putAll(other.systemsByIp);
        this.systemsByName.clear();
        this.systemsByName.putAll(other.systemsByName);
        this.connections.clear();
        this.connections.addAll(other.connections);
    }
}
