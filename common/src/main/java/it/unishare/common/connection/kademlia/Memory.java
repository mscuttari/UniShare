package it.unishare.common.connection.kademlia;

import it.unishare.common.connection.kademlia.rpc.Message;
import it.unishare.common.connection.kademlia.rpc.Store;
import it.unishare.common.utils.LogUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

class Memory {

    private final Node parentNode;
    private final Map<NodeId, KademliaFile> memory = new HashMap<>();
    private final Map<NodeId, Timer> republishTimers = new HashMap<>();
    private final Map<NodeId, Timer> expirationTimers = new HashMap<>();


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
     * Get values
     *
     * @return  all values
     */
    public Collection<KademliaFile> getFiles() {
        return memory.values();
    }


    /**
     * Store data only and set expiration
     *
     * @param   data    data to be stored
     */
    public void store(KademliaFile data) {
        final int EXPIRATION = 60 * 60 * 1000;

        // Check if the key already exists
        if (memory.containsKey(data.getKey())) {
            // The node already owns a file with the same name, so the external file is discarded
            if (!expirationTimers.containsKey(data.getKey()))
                return;

            // Reset the timer by first deleting the older one
            Timer timer = expirationTimers.get(data.getKey());
            timer.cancel();
        }

        // Store the key
        memory.put(data.getKey(), data);
        log("Key " + data.getKey() + " stored");

        // Schedule expiration after an hour
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                expirationTimers.remove(data.getKey());
                delete(data.getKey());
            }
        }, EXPIRATION);

        expirationTimers.put(data.getKey(), timer);
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
        publishKey(data, k);
    }


    /**
     * Publish the data to the k nodes nearest to the key
     *
     * @param   data    data to be stored
     * @param   k       the number of the nodes the information has to be stored on (same value of bucket size)
     */
    private void publishKey(KademliaFile data, int k) {
        final int REPUBLISH = 60 * 60 * 1000;

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
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
                    log("Publishing key " + data.getKey() + " to its nearest nodes: " + nearestNodes.toString());

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
        }, 0, REPUBLISH);

        if (republishTimers.containsKey(data.getKey())) {
            republishTimers.get(data.getKey()).cancel();
        }

        republishTimers.put(data.getKey(), timer);
    }


    /**
     * Remove data
     *
     * @param   id      key the value is mapped to
     */
    public void delete(NodeId id) {
        memory.remove(id);

        if (republishTimers.containsKey(id)) {
            republishTimers.get(id).cancel();
            republishTimers.remove(id);
        }

        if (expirationTimers.containsKey(id)) {
            expirationTimers.get(id).cancel();
            expirationTimers.remove(id);
        }
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
