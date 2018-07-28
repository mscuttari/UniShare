package it.unishare.common.connection.kademlia;

import java.util.*;

class RoutingTable {

    private final Node node;
    private final List<Bucket> buckets;


    /**
     * Routing table size
     *
     * @param   node        node
     * @param   size        table size
     */
    public RoutingTable(Node node, int size) {
        this.node = node;

        List<Bucket> buckets = new ArrayList<>(size);

        for (int i = 0; i < size; i++)
            buckets.set(i, new Bucket(node));

        this.buckets = Collections.unmodifiableList(buckets);
    }


    /**
     * Get nearest nodes
     *
     * @param   amount      amount of nodes
     * @return  nearest nodes
     */
    public List<NND> getNearestNodes(int amount) {
        List<NND> nodes = new ArrayList<>(amount);

        int bucketsAmount = buckets.size();

        for (int i = bucketsAmount - 1; i >= 0; i--) {
            if (nodes.size() == amount)
                return nodes;

            Bucket bucket = buckets.get(i);
            List<NND> nearestNodes = bucket.getNearestNodes(node.getInfo().getId(), amount);
            nodes.addAll(nearestNodes.subList(0, amount - nodes.size()));
        }

        return nodes;
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
        long xor = this.node.getInfo().getId() ^ node.getId();
        int bucketNumber = Long.numberOfLeadingZeros(xor);
        return buckets.get(bucketNumber);
    }

}
