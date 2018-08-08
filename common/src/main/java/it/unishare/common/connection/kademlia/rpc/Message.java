package it.unishare.common.connection.kademlia.rpc;

import it.unishare.common.connection.kademlia.NND;

import java.io.Serializable;
import java.util.UUID;

/**
 * Remote Procedure Call
 */
public abstract class Message implements Serializable {

    private static final long serialVersionUID = -4088040739038751081L;

    private long id;
    private NND source, destination;


    /**
     * Constructor
     *
     * @param   source          source node
     * @param   destination     destination node
     */
    public Message(NND source, NND destination) {
        this.id = generateId();
        this.source = source;
        this.destination = destination;
    }


    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Message)) return false;
        return id == ((Message) obj).id;
    }


    /**
     * Get message ID
     *
     * @return  ID
     */
    public long getId() {
        return id;
    }


    /**
     * Set message ID
     *
     * @param   id      message ID
     */
    protected void setId(long id) {
        this.id = id;
    }


    /**
     * Get source node
     *
     * @return  source node
     */
    public NND getSource() {
        return source;
    }


    /**
     * Get destination node
     *
     * @return  destination node
     */
    public NND getDestination() {
        return destination;
    }


    /**
     * Generate ID for the message
     *
     * @return  unique ID
     */
    private static long generateId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

}
