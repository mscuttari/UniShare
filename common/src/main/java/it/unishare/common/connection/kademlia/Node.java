package it.unishare.common.connection.kademlia;

import it.unishare.common.connection.kademlia.rpc.Message;
import it.unishare.common.connection.kademlia.rpc.Ping;
import it.unishare.common.utils.LogUtils;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.UUID;

public class Node {

    // Connection
    private DatagramSocket serverSocket;
    private Dispatcher dispatcher;

    private boolean connected = false;

    // Configuration
    private NND info;
    private static final int simultaneousLookups = 3;
    private RoutingTable routingTable;


    /**
     * Constructor
     */
    public Node() throws Exception {
        this.serverSocket = new DatagramSocket();
        this.dispatcher = new Dispatcher();
        this.info = new NND(generateId(), getServerIP(), serverSocket.getLocalPort());
        this.routingTable = new RoutingTable(this, getIdLength());

        startServer();
    }


    /**
     * Generate ID for the node
     *
     * @return  unique ID
     */
    private static long generateId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }


    /**
     * Get the number of bits composing the ID
     *
     * @return  ID length in bit
     */
    private static int getIdLength() {
        return 64;
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
     * Check whether the node is connected to the network
     *
     * @return  true if connected; false otherwise
     */
    public boolean isConnected() {
        return connected;
    }


    /**
     * Start server
     */
    private void startServer() {
        connected = true;

        Runnable server = () -> {
            DatagramPacket packet = new DatagramPacket(new byte[16416], 16416);

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

        log("Server started");
    }


    /**
     * Analyze received message and send a response message
     *
     * @param   message     received message
     */
    private void elaborateMessage(Message message) {
        routingTable.addNode(message.getSource());

        // Ping
        if (message instanceof Ping) {
            log("Ping request received from " + message.getSource().getId());
            Ping response = ((Ping) message).createResponse();
            dispatcher.sendMessage(response);
            log("Ping response sent to " + response.getDestination().getId());
        }
    }


    /**
     * Connect to the the Kademlia network and start the discovery process
     *
     * @param   accessPoint     access point information
     */
    public void bootstrap(NND accessPoint) {
        ping(accessPoint);
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
            public void onSuccess() {
                log("Ping response received from " + message.getDestination().getId());
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
     * Search key in the network
     *
     * @param   key     key
     * @return  value associated to the key
     */
    public String lookup(String key) {
        List<NND> nearestNodes = routingTable.getNearestNodes(simultaneousLookups);
        return null;
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