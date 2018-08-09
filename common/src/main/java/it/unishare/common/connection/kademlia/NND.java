package it.unishare.common.connection.kademlia;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Calendar;

/**
 * Network Node Data
 */
public class NND implements Serializable {

    private static final long serialVersionUID = -6360597132702310992L;

    /** ID */
    private NodeId id;

    /** Address */
    private InetAddress address;

    /** Connection port */
    private int port;

    /** Last seen */
    private Calendar lastSeen;


    /**
     * Constructor
     *
     * @param   address     address
     * @param   port        managers port
     */
    public NND(InetAddress address, int port) {
        this(new NodeId(), address, port);
    }


    /**
     * Constructor
     *
     * @param   id          ID
     * @param   address     address
     * @param   port        managers port
     */
    public NND(NodeId id, InetAddress address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;

        this.lastSeen = Calendar.getInstance();
    }


    @Override
    public int hashCode() {
        return id.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NND)) return false;
        return id.equals(((NND) obj).id);
    }


    @Override
    public String toString() {
        return id.toString();
    }


    /**
     * Get ID
     *
     * @return  ID
     */
    public NodeId getId() {
        return id;
    }


    /**
     * Get ID length
     *
     * @return  ID length in bit
     */
    public int getIdLength() {
        return id.getLength();
    }


    /**
     * Get address
     *
     * @return  address
     */
    public InetAddress getAddress() {
        return address;
    }


    /**
     * Set address
     *
     * @param   address     address
     */
    void setAddress(InetAddress address) {
        this.address = address;
    }


    /**
     * Get UDP managers port
     *
     * @return  UDP managers port
     */
    public int getPort() {
        return port;
    }


    /**
     * Set UDP managers port
     *
     * @param   port    UDP managers port
     */
    void setPort(int port) {
        this.port = port;
    }


    /**
     * Get last seen date
     *
     * @return  last seen date
     */
    public Calendar getLastSeen() {
        return lastSeen;
    }


    /**
     * Set last seen date
     *
     * @param   lastSeen    last seen date
     */
    void setLastSeen(Calendar lastSeen) {
        this.lastSeen = lastSeen;
    }


    /**
     * Get the distance between this and another node
     *
     * @param   node        node
     * @return  distance between this node and the given node
     */
    public BigInteger distance(NND node) {
        return id.distance(node.id);
    }

}
