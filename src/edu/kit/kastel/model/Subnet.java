package edu.kit.kastel.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a subnet in the network.
 * A subnet consists of Computers and a router.
 * Computers can only connect to systems in the same subnet.
 * Systems in different subnets can only connect via routers.
 * @author utsur
 */
public class Subnet {
    private final String cidr;
    private final Set<Systems> systems;
    private Router router;

    /**
     * This constructor creates a new Subnet object with the given CIDR.
     * @param cidr The CIDR of the subnet.
     */
    public Subnet(String cidr) {
        this.cidr = cidr;
        this.systems = new HashSet<>();
    }

    /**
     * This method adds a system to the subnet.
     * @param system The system to add.
     */
    public void addSystem(Systems system) {
        systems.add(system);
        if (system instanceof Router) {
            this.router = (Router) system;
        }
    }

    /**
     * This methode gets the CIDR of the subnet.
     * @return the CIDR of the subnet.
     */
    public String getCidr() {
        return cidr;
    }

    /**
     * This method returns all the systems in the subnet.
     * @return a set of systems in the subnet.
     */
    public Set<Systems> getSystems() {
        return new HashSet<>(systems);
    }

    /**
     * This method gets the router of the subnet.
     * @return the router of the subnet.
     */
    public Router getRouter() {
        return router;
    }
}
