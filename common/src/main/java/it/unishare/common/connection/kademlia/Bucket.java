package it.unishare.common.connection.kademlia;

import it.unishare.common.connection.kademlia.rpc.Message;
import it.unishare.common.connection.kademlia.rpc.Ping;
import it.unishare.common.utils.LogUtils;
import it.unishare.common.utils.RandomGaussian;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

class Bucket extends ArrayList<NND> {

    private int size;
    private Node parentNode;
    private Queue<NND> queue = new LinkedList<>();
    private RandomGaussian randomGaussian = new RandomGaussian();


    /**
     * Bucket size
     *
     * @param   k       bucket size
     * @param   node    parent node
     */
    public Bucket(int k, Node node) {
        super(k);

        this.size = k;
        this.parentNode = node;
    }


    @Override
    public synchronized NND set(int index, NND element) {
        throw new UnsupportedOperationException();
    }


    @Override
    public synchronized void add(int index, NND element) {
        throw new UnsupportedOperationException();
    }


    @Override
    public synchronized boolean add(NND nnd) {
        nnd.setLastSeen(Calendar.getInstance());

        // Check if the node ID is already in the bucket
        // In this case, just update its info
        int index = indexOf(nnd);
        if (index >= 0) {
            NND nodeInfo = get(index);

            nodeInfo.setAddress(nnd.getAddress());
            nodeInfo.setPort(nnd.getPort());
            nodeInfo.setLastSeen(nnd.getLastSeen());

            sort(lastSeenComparator());
            return true;
        }

        // Add to queue
        if (!queue.contains(nnd))
            queue.add(nnd);

        // Add the node if there is enough free space in the bucket
        if (super.size() < size) {
            NND node = queue.poll();
            final boolean result = super.add(node);
            sort(lastSeenComparator());
            return result;
        }

        // If the bucket is full, ping the least recently seen node in order to see if it's still alive
        NND firstNode = get(0);
        Ping ping = new Ping(parentNode.getInfo(), firstNode);
        log("Pinging " + firstNode.getId());

        parentNode.getDispatcher().sendMessage(ping, new Dispatcher.MessageListener() {
            @Override
            public void onSuccess(Message response) {
                log("Ping response received from " + response.getSource().getId());
                queue.poll();
            }

            @Override
            public void onFailure() {
                log("Can't ping " + ping.getDestination().getId());
                remove(firstNode);
                Bucket.super.add(queue.poll());
                sort(lastSeenComparator());
            }
        });

        return false;
    }


    @Override
    public synchronized boolean addAll(Collection<? extends NND> c) {
        boolean result = false;

        for (NND nnd : c)
            result |= add(nnd);

        return result;
    }


    @Override
    public synchronized boolean addAll(int index, Collection<? extends NND> c) {
        return addAll(c);
    }


    @Override
    public synchronized void replaceAll(UnaryOperator<NND> operator) {
        throw new UnsupportedOperationException();
    }


    @Override
    public synchronized NND remove(int index) {
        throw new UnsupportedOperationException();
    }


    @Override
    public synchronized boolean remove(Object o) {
        return super.remove(o);
    }


    @Override
    protected synchronized void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }


    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }


    @Override
    public synchronized boolean removeIf(Predicate<? super NND> filter) {
        throw new UnsupportedOperationException();
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
     * Schedule ping for a node
     *
     * @param   node    node to be pinged
     */
    private void schedulePing(NND node) {
        /*final int PING_PERIOD = Math.max((int) (randomGaussian.getGaussian(15, 1) * 1000), 5000);

        Timer timer = scheduledTimers.get(node);

        if (timer != null)
            timer.cancel();

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //parentNode.ping(node);
            }
        }, PING_PERIOD);

        scheduledTimers.put(node, timer);*/
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
        List<NND> allNodes = new ArrayList<>(this);

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
        LogUtils.d("Node [" + parentNode.getInfo().getId() + "]", message);
    }

}
