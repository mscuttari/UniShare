package it.unishare.common.connection.kademlia;

import java.util.Collection;
import java.util.EventListener;

/**
 * File search process listener
 *
 * {@link #found(Collection)} is called when a new file matching the search filter provided is found
 */
public interface SearchListener extends EventListener {

    /**
     * Called when a new file matching the search filter is found
     *
     * @param   files   unmodifiable lsit of all the found files matching the search filter
     */
    void found(Collection<KademliaFile> files);

}