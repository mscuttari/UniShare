package it.unishare.common.connection.kademlia;

import it.unishare.common.connection.kademlia.rpc.Message;
import it.unishare.common.connection.kademlia.rpc.Store;
import it.unishare.common.utils.LogUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

class Memory {

    private final Node parentNode;
    private final Map<NodeId, KademliaFile> memory = new HashMap<>();


    /**
     * Constructor
     *
     * @param   parentNode      parent node
     */
    public Memory(Node parentNode) {
        this.parentNode = parentNode;
    }


    /**
     * Get value
     *
     * @param   key     key the value is mapped to
     * @return  value associated to the key
     */
    public KademliaFile get(NodeId key) {
        return memory.get(key);
    }


    /**
     * Get keys
     *
     * @return  all keys
     */
    public Set<NodeId> getKeys() {
        return memory.keySet();
    }


    /**
     * Get values
     *
     * @return  all values
     */
    public Collection<KademliaFile> getFiles() {
        return memory.values();
    }


    /**
     * Store data only on this node
     *
     * @param   data    data to be stored
     */
    public void store(KademliaFile data) {
        memory.put(data.getKey(), data);
        log("Key " + data.getKey() + " stored");
    }


    /**
     * Store data and publish it to the k nodes nearest to the key
     *
     * @param   data    data to be stored
     * @param   k       the number of the nodes the information has to be stored on (same value of bucket size)
     */
    public void store(KademliaFile data, int k) {
        memory.put(data.getKey(), data);
        log("Key " + data.getKey() + " stored");

        List<NND> nearestNodes = parentNode.lookup(data.getKey());

        // Wait for the list to populate
        try {
            Semaphore semaphore = new Semaphore(1);
            semaphore.acquire();
            final AtomicInteger size = new AtomicInteger(nearestNodes.size());

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (nearestNodes.size() == size.get()) {
                        timer.cancel();
                        semaphore.release();
                    } else {
                        size.set(nearestNodes.size());
                    }
                }
            }, 1000, 1000);

            semaphore.acquire();

            // Copy the list of the nearest nodes in order to avoid changes
            List<NND> nearestNodesCopy = new ArrayList<>(nearestNodes);

            if (nearestNodesCopy.size() == 0)
                return;

            List<NND> nodes = nearestNodesCopy.subList(0, Math.min(nearestNodesCopy.size(), k));

            // Publish key
            log("Publishing key " + data.getKey() + ". Nearest nodes: " + nearestNodes.toString());

            nodes.forEach(node -> CompletableFuture.runAsync(() -> {
                Store message = new Store(parentNode.getInfo(), node, data);
                parentNode.getDispatcher().sendMessage(message, new Dispatcher.MessageListener() {
                    @Override
                    public void onSuccess(Message response) {
                        parentNode.getRoutingTable().addNode(response.getSource());
                    }

                    @Override
                    public void onFailure() {
                        parentNode.ping(message.getDestination());
                    }
                });
            }));

        } catch (InterruptedException e) {
            log("Can't publish key " + data.getKey());
            e.printStackTrace();
        }
    }


    /**
     * Remove data
     *
     * @param   key     key the value is mapped to
     */
    public void delete(String key) {
        NodeId id = new NodeId(key);
        memory.remove(id);
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
