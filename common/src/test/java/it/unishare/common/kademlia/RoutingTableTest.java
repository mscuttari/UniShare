package it.unishare.common.kademlia;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RoutingTableTest {

    @Test
    public void addNodesTest() {
        TestNode owner = new TestNode();
        RoutingTable routingTable = new RoutingTable(owner, owner.getInfo().getIdLength(), 20);

        TestNode node1 = new TestNode();
        TestNode node2 = new TestNode();
        TestNode node3 = new TestNode();
        TestNode node4 = new TestNode();
        TestNode node5 = new TestNode();

        routingTable.addNode(node1.getInfo());
        routingTable.addNode(node2.getInfo());
        routingTable.addNode(node3.getInfo());
        routingTable.addNode(node4.getInfo());
        routingTable.addNode(node5.getInfo());

        List<NND> allNodes = routingTable.getAllNodes();
        assertTrue(allNodes.contains(node1.getInfo()));
        assertTrue(allNodes.contains(node2.getInfo()));
        assertTrue(allNodes.contains(node3.getInfo()));
        assertTrue(allNodes.contains(node4.getInfo()));
        assertTrue(allNodes.contains(node5.getInfo()));
    }

}
