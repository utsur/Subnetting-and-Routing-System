package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class NetworkTest {
    private Network network;
    private Subnet subnet1, subnet2;
    private Router r1, r2;

    @BeforeEach
    public void setUp() {
        network = new Network();
        subnet1 = new Subnet("10.0.1.0/24");
        subnet2 = new Subnet("10.0.2.0/24");
        network.addSubnet(subnet1);
        network.addSubnet(subnet2);

        r1 = new Router("R1", "10.0.1.1", subnet1);
        r2 = new Router("R2", "10.0.2.1", subnet2);
        subnet1.addSystem(r1);
        subnet2.addSystem(r2);
        network.addSystem(r1);
        network.addSystem(r2);
    }

    @Test
    public void testBGPPropagatesRoutes() {
        network.addConnection(new Connection(r1, r2, null));
        
        // R1 should now know about subnet2
        assertTrue(r1.getRoutingTable().containsKey("10.0.2.0/24"));
        List<String> path = r1.getRoutingTable().get("10.0.2.0/24");
        assertEquals(2, path.size());
        assertEquals("10.0.1.1", path.get(0));
        assertEquals("10.0.2.1", path.get(1));
    }

    @Test
    public void testAdjacencyListUpdates() {
        Connection conn = new Connection(r1, r2, null);
        network.addConnection(conn);
        assertEquals(1, network.getConnections(r1).size());
        
        network.removeConnection(r1, r2);
        assertEquals(0, network.getConnections(r1).size());
        // Routing table should be reset
        assertFalse(r1.getRoutingTable().containsKey("10.0.2.0/24"));
    }
}
