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
     * Updates the BGP tables of all routers in the network.
     * The BGP tables are updated based on the connections between the routers.
     */
    public void updateBGPTables() {
        boolean changed;
        do {
            changed = false;
            for (Systems system : systemsByIp.values()) {
                if (system instanceof Router) {
                    Router router = (Router) system;
                    Map<String, List<String>> oldTable = router.getRoutingTable();

                    for (Connection conn : connections) {
                        if (conn.getSystem1() == router || conn.getSystem2() == router) {
                            Systems neighbor = conn.getOtherSystem(router);
                            if (neighbor instanceof Router) {
                                router.updateRoutingTable(((Router) neighbor).getRoutingTable());
                            }
                        }
                    }

                    if (!oldTable.equals(router.getRoutingTable())) {
                        changed = true;
                    }
                }
            }
        } while (changed);
    }

    /**
     * Adds a subnet to the network.
     * @param subnet The subnet to add.
     */
    public void addSubnet(Subnet subnet) {
        subnets.add(subnet);
    }

    /**
     * Returns the subnet with the given CIDR.
     * @param cidr The CIDR of the subnet.
     * @return The subnet with the given CIDR.
     */
    public Subnet getSubnetByCidr(String cidr) {
        for (Subnet subnet : subnets) {
            if (subnet.getCidr().equals(cidr)) {
                return subnet;
            }
        }
        return null;
    }

    /**
     * Returns the system with the given IP address.
     * @param ip The IP address of the system.
     * @return The system with the given IP address.
     */
    public Systems getSystemByIp(String ip) {
        return systemsByIp.get(ip);
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
     * Adds a system to the network.
     * @param system The system to add.
     */
    public void addSystem(Systems system) {
        systemsByIp.put(system.getIpAddress(), system);
        systemsByName.put(system.getName(), system);
    }

    /**
     * Removes a system from the network.
     * @param system The system to remove.
     */
    public void removeSystem(Systems system) {
        systemsByIp.remove(system.getIpAddress());
        systemsByName.remove(system.getName());
        connections.removeIf(conn -> conn.getSystem1() == system || conn.getSystem2() == system);
    }

    /**
     * Adds a connection between two systems.
     * @param connection The connection to add.
     */
    public void addConnection(Connection connection) {
        connections.add(connection);
        updateBGPTables();
    }

    /**
     * Checks if a connection exists between two systems.
     * @param system1 The first system.
     * @param system2 The second system.
     * @return True if a connection exists between the two systems, false otherwise.
     */
    public boolean connectionExists(Systems system1, Systems system2) {
        for (Connection conn : connections) {
            if ((conn.getSystem1() == system1 && conn.getSystem2() == system2)
                || (conn.getSystem1() == system2 && conn.getSystem2() == system1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a connection between two systems.
     * @param system1 The first system.
     * @param system2 The second system.
     */
    public void removeConnection(Systems system1, Systems system2) {
        connections.removeIf(conn ->
            (conn.getSystem1() == system1 && conn.getSystem2() == system2)
                || (conn.getSystem1() == system2 && conn.getSystem2() == system1)
        );
        updateBGPTables();
        updateBGPTables();
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

    /*
    private void resetBGPTables() {
        for (Systems system : systemsByIp.values()) {
            if (system instanceof Router) {
                ((Router) system).resetRoutingTable();
            }
        }
    } */

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
