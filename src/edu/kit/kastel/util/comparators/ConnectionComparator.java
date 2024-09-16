package edu.kit.kastel.util.comparators;

import edu.kit.kastel.model.Connection;
import edu.kit.kastel.model.Router;

import java.util.Comparator;

/**
 * This class is a comparator for connections.
 * It compares connections based on whether they contain a router and the name of the first system.
 * Connections with routers are always first in the list.
 * If two connections contain a router, they are compared by the name of the first system.
 * If one connection contains a router, it is always first.
 * If two connections do not contain a router, they are compared by the name of the first system.
 * @author utsur
 */
public class ConnectionComparator implements Comparator<Connection> {
    @Override
    public int compare(Connection c1, Connection c2) {
        boolean c1HasRouter = c1.getSystem1() instanceof Router || c1.getSystem2() instanceof Router;
        boolean c2HasRouter = c2.getSystem1() instanceof Router || c2.getSystem2() instanceof Router;

        if (c1HasRouter && !c2HasRouter) {
            return -1;
        }
        if (!c1HasRouter && c2HasRouter) {
            return 1;
        }

        if (c1HasRouter && c2HasRouter) {
            // both have a router, sort by weight.
            return Integer.compare(c1.getWeight(), c2.getWeight());
        }

        // neither has a router, sort by name and then by weight.
        int nameCompare = c1.getSystem1().getName().compareTo(c2.getSystem1().getName());
        if (nameCompare != 0) {
            return nameCompare;
        }
        return Integer.compare(c1.getWeight(), c2.getWeight());
    }
}
