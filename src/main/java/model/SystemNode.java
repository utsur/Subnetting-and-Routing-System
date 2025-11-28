package main.java.model;

/**
 * Represents a system in the network.
 * A system has a name, an IP address, and belongs to a subnet.
 * There are computers and routers that have different functions that are specified in their according classes.
 * @author utsur
 */
public class SystemNode {
    private final String name;
    private final String ipAddress;
    private final Subnet subnet;

    /**
     * Constructs a new System with the specified name, IP address, and subnet.
     * @param name The name of the system.
     * @param ipAddress The IP address of the system.
     * @param subnet The subnet to which the system belongs.
     */
    public SystemNode(String name, String ipAddress, Subnet subnet) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.subnet = subnet;
    }

    /**
     * This method returns the name of the system.
     * @return The name of the system.
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns the IP address of the system.
     * @return The IP address of the system.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * This method returns the subnet to which the system belongs.
     * @return The subnet to which the system belongs.
     */
    public Subnet getSubnet() {
        return subnet;
    }
}
