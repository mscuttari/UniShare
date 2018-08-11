package it.unishare.common.connection.kademlia;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

final class FindDataMessage extends Message {

    private static final long serialVersionUID = -1161228654580574231L;

    private KademliaFileData filter;
    private Collection<KademliaFile> files = new HashSet<>();


    /**
     * Constructor
     *
     * @param   source          source node
     * @param   destination     destination node
     */
    public FindDataMessage(NND source, NND destination, KademliaFileData filter) {
        super(source, destination);

        this.filter = filter;
    }


    /**
     * Create response message
     *
     * @return  response message
     */
    public FindDataMessage createResponse(Collection<KademliaFile> files) {
        FindDataMessage response = new FindDataMessage(getDestination(), getSource(), filter);
        response.setId(getId());

        if (files != null) {
            response.files.clear();
            response.files.addAll(files);
        }

        return response;
    }


    /**
     * Get search filter
     *
     * @return  search filter
     */
    public KademliaFileData getFilter() {
        return filter;
    }


    /**
     * Get response files
     *
     * @return  response files
     */
    public Collection<KademliaFile> getFiles() {
        return Collections.unmodifiableCollection(files);
    }

}
