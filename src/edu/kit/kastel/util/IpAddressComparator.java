package edu.kit.kastel.util;

/**
 * This class provides a method to compare IP addresses and CIDRs.
 * It is used to sort IP addresses and CIDRs in ascending order.
 * @author utsur
 */
public final class IpAddressComparator {

    private IpAddressComparator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Compares two IP addresses.
     * This methode is used as a helper method in the ListSystems class.
     * @param ip1 The first IP address.
     * @param ip2 The second IP address.
     * @return A negative integer, zero, or a positive integer as the first IP address is less than,
     *     equal to, or greater than the second IP address.
     */
    public static int compareIpAddresses(String ip1, String ip2) {
        String[] parts1 = ip1.split("\\.");
        String[] parts2 = ip2.split("\\.");

        for (int i = 0; i < 4; i++) {
            int octet1 = Integer.parseInt(parts1[i]);
            int octet2 = Integer.parseInt(parts2[i]);
            if (octet1 != octet2) {
                return Integer.compare(octet1, octet2);
            }
        }
        return 0;
    }

    /**
     * Compares two CIDRs.
     * This methode is used as a helper method in the ListSubnets class.
     * @param cidr1 The first CIDR.
     * @param cidr2 The second CIDR.
     * @return A negative integer, zero, or a positive integer as the first CIDR is less than, equal to, or greater than the second CIDR.
     */
    public static int compareSubnetCidrs(String cidr1, String cidr2) {
        String[] parts1 = cidr1.split("\\.|/");
        String[] parts2 = cidr2.split("\\.|/");

        int comparison = compareIpAddresses(parts1[0] + "." + parts1[1] + "." + parts1[2] + "." + parts1[3],
            parts2[0] + "." + parts2[1] + "." + parts2[2] + "." + parts2[3]);

        if (comparison == 0) {
            return Integer.compare(Integer.parseInt(parts1[4]), Integer.parseInt(parts2[4]));
        }

        return comparison;
    }
}
