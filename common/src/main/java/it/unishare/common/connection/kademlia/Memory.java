package it.unishare.common.connection.kademlia;

import it.unishare.common.connection.kademlia.rpc.Message;
import it.unishare.common.connection.kademlia.rpc.Store;
import it.unishare.common.utils.LogUtils;

import java.util.*;

class Memory {

    private final KademliaNode parentNode;
    private final Map<NodeId, KademliaFile> memory = new HashMap<>();
    private final Map<NodeId, Timer> republishTimers = new HashMap<>();
    private final Map<NodeId, Timer> expirationTimers = new HashMap<>();


    /**
     * Constructor
     *
     * @param   parentNode      parent node
     */
    public Memory(KademliaNode parentNode) {
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
     * Get all files
     *
     * @return  all files
     */
    public Collection<KademliaFile> getAllFiles() {
        return memory.values();
    }


    /**
     * Get the files matching the filter
     *
     * @param   filter      filter
     * @return  files matching the filter
     */
    public Collection<KademliaFile> getFiles(KademliaFileData filter) {
        Collection<KademliaFile> allFiles = getAllFiles();

        if (filter == null) {
            return allFiles;
        } else {
            Collection<KademliaFile> result = new ArrayList<>();
            log("All files: " + allFiles);

            allFiles.forEach(file -> {
                if (isFileMatchingFilter(file, filter)) {
                    log("File found: " + file.getData());
                    result.add(file);
                }
            });

            return result;
        }
    }


    /**
     * Check if a file matches a filter
     *
     * @param   file        file
     * @param   filter      filter
     *
     * @return  true if the file matches the filter; false otherwise
     */
    private static boolean isFileMatchingFilter(KademliaFile file, KademliaFileData filter) {
        KademliaFileData fileData = file.getData();

        if (filter.getTitle() != null && !filter.getTitle().equals(fileData.getTitle()))
            return false;

        if (filter.getAuthor() != null && !filter.getAuthor().equals(fileData.getAuthor()))
            return false;

        if (filter.getUniversity() != null && !filter.getUniversity().equals(fileData.getUniversity()))
            return false;

        if (filter.getDepartment() != null && !filter.getDepartment().equals(fileData.getDepartment()))
            return false;

        if (filter.getCourse() != null && !filter.getCourse().equals(fileData.getCourse()))
            return false;

        if (filter.getTeacher() != null && !filter.getTeacher().equals(fileData.getTeacher()))
            return false;

        return true;
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
                parentNode.waitForConnection();

                parentNode.lookup(data.getKey(), nearestNodes -> {
                    // Publish key
                    log("Publishing key " + data.getKey() + " to its nearest nodes: " + nearestNodes.toString());

                    nearestNodes.forEach(node -> {
                        Store message = new Store(parentNode.getInfo(), node, data);

                        parentNode.getDispatcher().sendMessage(message, new MessageListener() {
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
        Collection<NodeId> ownedFiles = republishTimers.keySet();

        for (NodeId file : ownedFiles) {
            delete(file);
        }
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
