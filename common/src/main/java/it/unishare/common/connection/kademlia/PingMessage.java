package it.unishare.common.connection.kademlia;

final class PingMessage extends Message {

    private static final long serialVersionUID = 5524791019372047864L;


    /**
     * Constructor
     *
     * @param   source          source node
     * @param   destination     destination node
     */
    public PingMessage(NND source, NND destination) {
        super(source, destination);
    }


    /**
     * Create response message
     *
     * @return  response message
     */
    public PingMessage createResponse() {
        PingMessage response = new PingMessage(getDestination(), getSource());
        response.setId(getId());
        return response;
    }

}
