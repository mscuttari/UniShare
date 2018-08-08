package it.unishare.common.connection.kademlia.rpc;

import it.unishare.common.connection.kademlia.KademliaFile;
import it.unishare.common.connection.kademlia.KademliaFileData;
import it.unishare.common.connection.kademlia.NND;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class FindData extends Message {

    private static final long serialVersionUID = -1161228654580574231L;

    private KademliaFileData filter;
    private Collection<KademliaFile> files = new HashSet<>();


    /**
     * Constructor
     *
     * @param   source          source node
     * @param   destination     destination node
     */
    public FindData(NND source, NND destination, KademliaFileData filter) {
        super(source, destination);

        this.filter = filter;
    }


    /**
     * Create response message
     *
     * @return  response message
     */
    public FindData createResponse(Collection<KademliaFile> files) {
        FindData response = new FindData(getDestination(), getSource(), filter);
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
