package it.unishare.common.connection.kademlia;

import it.unishare.common.connection.kademlia.rpc.FindNode;
import it.unishare.common.connection.kademlia.rpc.Message;
import it.unishare.common.connection.kademlia.rpc.Ping;
import it.unishare.common.connection.kademlia.rpc.Store;
import it.unishare.common.utils.LogUtils;
import it.unishare.common.utils.Pair;
import sun.awt.Mutex;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Node {

    // Connection
    private DatagramSocket serverSocket;

    private Mutex mutex = new Mutex();
    private boolean connected = false;

    // Configuration
    private static final int BUCKET_SIZE = 20;
    private static final int SIMULTANEOUS_LOOKUPS = 3;

    // Data
    private final Dispatcher dispatcher;
    private final NND info;
    private final RoutingTable routingTable;
    private final Memory memory;


    /**
     * Constructor
     */
    public Node() throws Exception {
        this.serverSocket = new DatagramSocket();
        this.dispatcher = new Dispatcher();
        this.info = new NND(getServerIP(), serverSocket.getLocalPort());
        this.routingTable = new RoutingTable(this, info.getIdLength(), BUCKET_SIZE);
        this.memory = new Memory(this);
    }


    /**
     * Get server side IP address
     *
     * @return  IP address
     */
    private static InetAddress getServerIP() throws IOException {
        return InetAddress.getByName("127.0.0.1");

        /*
        URL whatIsMyIP = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIP.openStream()));
        String ip = in.readLine();
        return InetAddress.getByName(ip);
        */
    }


    /**
     * Get node information
     *
     * @return  node info
     */
    public NND getInfo() {
        return info;
    }


    /**
     * Get the message dispatcher
     *
     * @return  message dispatcher
     */
    Dispatcher getDispatcher() {
        return dispatcher;
    }


    /**
     * Get the routing table
     *
     * @return  routing table
     */
    RoutingTable getRoutingTable() {
        return routingTable;
    }


    /**
     * Start server
     */
    private void startServer() {
        mutex.lock();
        connected = true;

        Runnable server = () -> {
            DatagramPacket packet = new DatagramPacket(new byte[16416], 16416);
            log("Server started");

            while (connected) {
                try {
                    serverSocket.receive(packet);

                    byte[] data = packet.getData();
                    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
                    Object obj = objectInputStream.readObject();

                    if (obj == null) continue;

                    if (obj instanceof Message) {
                        Message message = (Message) obj;

                        if (dispatcher.isResponse(message)) {
                            dispatcher.dispatch(message);
                        } else {
                            elaborateMessage(message);
                        }
                    }

                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(server);
        thread.start();
        mutex.unlock();
    }


    /**
     * Analyze received message and send a response message
     *
     * @param   message     received message
     */
    private void elaborateMessage(Message message) {
        routingTable.addNode(message.getSource());

        // PING
        if (message instanceof Ping) {
            log("Ping request received from " + message.getSource().getId());
            Ping response = ((Ping) message).createResponse();
            dispatcher.sendMessage(response);
            log("Ping response sent to " + response.getDestination().getId());
        }

        // FIND_NODE
        if (message instanceof FindNode) {
            NodeId targetId = ((FindNode) message).getTargetId();
            log("Lookup request received from " + message.getSource().getId() + " for " + targetId);
            FindNode response = ((FindNode) message).createResponse();
            response.setNearestNodes(routingTable.getNearestNodes(targetId, BUCKET_SIZE));
            dispatcher.sendMessage(response);
        }

        // STORE
        if (message instanceof Store) {
            KademliaFile data = ((Store) message).getData();
            log("Store request received from " + message.getSource().getId() + " for " + data.getKey());
            memory.store(data);
            Store response = ((Store) message).createResponse();
            dispatcher.sendMessage(response);
        }
    }


    /**
     * Connect to the the Kademlia network and start the discovery process
     *
     * @param   bootstrapNode       bootstrap node
     */
    public void bootstrap(NND bootstrapNode) {
        startServer();
        mutex.lock();

        if (!getInfo().equals(bootstrapNode)) {
            Ping message = new Ping(getInfo(), bootstrapNode);

            dispatcher.sendMessage(message, new Dispatcher.MessageListener() {
                @Override
                public void onSuccess(Message response) {
                    log("Ping response received from " + response.getSource().getId());
                    routingTable.addNode(response.getSource());
                    lookup(bootstrapNode.getId());
                }

                @Override
                public void onFailure() {
                    log("Can't ping " + message.getDestination().getId());
                }
            });
        }

        mutex.unlock();
    }


    /**
     * Ping a node
     *
     * @param   node    node to be pinged
     */
    void ping(NND node) {
        log("Pinging " + node.getId());
        Ping message = new Ping(getInfo(), node);

        dispatcher.sendMessage(message, new Dispatcher.MessageListener() {
            @Override
            public void onSuccess(Message response) {
                log("Ping response received from " + response.getSource().getId());
                routingTable.addNode(node);
            }

            @Override
            public void onFailure() {
                log("Can't ping " + message.getDestination().getId());
                routingTable.removeNode(node);
            }
        });
    }


    /**
     * Node lookup
     *
     * @param   targetId        target node ID
     * @return  nodes close to the target
     */
    List<NND> lookup(NodeId targetId) {
        // Get the first alpha nodes to send the first requests
        List<NND> nearestNodes = routingTable.getNearestNodes(targetId, SIMULTANEOUS_LOOKUPS);
        if (nearestNodes.size() == 0) return new ArrayList<>();
        log("Starting lookup nodes: " + nearestNodes);

        // Send asynchronous requests
        List<NND> queriedNodes = new ArrayList<>();

        nearestNodes.forEach(node -> CompletableFuture.runAsync(() -> {
            FindNode message = new FindNode(getInfo(), node, targetId);

            dispatcher.sendMessage(message, new Dispatcher.MessageListener() {
                @Override
                public void onSuccess(Message response) {
                    // Add the node to the queried ones
                    queriedNodes.add(node);
                    routingTable.addNode(response.getSource());

                    // Get query groups
                    assert response instanceof FindNode;

                    List<NND> nearestNodes = new ArrayList<>(((FindNode) response).getNearestNodes());
                    nearestNodes.remove(getInfo());
                    log("Recursive lookup nodes: " + nearestNodes);

                    Pair<List<NND>, List<NND>> lookupGroups = splitLookupGroup(nearestNodes);
                    List<NND> primaryGroup = lookupGroups.first;
                    List<NND> secondaryGroup = lookupGroups.second;

                    recursiveLookup(queriedNodes, primaryGroup, secondaryGroup, targetId);
                }

                @Override
                public void onFailure() {
                    ping(node);
                }
            });
        }));

        return queriedNodes;
    }


    /**
     * Recursive node lookup
     *
     * @param   queriedNodes        already queries nodes during the lookup process
     * @param   primaryGroup        primary group (max size of SIMULTANEOUS_LOOKUPS)
     * @param   secondaryGroup      secondary group
     * @param   targetId            target node ID
     */
    private void recursiveLookup(List<NND> queriedNodes, List<NND> primaryGroup, List<NND> secondaryGroup, NodeId targetId) {
        primaryGroup.forEach(recipient -> CompletableFuture.runAsync(() -> {
            if (queriedNodes.contains(recipient))
                return;

            FindNode message = new FindNode(getInfo(), recipient, targetId);

            dispatcher.sendMessage(message, new Dispatcher.MessageListener() {
                @Override
                public void onSuccess(Message response) {
                    if (queriedNodes.contains(recipient))
                        return;

                    // Add the node to the queried ones
                    queriedNodes.add(recipient);
                    routingTable.addNode(response.getSource());

                    // Get query groups
                    assert response instanceof FindNode;

                    List<NND> nearestNodes = new ArrayList<>(((FindNode) response).getNearestNodes());
                    nearestNodes.remove(getInfo());
                    nearestNodes.removeAll(queriedNodes);
                    log("Recursive lookup nodes: " + nearestNodes);

                    Pair<List<NND>, List<NND>> lookupGroups = splitLookupGroup(nearestNodes);
                    List<NND> nextPrimaryGroup = lookupGroups.first;
                    List<NND> nextSecondaryGroup = lookupGroups.second;

                    // Sort the encountered nodes in order to get the nearest encountered node to the target node
                    queriedNodes.sort((o1, o2) -> {
                        BigInteger firstDistance  = o1.getId().distance(targetId);
                        BigInteger secondDistance = o2.getId().distance(targetId);
                        return firstDistance.compareTo(secondDistance);
                    });

                    boolean shouldContinueWithPrimaryGroup = false;
                    NND nearestNode = queriedNodes.get(0);
                    for (NND node : nearestNodes) {
                        shouldContinueWithPrimaryGroup = node.getId().distance(targetId).compareTo(nearestNode.getId().distance(targetId)) <= 0;
                        if (shouldContinueWithPrimaryGroup) break;
                    }

                    if (shouldContinueWithPrimaryGroup) {
                        recursiveLookup(queriedNodes, nextPrimaryGroup, nextSecondaryGroup, targetId);
                    } else {
                        // There is no new nearer node
                        recursiveLookup(queriedNodes, secondaryGroup, new ArrayList<>(), targetId);
                    }
                }

                @Override
                public void onFailure() {
                    ping(recipient);
                }
            });
        }));
    }


    /**
     * Split nearest nodes list into primary and secondary group for successive lookup queries
     *
     * @param   nodes       complete nearest nodes list
     * @return  primary and secondary query groups
     */
    private static Pair<List<NND>, List<NND>> splitLookupGroup(List<NND> nodes) {
        // Primary group
        List<NND> primaryGroup;

        if (nodes.size() == 0) {
            primaryGroup = new ArrayList<>();
        } else {
            primaryGroup = nodes.subList(0, Math.min(nodes.size(), SIMULTANEOUS_LOOKUPS));
        }

        // Secondary group
        List<NND> secondaryGroup;

        if (nodes.size() == 0 || SIMULTANEOUS_LOOKUPS >= nodes.size()) {
            secondaryGroup = new ArrayList<>();
        } else {
            secondaryGroup = nodes.subList(SIMULTANEOUS_LOOKUPS, nodes.size());
        }

        return new Pair<>(primaryGroup, secondaryGroup);
    }


    /**
     * Store data in this node
     *
     * @param   data        data to be stored
     */
    public void storeData(KademliaFile data) {
        memory.store(data, BUCKET_SIZE);
    }


    /**
     * Delete data from this node
     *
     * @param   key         key the data is associated to
     */
    public void deleteData(String key) {
        memory.delete(key);
    }


    /**
     * Log message
     *
     * @param   message     message to be logged
     */
    private void log(String message) {
        LogUtils.d("Node [" + info.getId() + "]", message);
    }

}