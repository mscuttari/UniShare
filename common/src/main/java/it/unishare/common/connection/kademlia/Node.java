package it.unishare.common.connection.kademlia;

import it.unishare.common.connection.kademlia.rpc.FindNode;
import it.unishare.common.connection.kademlia.rpc.Message;
import it.unishare.common.connection.kademlia.rpc.Ping;
import it.unishare.common.utils.LogUtils;
import sun.awt.Mutex;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Node {

    // Connection
    private DatagramSocket serverSocket;
    private Dispatcher dispatcher;

    private Mutex mutex = new Mutex();
    private boolean connected = false;

    // Configuration
    private static final int BUCKET_SIZE = 20;
    private static final int SIMULTANEOUS_LOOKUPS = 3;
    private NND info;
    private RoutingTable routingTable;


    /**
     * Constructor
     */
    public Node() throws Exception {
        this.serverSocket = new DatagramSocket();
        this.dispatcher = new Dispatcher();
        this.info = new NND(getServerIP(), serverSocket.getLocalPort());
        this.routingTable = new RoutingTable(this, info.getIdLength(), BUCKET_SIZE);
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
     * Get message dispatcher
     *
     * @return  message dispatcher
     */
    Dispatcher getDispatcher() {
        return dispatcher;
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
    }


    /**
     * Connect to the the Kademlia network and start the discovery process
     *
     * @param   bootstrapNode       bootstrap node
     */
    public void bootstrap(NND bootstrapNode) {
        startServer();
        mutex.lock();

        if (!info.equals(bootstrapNode)) {
            ping(bootstrapNode);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    //findNode(bootstrapNode.getId());
                }
            }, 5000);
        }

        mutex.unlock();
    }


    /**
     * Ping a node
     *
     * @param   node    node to be pinged
     */
    private void ping(NND node) {
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
     * Get the k nearest nodes to the target ID
     *
     * @param   targetId        ID of the searched node
     */
    private void findNode(NodeId targetId) {
        List<NND> nearestNodes = routingTable.getNearestNodes(targetId, SIMULTANEOUS_LOOKUPS);

        nearestNodes.remove(info);
        nearestNodes.forEach(node -> CompletableFuture.runAsync(() -> {
            FindNode message = new FindNode(getInfo(), node, targetId);

            dispatcher.sendMessage(message, new Dispatcher.MessageListener() {
                @Override
                public void onSuccess(Message response) {
                    if (response instanceof FindNode) {
                        FindNode findNodeResponse = (FindNode) response;
                        List<NND> nearestNodes = findNodeResponse.getNearestNodes();

                        boolean found = false;
                        for (NND node : nearestNodes) {
                            if (!info.equals(node))
                                routingTable.addNode(node);
                        }
                    }
                }

                @Override
                public void onFailure() {
                    ping(node);
                }
            });
        }));
    }


    /**
     * Get distance from an another node
     *
     * @param   firstId     first node ID
     * @param   secondId    second node ID
     *
     * @return  distance
     */
    public static long distance(long firstId, long secondId) {
        return firstId ^ secondId;
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