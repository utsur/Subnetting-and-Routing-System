package main.java.util;

/**
 * This class provides a method to compare IP addresses and CIDRs.
 * It is used to sort IP addresses and CIDRs in ascending order.
 * @author utsur
 */
public final class IpAddressComparator {
    private static final String DOT = ".";
    private static final String DOT_REGEX = "\\.";
    private static final String CIDR_SPLIT_REGEX = "\\.|/";
    private static final int IP_OCTET_COUNT = 4;
    private static final int CIDR_SUBNET_MASK_INDEX = 4;
    private static final int FIRST_OCTET_INDEX = 0;
    private static final int SECOND_OCTET_INDEX = 1;
    private static final int THIRD_OCTET_INDEX = 2;
    private static final int FOURTH_OCTET_INDEX = 3;

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
        String[] parts1 = ip1.split(DOT_REGEX);
        String[] parts2 = ip2.split(DOT_REGEX);

        for (int i = 0; i < IP_OCTET_COUNT; i++) {
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
    public static int compareSubnetCIDRs(String cidr1, String cidr2) {
        String[] parts1 = cidr1.split(CIDR_SPLIT_REGEX);
        String[] parts2 = cidr2.split(CIDR_SPLIT_REGEX);

        int comparison = compareIpAddresses(joinIpParts(parts1), joinIpParts(parts2));

        if (comparison == 0) {
            return Integer.compare(Integer.parseInt(parts1[CIDR_SUBNET_MASK_INDEX]),
                Integer.parseInt(parts2[CIDR_SUBNET_MASK_INDEX]));
        }

        return comparison;
    }

    private static String joinIpParts(String[] parts) {
        return String.join(DOT,
            parts[FIRST_OCTET_INDEX],
            parts[SECOND_OCTET_INDEX],
            parts[THIRD_OCTET_INDEX],
            parts[FOURTH_OCTET_INDEX]);
    }
}
