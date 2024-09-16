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
            // if both have a router, sort by the name of the first system
            String c1NonRouter = c1.getSystem1() instanceof Router ? c1.getSystem2().getName() : c1.getSystem1().getName();
            String c2NonRouter = c2.getSystem1() instanceof Router ? c2.getSystem2().getName() : c2.getSystem1().getName();
            return c1NonRouter.compareTo(c2NonRouter);
        }
        // if both do not have a router, sort by the name of the first system
        return c1.getSystem1().getName().compareTo(c2.getSystem1().getName());
    }
}
