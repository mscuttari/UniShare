package it.unishare.common.connection.kademlia.rpc;

import it.unishare.common.connection.kademlia.NND;

public final class Ping extends Message<Ping> {

    /**
     * Constructor
     *
     * @param   source          source node
     * @param   destination     destination node
     */
    public Ping(NND source, NND destination) {
        super(source, destination);
    }


    @Override
    public Ping createResponse() {
        Ping response = new Ping(getDestination(), getSource());
        response.setId(getId());
        return response;
    }

}
