package edu.kit.kastel.util.comparators;

import edu.kit.kastel.model.Router;
import edu.kit.kastel.model.Systems;

import java.util.Comparator;

/**
 * This class is a comparator for systems.
 * It compares systems based on their type and name.
 * Routers are always first in the list.
 * If two systems are routers, they are compared by their name.
 * If one system is a router, it is always first.
 * If two systems are not routers, they are compared by their name.
 * @author utsur
 */
public class SystemComparator implements Comparator<Systems> {
    @Override
    public int compare(Systems s1, Systems s2) {
        if (s1 instanceof Router && !(s2 instanceof Router)) {
            return -1;
        }
        if (!(s1 instanceof Router) && s2 instanceof Router) {
            return 1;
        }

        return s1.getName().compareTo(s2.getName());
    }
}
