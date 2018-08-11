package it.unishare.common.kademlia;

import java.util.Collections;
import java.util.List;

final class FindNodeMessage extends Message {

    private static final long serialVersionUID = -8238542814034385851L;

    private NodeId targetId;
    private List<NND> nearestNodes;


    /**
     * Constructor
     *
     * @param   source          source node
     * @param   destination     destination node
     */
    public FindNodeMessage(NND source, NND destination, NodeId targetId) {
        super(source, destination);
        this.targetId = targetId;
    }


    /**
     * Create response message
     *
     * @return  response message
     */
    public FindNodeMessage createResponse() {
        FindNodeMessage response = new FindNodeMessage(getDestination(), getSource(), targetId);
        response.setId(getId());
        return response;
    }


    /**
     * Get target node ID
     *
     * @return  target node ID
     */
    public NodeId getTargetId() {
        return targetId;
    }


    /**
     * Get nearest nodes
     *
     * @return  nearest nodes
     */
    public List<NND> getNearestNodes() {
        return Collections.unmodifiableList(nearestNodes);
    }


    /**
     * Set nearest nodes
     *
     * @param   nearestNodes    nearest nodes
     */
    public void setNearestNodes(List<NND> nearestNodes) {
        this.nearestNodes = nearestNodes;
    }

}
