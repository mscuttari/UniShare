package it.unishare.common.kademlia;

import java.util.*;

class Memory<F extends KademliaFile<FD>, FD extends KademliaFileData> {

    private final KademliaNode<F, FD> parentNode;
    private final Map<NodeId, F> memory = new HashMap<>();
    private final Map<NodeId, Timer> republishTimers = new HashMap<>();
    private final Map<NodeId, Timer> expirationTimers = new HashMap<>();


    /**
     * Constructor
     *
     * @param   parentNode      parent node
     */
    public Memory(KademliaNode<F, FD> parentNode) {
        this.parentNode = parentNode;
    }


    /**
     * Get value
     *
     * @param   key     key the value is mapped to
     * @return  value associated to the key
     */
    public F get(NodeId key) {
        return memory.get(key);
    }


    /**
     * Get all files
     *
     * @return  all files
     */
    public Collection<F> getAllFiles() {
        return memory.values();
    }


    /**
     * Get the files matching the filter
     *
     * @param   filter      filter
     * @return  files matching the filter
     */
    public Collection<F> getFiles(FD filter) {
        Collection<F> allFiles = getAllFiles();

        if (filter == null) {
            return allFiles;
        } else {
            Collection<F> result = new ArrayList<>();
            log("All files: " + allFiles);

            allFiles.forEach(file -> {
                if (file.matchesFilter(filter)) {
                    log("File found: " + file.getData());
                    result.add(file);
                }
            });

            return result;
        }
    }


    /**
     * Store data only and set expiration
     *
     * @param   data    data to be stored
     */
    public void store(F data) {
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
        Timer timer = new Timer(true);
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
    public void store(F data, int k) {
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
    private void publishKey(F data, int k) {
        final int REPUBLISH = 60 * 60 * 1000;

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                parentNode.waitForConnection();

                parentNode.lookup(data.getKey(), nearestNodes -> {
                    // Publish key
                    log("Publishing key " + data.getKey() + " to its nearest nodes: " + nearestNodes.toString());

                    nearestNodes.forEach(node -> {
                        StoreMessage message = new StoreMessage(parentNode.getInfo(), node, data);

                        parentNode.sendMessage(message, new MessageListener() {
                            @Override
                            public void onSuccess(Message response) {
                                parentNode.getRoutingTable().addNode(response.getSource());
                            }

                            @Override
                            public void onFailure() {
                                parentNode.ping(message.getDestination());
                            }
                        });
                    });
                });
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
     * Delete all the owned files
     */
    public void deleteAll() {
        Collection<NodeId> ownedFiles = new ArrayList<>(republishTimers.keySet());
        ownedFiles.forEach(this::delete);
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
