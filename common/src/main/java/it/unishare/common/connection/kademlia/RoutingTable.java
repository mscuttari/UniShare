package it.unishare.common.connection.kademlia;

import java.math.BigInteger;
import java.util.*;

class RoutingTable {

    private final Node node;
    private final List<Bucket> buckets;


    /**
     * Routing table size
     *
     * @param   node            node
     * @param   idLength        table size
     * @param   k               bucket size
     */
    public RoutingTable(Node node, int idLength, int k) {
        this.node = node;

        List<Bucket> buckets = new ArrayList<>(idLength);

        for (int i = 0; i < idLength; i++)
            buckets.add(new Bucket(k, node));

        this.buckets = Collections.unmodifiableList(buckets);
    }


    /**
     * Get nearest nodes to a node ID
     *
     * @param   nodeId      node ID to search neighbours for
     * @param   amount      amount of desired nearest nodes
     *
     * @return  nearest nodes
     */
    public List<NND> getNearestNodes(NodeId nodeId, int amount) {
        List<NND> nearestNodes = new ArrayList<>(amount);
        int bucketsAmount = buckets.size();

        for (int i = bucketsAmount - 1; i >= 0; i--) {
            if (nearestNodes.size() >= amount)
                return nearestNodes;

            Bucket bucket = buckets.get(i);
            List<NND> bucketNearestNodes = bucket.getNearestNodes(nodeId, amount);
            nearestNodes.addAll(bucketNearestNodes);
        }

        return nearestNodes.subList(0, Math.min(nearestNodes.size(), amount));
    }


    /**
     * Add node to the routing table
     *
     * @param   node    node info
     */
    public void addNode(NND node) {
        getBucket(node).add(node);
    }


    /**
     * Remove node from the routing table
     *
     * @param   node    node info
     */
    public void removeNode(NND node) {
        getBucket(node).remove(node);
    }


    /**
     * Get the bucket associated to a node
     *
     * @param   node    node info
     * @return  bucket
     */
    private Bucket getBucket(NND node) {
        NodeId xor = this.node.getInfo().getId().xor(node.getId());
        int bucketNumber = xor.getLeadingZeros();
        return buckets.get(bucketNumber);
    }

}
