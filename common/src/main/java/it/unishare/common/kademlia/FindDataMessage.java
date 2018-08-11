package it.unishare.common.kademlia;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

final class FindDataMessage<F extends KademliaFile<FD>, FD extends KademliaFileData> extends Message {

    private static final long serialVersionUID = -1161228654580574231L;

    private FD filter;
    private Collection<F> files = new HashSet<>();


    /**
     * Constructor
     *
     * @param   source          source node
     * @param   destination     destination node
     */
    public FindDataMessage(NND source, NND destination, FD filter) {
        super(source, destination);

        this.filter = filter;
    }


    /**
     * Create response message
     *
     * @return  response message
     */
    public FindDataMessage<F, FD> createResponse(Collection<F> files) {
        FindDataMessage<F, FD> response = new FindDataMessage<>(getDestination(), getSource(), filter);
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
    public FD getFilter() {
        return filter;
    }


    /**
     * Get response files
     *
     * @return  response files
     */
    public Collection<F> getFiles() {
        return Collections.unmodifiableCollection(files);
    }

}
