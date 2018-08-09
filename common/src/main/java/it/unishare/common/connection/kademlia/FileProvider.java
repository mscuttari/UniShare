package it.unishare.common.connection.kademlia;

import java.io.File;

/**
 * File provider interface
 *
 * This is used by the node to get the file to be uploaded
 */
public interface FileProvider {

    /**
     * Get real file given Kademlia representation
     *
     * @param   file    file data
     * @return  real file (null if not found)
     */
    File getFile(KademliaFile file);

}
