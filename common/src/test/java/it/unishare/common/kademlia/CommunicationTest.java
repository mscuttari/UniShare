package it.unishare.common.kademlia;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CommunicationTest {

    @Test
    public void pingTest() throws InterruptedException {
        TestNode node1 = new TestNode();
        TestNode node2 = new TestNode();

        node1.bootstrap(node1.getInfo());
        node2.bootstrap(node2.getInfo());
        Thread.sleep(5000);

        node1.ping(node2.getInfo());
        Thread.sleep(5000);

        List<NND> node1KnownNodes = node1.getRoutingTable().getAllNodes();
        List<NND> node2KnownNodes = node2.getRoutingTable().getAllNodes();

        assertTrue(node1KnownNodes.contains(node2.getInfo()));
        assertTrue(node2KnownNodes.contains(node1.getInfo()));
    }

}
