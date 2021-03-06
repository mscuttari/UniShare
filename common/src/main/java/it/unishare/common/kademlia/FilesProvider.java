package it.unishare.common.kademlia;

import it.unishare.common.models.Review;

import java.io.File;
import java.util.List;

/**
 * File provider interface
 *
 * This is used by the node to get the file to be uploaded and to get / set file reviews
 */
public interface FilesProvider {

    /**
     * Get real file given Kademlia representation
     *
     * @param   file    file data
     * @return  real file (null if not found)
     */
    File getFile(KademliaFile file);

}
