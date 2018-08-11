package it.unishare.common.connection.kademlia;

import java.io.Serializable;

public class KademliaFile implements Serializable {

    private static final long serialVersionUID = 802256779636121461L;

    private NodeId key;
    private NND owner;

    private KademliaFileData data;


    /**
     * Constructor
     *
     * @param   key     key
     * @param   owner   owner of the data
     */
    public KademliaFile(byte[] key, NND owner, KademliaFileData data) {
        this.key = new NodeId(key);
        this.owner = owner;
        this.data = data;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KademliaFile))
            return false;

        return getKey().equals(((KademliaFile) obj).getKey());
    }


    @Override
    public int hashCode() {
        return getKey().hashCode();
    }


    @Override
    public String toString() {
        return "{" + key + ", " + owner + ", " + data + "}";
    }


    /**
     * Get key
     *
     * @return  key
     */
    public NodeId getKey() {
        return key;
    }


    /**
     * Get data owner
     *
     * @return  data owner
     */
    public NND getOwner() {
        return owner;
    }


    /**
     * Get file data
     *
     * @return  file data
     */
    public KademliaFileData getData() {
        return data;
    }

}
