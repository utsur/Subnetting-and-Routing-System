package edu.kit.kastel.model;

/**
 * Represents a computer in the network.
 * It has an IP address and belongs to a subnet.
 * It extends the Systems class as it has other functionality as other systems like routers.
 * @author utsur
 */
public class Computer extends Systems {

    /**
     * Creates a new Computer object with the given name, IP address and subnet.
     * This constructor simply calls the superclass constructor.
     * @param name The name of the computer.
     * @param ipAddress The IP address of the computer.
     * @param subnet The subnet to which the computer belongs.
     */
    public Computer(String name, String ipAddress, Subnet subnet) {
        super(name, ipAddress, subnet);
    }
}
