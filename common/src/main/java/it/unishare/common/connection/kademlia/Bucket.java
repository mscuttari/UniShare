package it.unishare.common.connection.kademlia;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

class Bucket extends ArrayList<NND> {

    private static final int size = 20;
    private Node parentNode;
    private Queue<NND> queue = new LinkedList<>();


    /**
     * Bucket size
     */
    public Bucket(Node node) {
        super(size);
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

            nodeInfo.setIp(nnd.getIp());
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
            final boolean result = super.add(queue.poll());
            sort(lastSeenComparator());
            return result;
        }

        // If the bucket is full, ping the least recently seen nodes in order to search for dead ones
        forEach(n -> parentNode.ping(n));

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
        return super.remove(index);
    }


    @Override
    public synchronized boolean remove(Object o) {
        return super.remove(o);
    }


    @Override
    protected synchronized void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }


    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }


    @Override
    public synchronized boolean removeIf(Predicate<? super NND> filter) {
        return super.removeIf(filter);
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
     * Get nearest nodes
     *
     * @param   nodeId      node ID to search neighbours for
     * @param   amount      amount of nodes
     *
     * @return  nearest nodes
     */
    public List<NND> getNearestNodes(long nodeId, int amount) {
        List<NND> allNodes = new ArrayList<>(this);

        allNodes.sort((o1, o2) -> {
            long difference = o1.getId() - o2.getId();
            return difference == 0 ? 0 : difference < 0 ? -1 : 1;
        });

        return new ArrayList<>(allNodes.subList(0, amount));
    }

}
