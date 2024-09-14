package edu.kit.kastel.model;

/**
 * Represents a router in the network.
 * @author utsur
 */
public class Router extends Systems {

    /**
     * This constructor creates a new Router object with the given IP address and subnet.
     * @param name The name of the router.
     * @param ipAddress The IP address of the router.
     * @param subnet The subnet to which the router belongs.
     */
    public Router(String name, String ipAddress, Subnet subnet) {
        super(name, ipAddress, subnet);
    }
}
