package it.unishare.common.connection.kademlia.rpc;

import it.unishare.common.connection.kademlia.NND;

public final class Ping extends Message {

    private static final long serialVersionUID = 5524791019372047864L;


    /**
     * Constructor
     *
     * @param   source          source node
     * @param   destination     destination node
     */
    public Ping(NND source, NND destination) {
        super(source, destination);
    }


    /**
     * Create response message
     *
     * @return  response message
     */
    public Ping createResponse() {
        Ping response = new Ping(getDestination(), getSource());
        response.setId(getId());
        return response;
    }

}
