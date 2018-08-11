package it.unishare.common.kademlia;

import java.math.BigInteger;
import java.util.*;

class Bucket {

    private final int size;
    private KademliaNode parentNode;
    private ArrayList<NND> bucket;
    private Queue<NND> queue = new LinkedList<>();


    /**
     * Bucket size
     *
     * @param   k       bucket size
     * @param   node    parent node
     */
    public Bucket(int k, KademliaNode node) {
        this.size = k;
        this.parentNode = node;
        this.bucket = new ArrayList<>(k);
    }


    /**
     * Check if the bucket contains a specific node
     *
     * @param   nnd     network node data of the searched node
     * @return  true if the node is in the bucket; false otherwise
     */
    public boolean contains(NND nnd) {
        return bucket.contains(nnd);
    }


    /**
     * Get the number of nodes in the bucket
     *
     * @return  number of nodes
     */
    public int size() {
        return bucket.size();
    }


    /**
     * Get all the nodes in the bucket
     *
     * @return  unmodifiable collection containing all the nodes in the bucket
     */
    public Collection<NND> getAll() {
        return Collections.unmodifiableCollection(bucket);
    }


    /**
     * Add node to the bucket
     *
     *
     *
     * @param   nnd     node network data
     * @return  true if the node has been added successfully; false otherwise
     */
    public synchronized boolean add(NND nnd) {
        nnd.setLastSeen(Calendar.getInstance());

        // Check if the node ID is already in the bucket
        // In this case, just update its info
        int index = bucket.indexOf(nnd);
        if (index >= 0) {
            NND nodeInfo = bucket.get(index);

            nodeInfo.setAddress(nnd.getAddress());
            nodeInfo.setPort(nnd.getPort());
            nodeInfo.setLastSeen(nnd.getLastSeen());

            bucket.sort(lastSeenComparator());
            return true;
        }

        // Add to queue
        if (!queue.contains(nnd))
            queue.add(nnd);

        // Add the node if there is enough free space in the bucket
        if (bucket.size() < size) {
            NND node = queue.poll();
            final boolean result = bucket.add(node);
            bucket.sort(lastSeenComparator());
            return result;
        }

        // If the bucket is full, ping the least recently seen node in order to see if it's still alive
        NND firstNode = bucket.get(0);
        PingMessage ping = new PingMessage(parentNode.getInfo(), firstNode);
        log("Pinging " + firstNode.getId());

        parentNode.sendMessage(ping, new MessageListener() {
            @Override
            public void onSuccess(Message response) {
                log("Ping response received from " + response.getSource().getId());
                queue.poll();
            }

            @Override
            public void onFailure() {
                log("Can't ping " + ping.getDestination().getId());
                remove(firstNode);
                bucket.add(queue.poll());
                bucket.sort(lastSeenComparator());
            }
        });

        return false;
    }


    /**
     * Add the nodes to the bucket
     *
     * @param   nnds    {@link Collection} of the network node data to be added
     * @return  true if at least one of the node has been added successfully; false otherwise
     */
    public synchronized boolean addAll(Collection<NND> nnds) {
        boolean result = false;

        for (NND nnd : nnds)
            result |= add(nnd);

        return result;
    }


    /**
     * Remove node from the bucket
     *
     * @param   nnd     network node data of the node to be removed
     * @return  true if the node has been removed successfully; false otherwise
     */
    public synchronized boolean remove(NND nnd) {
        return bucket.remove(nnd);
    }


    /**
     * Get comparator by last seen date
     *
     * @return  comparator
     */
    private Comparator<NND> lastSeenComparator() {
        return Comparator.comparing(NND::getLastSeen);
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
        List<NND> allNodes = new ArrayList<>(bucket);

        allNodes.sort((o1, o2) -> {
            BigInteger firstDistance  = o1.getId().distance(nodeId);
            BigInteger secondDistance = o2.getId().distance(nodeId);
            return firstDistance.compareTo(secondDistance);
        });

        if (allNodes.size() == 0)
            return allNodes;

        return new ArrayList<>(allNodes.subList(0, Math.min(allNodes.size(), amount)));
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
