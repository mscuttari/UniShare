package it.unishare.common.connection.dht;

import it.unishare.common.kademlia.KademliaFile;
import it.unishare.common.kademlia.NND;

public final class NoteFile extends KademliaFile<NoteMetadata> {

    // Serialization
    private static final long serialVersionUID = -6075419551224314714L;


    /**
     * Constructor
     *
     * @param   key     key
     * @param   owner   owner of the data
     */
    public NoteFile(byte[] key, NND owner, NoteMetadata data) {
        super(key, owner, data);
    }


    @Override
    public boolean matchesFilter(NoteMetadata filter) {
        if (filter.getTitle() != null && !filter.getTitle().equals(getData().getTitle()))
            return false;

        if (filter.getAuthor() != null && !filter.getAuthor().equals(getData().getAuthor()))
            return false;

        if (filter.getUniversity() != null && !filter.getUniversity().equals(getData().getUniversity()))
            return false;

        if (filter.getDepartment() != null && !filter.getDepartment().equals(getData().getDepartment()))
            return false;

        if (filter.getCourse() != null && !filter.getCourse().equals(getData().getCourse()))
            return false;

        if (filter.getTeacher() != null && !filter.getTeacher().equals(getData().getTeacher()))
            return false;

        return true;
    }

}
