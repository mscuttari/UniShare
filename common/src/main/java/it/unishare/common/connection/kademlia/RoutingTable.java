package it.unishare.common.connection.kademlia;

import java.math.BigInteger;
import java.util.*;

class RoutingTable {

    private final KademliaNode parentNode;
    private final List<Bucket> buckets;


    /**
     * Routing table size
     *
     * @param   parentNode      parent node
     * @param   idLength        table size
     * @param   k               bucket size
     */
    RoutingTable(KademliaNode parentNode, int idLength, int k) {
        this.parentNode = parentNode;

        List<Bucket> buckets = new ArrayList<>(idLength);

        for (int i = 0; i < idLength; i++)
            buckets.add(new Bucket(k, parentNode));

        this.buckets = Collections.unmodifiableList(buckets);
    }


    /**
     * Get nodes count
     *
     * @return  nodes count
     */
    private int getNodesCount() {
        int count = 0;

        for (Bucket bucket : buckets)
            count += bucket.size();

        return count;
    }


    /**
     * Get all the known nodes, sorted by distance
     *
     * @return  unmodifiable list containing all the known nodes, sorted by distance
     */
    public List<NND> getAllNodes() {
        List<NND> result = new ArrayList<>();
        buckets.forEach(result::addAll);

        result.sort((o1, o2) -> {
            BigInteger firstDistance  = o1.getId().distance(parentNode.getInfo().getId());
            BigInteger secondDistance = o2.getId().distance(parentNode.getInfo().getId());
            return firstDistance.compareTo(secondDistance);
        });

        return Collections.unmodifiableList(result);
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

        return new ArrayList<>(nearestNodes.subList(0, Math.min(nearestNodes.size(), amount)));
    }


    /**
     * Add node to the routing table
     *
     * @param   node    node info
     */
    public void addNode(NND node) {
        boolean alreadyKnownNode = getBucket(node).contains(node);

        log("Adding node " + node.getId());
        getBucket(node).add(node);
        log("Node " + node.getId() + " added");

        if (!alreadyKnownNode)
            parentNode.lookup(node.getId(), null);

        updateConnectionStatus();
    }


    /**
     * Remove node from the routing table
     *
     * @param   node    node info
     */
    public void removeNode(NND node) {
        getBucket(node).remove(node);
        updateConnectionStatus();
    }


    /**
     * Get the bucket associated to a node
     *
     * @param   node    node info
     * @return  bucket
     */
    private Bucket getBucket(NND node) {
        NodeId xor = parentNode.getInfo().getId().xor(node.getId());
        int bucketNumber = xor.getLeadingZeros();
        return buckets.get(bucketNumber);
    }


    /**
     * Update node managers status
     *
     * The node can be considered as connected if it has at least one node in its routing table
     */
    private void updateConnectionStatus() {
        parentNode.setConnectionStatus(getNodesCount() > 0);
    }


    /**
     * Log message
     *
     * @param   message     message to be logged
     */
    private void log(String message) {
        parentNode.log(message);
    }

}
