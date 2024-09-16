package edu.kit.kastel.model;

/**
 * Represents a computer in the network.
 * It has an IP address and belongs to a subnet.
 * It extends the System class.
 * @author utsur
 */
public class Computer extends Systems {

    /**
     * This constructor creates a new Computer object with the given IP address and subnet.
     * @param name The name of the computer.
     * @param ipAddress The IP address of the computer.
     * @param subnet The subnet to which the computer belongs.
     */
    public Computer(String name, String ipAddress, Subnet subnet) {
        super(name, ipAddress, subnet);
    }
}
