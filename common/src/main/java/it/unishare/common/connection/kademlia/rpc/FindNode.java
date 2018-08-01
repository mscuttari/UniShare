package it.unishare.common.connection.kademlia.rpc;

import it.unishare.common.connection.kademlia.NND;
import it.unishare.common.connection.kademlia.NodeId;

import java.util.Collections;
import java.util.List;

public final class FindNode extends Message<FindNode> {

    private static final long serialVersionUID = -8238542814034385851L;

    private NodeId targetId;
    private List<NND> nearestNodes;


    /**
     * Constructor
     *
     * @param   source          source node
     * @param   destination     destination node
     */
    public FindNode(NND source, NND destination, NodeId targetId) {
        super(source, destination);
        this.targetId = targetId;
    }


    @Override
    public FindNode createResponse() {
        FindNode response = new FindNode(getDestination(), getSource(), targetId);
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
