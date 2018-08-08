package it.unishare.common.connection.kademlia.rpc;

import it.unishare.common.connection.kademlia.KademliaFile;
import it.unishare.common.connection.kademlia.NND;

public final class Store extends Message {

    private static final long serialVersionUID = 5859381422717733578L;

    private KademliaFile data;


    /**
     * Constructor
     *
     * @param   source          source node
     * @param   destination     destination node
     * @param   data            data to be stored
     */
    public Store(NND source, NND destination, KademliaFile data) {
        super(source, destination);
        this.data = data;
    }


    /**
     * Create response message
     *
     * @return  response message
     */
    public Store createResponse() {
        Store response = new Store(getDestination(), getSource(), null);
        response.setId(getId());
        return response;
    }


    /**
     * Get data to be stored
     *
     * @return  data
     */
    public KademliaFile getData() {
        return data;
    }


    /**
     * Set data to be stored
     *
     * @param   data    (key, value) data
     */
    public void setData(KademliaFile data) {
        this.data = data;
    }

}