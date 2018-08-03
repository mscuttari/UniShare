package it.unishare.common.connection.kademlia;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class KademliaFile implements Serializable {

    private static final long serialVersionUID = 802256779636121461L;

    private String clearKey;
    private NodeId key;
    private NND owner;


    /**
     * Constructor
     *
     * @param   key     key
     * @param   owner   owner of the data
     */
    public KademliaFile(String key, NND owner) {
        this.clearKey = key;
        this.key = new NodeId(key);
        this.owner = owner;
    }


    /**
     * Get clear key
     *
     * @return  clear key
     */
    public String getClearKey() {
        return clearKey;
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

}
