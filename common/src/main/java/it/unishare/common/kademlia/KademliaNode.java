package it.unishare.common.kademlia;

import com.google.common.util.concurrent.MoreExecutors;
import it.unishare.common.exceptions.NodeNotConnectedException;
import it.unishare.common.utils.ListUtils;
import it.unishare.common.utils.LogUtils;
import it.unishare.common.utils.Pair;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public abstract class KademliaNode<F extends KademliaFile<FD>, FD extends KademliaFileData> {

    // Connection
    private DatagramSocket messagesServerSocket;
    private ServerSocket fileServerSocket;

    private ExecutorService executorService, downloadExecutorService, uploadExecutorService;
    private Semaphore serverSemaphore = new Semaphore(0);
    private Semaphore connectionSemaphore = new Semaphore(0);

    // Configuration
    private static final int BUCKET_SIZE = 20;
    private static final int SIMULTANEOUS_LOOKUPS = 3;

    // Data
    private NND info;
    private Dispatcher dispatcher;
    private RoutingTable routingTable;
    private Memory<F, FD> memory;

    // Files
    private FilesProvider filesProvider;


    /**
     * Constructor
     */
    public KademliaNode() {
        this(null);
    }


    /**
     * Constructor
     *
     * @param   fileProvider        files and reviews provider
     */
    public KademliaNode(FilesProvider fileProvider) {
        this.filesProvider = fileProvider;

        this.executorService = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });

        this.downloadExecutorService = MoreExecutors.getExitingExecutorService(
                (ThreadPoolExecutor) Executors.newCachedThreadPool(),
                Integer.MAX_VALUE, TimeUnit.DAYS
        );

        this.uploadExecutorService = MoreExecutors.getExitingExecutorService(
                (ThreadPoolExecutor) Executors.newCachedThreadPool(),
                Integer.MAX_VALUE, TimeUnit.DAYS
        );
    }


    /**
     * Get server side IP address
     *
     * @return  IP address
     */
    private static InetAddress getServerIP() throws IOException {
        try (final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress();
        }

        /*
            Uncomment if you want to run the network on a real web scenario and not in a LAN

            URL whatIsMyIP = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIP.openStream()));
            String ip = in.readLine();
            return InetAddress.getByName(ip);
        */
    }


    /**
     * Get the messages server socket
     *
     * @return  messages server socket
     */
    private DatagramSocket getMessagesServerSocket() {
        if (messagesServerSocket == null) {
            try {
                messagesServerSocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        return messagesServerSocket;
    }


    /**
     * Get the message dispatcher
     *
     * @return  message dispatcher
     */
    private Dispatcher getDispatcher() {
        if (dispatcher == null) {
            try {
                dispatcher = new Dispatcher();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        return dispatcher;
    }


    /**
     * Get node information
     *
     * @return  node info
     */
    public NND getInfo() {
        if (info == null && getMessagesServerSocket() != null) {
            try {
                info = new NND(getServerIP(), getMessagesServerSocket().getLocalPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return info;
    }


    /**
     * Get the routing table
     *
     * @return  routing table
     */
    RoutingTable getRoutingTable() {
        if (routingTable == null && getInfo() != null) {
            routingTable = new RoutingTable(this, getInfo().getIdLength(), BUCKET_SIZE);
        }

        return routingTable;
    }


    /**
     * Get memory
     *
     * @return  memory
     */
    private Memory<F, FD> getMemory() {
        if (memory == null) {
            memory = new Memory<>(this);
        }

        return memory;
    }


    /**
     * Get files provider
     *
     * @return  files provider
     */
    protected FilesProvider getFilesProvider() {
        return filesProvider;
    }


    /**
     * Set files provider
     *
     * @param   filesProvider   files provider
     */
    public void setFilesProvider(FilesProvider filesProvider) {
        this.filesProvider = filesProvider;
    }


    /**
     * Check whether the node is connected to the DHT network
     *
     * @return  true if connected; false otherwise
     */
    private boolean isConnected() {
        return connectionSemaphore.availablePermits() > 0;
    }


    /**
     * Wait for connectivity
     */
    void waitForConnection() {
        try {
            connectionSemaphore.acquire();
            connectionSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Set the managers status
     *
     * @param   connected   true to set the node as connected to the network; false to set it as disconnected
     */
    void setConnectionStatus(boolean connected) {
        connectionSemaphore.drainPermits();

        if (connected) {
            connectionSemaphore.release(Integer.MAX_VALUE);
        } else {
            resetConnection();
        }
    }


    /**
     * Reset servers
     */
    private void resetConnection() {
        executorService.shutdownNow();

        // Close the messages server socket
        if (messagesServerSocket != null && !messagesServerSocket.isClosed())
            messagesServerSocket.close();

        messagesServerSocket = null;

        // Close the file server socket
        if (fileServerSocket != null && !fileServerSocket.isClosed()) {
            try {
                fileServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        fileServerSocket = null;

        info = null;
    }


    /**
     * Start server
     */
    private void startServer() {
        // Response server
        executorService.submit(() -> {
            DatagramPacket packet = new DatagramPacket(new byte[65535], 65535);
            log("Response server started");
            serverSemaphore.release();

            // noinspection InfiniteLoopStatement
            while (true) {
                try {
                    getMessagesServerSocket().receive(packet);

                    byte[] data = packet.getData();
                    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
                    Object obj = objectInputStream.readObject();

                    if (obj == null) continue;

                    if (obj instanceof Message) {
                        Message message = (Message) obj;

                        if (getDispatcher().isResponse(message)) {
                            getDispatcher().dispatch(message);
                        } else {
                            elaborateMessage(message);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // File server
        executorService.submit(() -> {
            try {
                fileServerSocket = new ServerSocket(getMessagesServerSocket().getLocalPort());
                log("File server started");
                serverSemaphore.release();

                // noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        Socket socket = fileServerSocket.accept();
                        uploadExecutorService.submit(new Uploader(KademliaNode.this, socket));
                    } catch (Exception e) {
                        // Recreate the socket
                        fileServerSocket = new ServerSocket(getMessagesServerSocket().getLocalPort());
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * Send message
     *
     * @param   message         message to be sent
     */
    protected void sendMessage(Message message) {
        sendMessage(message, null);
    }


    /**
     * Send message
     *
     * @param   message         message to be sent
     * @param   listener        response listener
     */
    protected void sendMessage(Message message, MessageListener listener) {
        getDispatcher().sendMessage(message, listener);
    }


    /**
     * Analyze received message and send a response message
     *
     * The message is passed to {@link #onMessageReceived(Message)} if it's not
     * a {@link PingMessage}, a {@link FindNodeMessage}, a {@link FindDataMessage}
     * or a {@link StoreMessage}.
     *
     * @param   message     received message
     */
    private void elaborateMessage(Message message) {
        getRoutingTable().addNode(message.getSource());

        if (message instanceof PingMessage) {
            // PING
            log("Ping request received from " + message.getSource().getId());
            PingMessage response = ((PingMessage) message).createResponse();
            sendMessage(response);
            log("Ping response sent to " + response.getDestination().getId());

        } else if (message instanceof FindNodeMessage) {
            // FIND_NODE
            NodeId targetId = ((FindNodeMessage) message).getTargetId();
            log("Lookup request received from " + message.getSource().getId() + " for " + targetId);
            FindNodeMessage response = ((FindNodeMessage) message).createResponse();
            response.setNearestNodes(getRoutingTable().getNearestNodes(targetId, BUCKET_SIZE));
            sendMessage(response);

        } else if (message instanceof StoreMessage) {
            // STORE
            @SuppressWarnings("unchecked") StoreMessage<F, FD> storeMessage = (StoreMessage<F, FD>) message;
            F data = storeMessage.getData();
            log("Store request received from " + message.getSource().getId() + " for " + data.getKey());
            getMemory().store(data);
            StoreMessage response = ((StoreMessage) message).createResponse();
            sendMessage(response);

        } else if (message instanceof FindDataMessage) {
            // FIND_DATA
            @SuppressWarnings("unchecked") FindDataMessage<F, FD> findDataMessage = (FindDataMessage<F, FD>) message;
            FD filter = findDataMessage.getFilter();
            log("Search request received from " + message.getSource().getId() + " for " + filter);
            FindDataMessage<F, FD> response = findDataMessage.createResponse(getMemory().getFiles(filter));
            sendMessage(response);

        } else {
            onMessageReceived(message);
        }
    }


    /**
     * Called when a non-basic message is received.
     * This way, custom messages can be defined and managed to an higher application level.
     *
     * @param   message     received message
     */
    protected abstract void onMessageReceived(Message message);


    /**
     * Connect to the the Kademlia network and start the discovery process
     *
     * @param   bootstrapNode       bootstrap node
     */
    public void bootstrap(NND bootstrapNode) {
        startServer();

        try {
            serverSemaphore.acquire(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!getInfo().equals(bootstrapNode)) {
            PingMessage message = new PingMessage(getInfo(), bootstrapNode);

            sendMessage(message, new MessageListener() {
                @Override
                public void onSuccess(Message response) {
                    log("Ping response received from " + response.getSource().getId());
                    getRoutingTable().addNode(response.getSource());
                    lookup(bootstrapNode.getId());
                }

                @Override
                public void onFailure() {
                    log("Can't ping " + message.getDestination().getId());
                }
            });
        }
    }


    /**
     * Ping a node
     *
     * @param   node    node to be pinged
     */
    protected void ping(NND node) {
        ping(node, new MessageListener() {
            @Override
            public void onSuccess(Message response) {
                log("Ping response received from " + response.getSource().getId());
                getRoutingTable().addNode(node);
            }

            @Override
            public void onFailure() {
                log("Can't ping " + node.getId());
                getRoutingTable().removeNode(node);
            }
        });
    }


    /**
     * Ping node
     *
     * @param   node        node to be pinged
     * @param   listener    response listener
     */
    private void ping(NND node, MessageListener listener) {
        log("Pinging " + node.getId());
        PingMessage message = new PingMessage(getInfo(), node);
        sendMessage(message, listener);
    }


    /**
     * Node lookup
     *
     * @param   targetId                target node ID
     * @param   endProcessListener      listener to be called when the lookup process is terminated
     */
    void lookup(NodeId targetId, LookupListener endProcessListener) {
        // List check period
        final int PERIOD = 1500;

        // Start the lookup process
        final List<NND> nearestNodes = lookup(targetId);

        try {
            Semaphore semaphore = new Semaphore(1);
            semaphore.acquire();

            final AtomicReference<List<NND>> oldNearestNodes = new AtomicReference<>();
            oldNearestNodes.set(new ArrayList<>(nearestNodes));

            // Schedule periodic check
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // Check if the list has changed
                    if (ListUtils.equalsIgnoreOrderAndRepetitions(nearestNodes, oldNearestNodes.get())) {
                        timer.cancel();
                        semaphore.release();
                    } else {
                        oldNearestNodes.set(new ArrayList<>(nearestNodes));
                    }
                }
            }, PERIOD, PERIOD);

            semaphore.acquire();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Pass the result to the listener
        if (endProcessListener != null) {
            List<NND> result = new ArrayList<>(nearestNodes);
            endProcessListener.finish(Collections.unmodifiableList(result));
        }
    }


    /**
     * Node lookup
     *
     * @param   targetId        target node ID
     * @return  nodes close to the target
     */
    private List<NND> lookup(NodeId targetId) {
        // Get the first alpha nodes to send the first requests
        List<NND> nearestNodes = getRoutingTable().getNearestNodes(targetId, SIMULTANEOUS_LOOKUPS);
        if (nearestNodes.size() == 0) return new ArrayList<>();
        log("Starting lookup nodes: " + nearestNodes);

        // Send asynchronous requests
        List<NND> queriedNodes = new ArrayList<>();

        nearestNodes.forEach(node -> {
            FindNodeMessage message = new FindNodeMessage(getInfo(), node, targetId);

            sendMessage(message, new MessageListener() {
                @Override
                public void onSuccess(Message response) {
                    // Add the node to the queried ones
                    queriedNodes.add(node);
                    getRoutingTable().addNode(response.getSource());

                    // Get query groups
                    assert response instanceof FindNodeMessage;

                    List<NND> nearestNodes = new ArrayList<>(((FindNodeMessage) response).getNearestNodes());
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
        });

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
        primaryGroup.forEach(recipient -> {
            if (queriedNodes.contains(recipient))
                return;

            FindNodeMessage message = new FindNodeMessage(getInfo(), recipient, targetId);

            sendMessage(message, new MessageListener() {
                @Override
                public void onSuccess(Message response) {
                    if (queriedNodes.contains(recipient))
                        return;

                    // Add the node to the queried ones
                    queriedNodes.add(recipient);
                    getRoutingTable().addNode(response.getSource());

                    // Get query groups
                    assert response instanceof FindNodeMessage;

                    List<NND> nearestNodes = new ArrayList<>(((FindNodeMessage) response).getNearestNodes());
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
        });
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
     * Store file in this node and publish it on the network
     *
     * @param   file        file to be stored
     */
    public void storeFile(F file) {
        getMemory().store(file, BUCKET_SIZE);
    }


    /**
     * Store files in this node and publish them on the network
     *
     * @param   files       files to be stored
     */
    public void storeFiles(Collection<F> files) {
        files.forEach(this::storeFile);
    }


    /**
     * Delete file from this node
     *
     * @param   key         key the data is associated to
     */
    public void deleteFile(NodeId key) {
        getMemory().delete(key);
    }


    /**
     * Delete all the owned files by this node
     */
    public void deleteAllFiles() {
        getMemory().deleteAll();
    }


    /**
     * Search files with given values
     *
     * @param   filter      search filters
     * @throws  NodeNotConnectedException   if the node is not connected to the network
     */
    public void searchData(FD filter, SearchListener<F, FD> listener) throws NodeNotConnectedException {
        if (!isConnected())
            throw new NodeNotConnectedException();

        Collection<F> files = new HashSet<>();
        Collection<F> unmodifiableFilesList = Collections.unmodifiableCollection(files);
        List<NND> knownNodes = getRoutingTable().getAllNodes();

        knownNodes.forEach(node -> {
            FindDataMessage<F, FD> message = new FindDataMessage<>(getInfo(), node, filter);

            sendMessage(message, new MessageListener() {
                @Override
                public void onSuccess(Message response) {
                    if (response instanceof FindDataMessage) {
                        @SuppressWarnings("unchecked") FindDataMessage<F, FD> findDataResponse = (FindDataMessage<F, FD>) response;

                        for (F file : findDataResponse.getFiles()) {
                            if ((file.getOwner().equals(getInfo())))
                                continue;

                            // Check if the node is online
                            ping(file.getOwner(), new MessageListener() {
                                @Override
                                public void onSuccess(Message response) {
                                    listener.found(unmodifiableFilesList);
                                    files.add(file);
                                }

                                @Override
                                public void onFailure() {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure() {
                    ping(node);
                }
            });
        });
    }


    /**
     * Download file
     *
     * @param   file            file to be downloaded
     * @param   downloadPath    download path
     *
     * @return  {@link Future} representing the download process
     */
    public Future<?> downloadFile(KademliaFile file, File downloadPath) {
        Runnable downloader = new Downloader(file, downloadPath);
        return downloadExecutorService.submit(downloader);
    }


    /**
     * Log message
     *
     * @param   message     message to be logged
     */
    protected void log(String message) {
        LogUtils.d("Node [" + getInfo().getId() + ", " + getInfo().getAddress() + ":" + getInfo().getPort() + "]", message);
    }

}