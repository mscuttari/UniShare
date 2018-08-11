package it.unishare.common.kademlia;

import java.util.EventListener;
import java.util.List;

/**
 * Lookup process listener
 *
 * {@link #finish(List)} is called when the nearest nodes list has not changed for a while and
 * therefore can be considered stable
 */
interface LookupListener extends EventListener {

    /**
     * Called when the nearest nodes list can be considered stable
     *
     * @param   nearestNodes    unmodifiable list of the nearest nodes to the lookup target ID
     */
    void finish(List<NND> nearestNodes);
}