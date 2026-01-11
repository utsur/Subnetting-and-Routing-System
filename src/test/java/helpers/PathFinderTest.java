package helpers;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PathFinderTest {
    private Network network;
    private Subnet subnet1;
    private Computer c1, c2, c3;
    private PathFinder pathFinder;

    @BeforeEach
    public void setUp() {
        network = new Network();
        subnet1 = new Subnet("192.168.1.0/24");
        network.addSubnet(subnet1);

        c1 = new Computer("C1", "192.168.1.1", subnet1);
        c2 = new Computer("C2", "192.168.1.2", subnet1);
        c3 = new Computer("C3", "192.168.1.3", subnet1);

        subnet1.addSystem(c1);
        subnet1.addSystem(c2);
        subnet1.addSystem(c3);

        network.addSystem(c1);
        network.addSystem(c2);
        network.addSystem(c3);

        pathFinder = new PathFinder(network);
    }

    @Test
    public void testShortestPathDirect() {
        Connection conn = new Connection(c1, c2, 10);
        network.addConnection(conn);

        List<SystemNode> path = pathFinder.findShortestPath(c1, c2);
        assertNotNull(path);
        assertEquals(2, path.size());
        assertEquals(c1, path.get(0));
        assertEquals(c2, path.get(1));
    }

    @Test
    public void testShortestPathIndirect() {
        network.addConnection(new Connection(c1, c2, 100));
        network.addConnection(new Connection(c1, c3, 10));
        network.addConnection(new Connection(c3, c2, 10));

        List<SystemNode> path = pathFinder.findShortestPath(c1, c2);
        assertNotNull(path);
        assertEquals(3, path.size());
        assertEquals(c1, path.get(0));
        assertEquals(c3, path.get(1));
        assertEquals(c2, path.get(2));
    }

    @Test
    public void testNoPath() {
        List<SystemNode> path = pathFinder.findShortestPath(c1, c2);
        assertTrue(path.isEmpty());
    }
}
