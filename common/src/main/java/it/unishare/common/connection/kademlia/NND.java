package it.unishare.common.connection.kademlia;

import java.net.InetAddress;
import java.util.Calendar;

/**
 * Network Node Data
 */
public class NND {

    /** ID */
    private long id;

    /** IP address */
    private InetAddress ip;

    /** Connection port */
    private int port;

    /** Last seen */
    private Calendar lastSeen;


    /**
     * Constructor
     *
     * @param   id      ID
     * @param   ip      IP address
     * @param   port    connection port
     */
    public NND(long id, InetAddress ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;

        this.lastSeen = Calendar.getInstance();
    }


    @Override
    public int hashCode() {
        return (int) id;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NND)) return false;
        return id == ((NND) obj).id;
    }


    /**
     * Get ID
     *
     * @return  ID
     */
    public long getId() {
        return id;
    }


    /**
     * Get IP address
     *
     * @return  IP address
     */
    public InetAddress getIp() {
        return ip;
    }


    /**
     * Set IP address
     *
     * @param   ip      IP address
     */
    public void setIp(InetAddress ip) {
        this.ip = ip;
    }


    /**
     * Get UDP connection port
     *
     * @return  UDP connection port
     */
    public int getPort() {
        return port;
    }


    /**
     * Set UDP connection port
     *
     * @param   port    UDP connection port
     */
    public void setPort(int port) {
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
    public void setLastSeen(Calendar lastSeen) {
        this.lastSeen = lastSeen;
    }

}
