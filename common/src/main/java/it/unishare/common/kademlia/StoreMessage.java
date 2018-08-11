package it.unishare.common.kademlia;

final class StoreMessage<F extends KademliaFile<FD>, FD extends KademliaFileData> extends Message {

    private static final long serialVersionUID = 5859381422717733578L;

    private F data;


    /**
     * Constructor
     *
     * @param   source          source node
     * @param   destination     destination node
     * @param   data            data to be stored
     */
    public StoreMessage(NND source, NND destination, F data) {
        super(source, destination);
        this.data = data;
    }


    /**
     * Create response message
     *
     * @return  response message
     */
    public StoreMessage createResponse() {
        StoreMessage<F, FD> response = new StoreMessage<>(getDestination(), getSource(), null);
        response.setId(getId());
        return response;
    }


    /**
     * Get data to be stored
     *
     * @return  data
     */
    public F getData() {
        return data;
    }


    /**
     * Set data to be stored
     *
     * @param   data    (key, value) data
     */
    public void setData(F data) {
        this.data = data;
    }

}