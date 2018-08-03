package it.unishare.common.connection.kademlia;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class NodeId implements Serializable {

    private static final long serialVersionUID = 4410419515554173134L;

    private static final int ID_LENGTH = 160;
    private byte[] keyBytes;


    /**
     * Default constructor to generate a random key
     */
    public NodeId() {
        keyBytes = new byte[ID_LENGTH / 8];
        new Random().nextBytes(keyBytes);
    }


    /**
     * Generate the node ID from a given byte array
     *
     * @param   bytes       byte array to be used for the ID
     */
    private NodeId(byte[] bytes) {
        if (bytes.length != ID_LENGTH / 8) {
            throw new IllegalArgumentException("Data need to be " + (ID_LENGTH / 8) + " characters long");
        }

        this.keyBytes = bytes;
    }


    /**
     * Constructor
     *
     * @param   data    string data
     */
    public NodeId(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(data.getBytes());
            this.keyBytes = messageDigest.digest();

        } catch (NoSuchAlgorithmException e) {
            if (data.getBytes().length != ID_LENGTH / 8) {
                throw new IllegalArgumentException("Data need to be " + (ID_LENGTH / 8) + " characters long");
            }

            this.keyBytes = data.getBytes();
        }
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Arrays.hashCode(this.keyBytes);
        return hash;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NodeId) {
            NodeId nid = (NodeId) obj;
            return this.hashCode() == nid.hashCode();
        }

        return false;
    }


    @Override
    public String toString() {
        return getInt().toString();
    }


    /**
     * Get ID length
     *
     * @return  ID length in bit
     */
    public int getLength() {
        return ID_LENGTH;
    }


    /**
     * Get the 20 bytes (160 bit / 8) of the key
     *
     * @return  key bytes
     */
    public byte[] getBytes() {
        return keyBytes;
    }


    /**
     * Get the integer representation of the key
     *
     * @return  integer representation
     */
    public BigInteger getInt() {
        return new BigInteger(1, this.getBytes());
    }


    /**
     * Get the XOR result between this and another node ID
     *
     * @param   nodeId      node ID
     * @return  XOR result
     */
    public NodeId xor(NodeId nodeId) {
        byte[] distance = new byte[ID_LENGTH / 8];
        byte[] bytes = nodeId.getBytes();

        for (int i = 0; i < ID_LENGTH / 8; i++) {
            distance[i] = (byte) (this.keyBytes[i] ^ bytes[i]);
        }

        return new NodeId(distance);
    }


    /**
     * Get the distance between this and another node ID
     *
     * @param   nodeId      node ID
     * @return  distance between this node ID and the given node ID
     */
    public BigInteger distance(NodeId nodeId) {
        NodeId xor = xor(nodeId);
        return new BigInteger(1, xor.getBytes());
    }


    /**
     * Get the number of leading 0s
     *
     * @return  number of leading 0s
     */
    public int getLeadingZeros() {
        int leadingZeros = 0;

        for (byte b : this.keyBytes) {
            if (b == 0) {
                leadingZeros += 8;
            } else {
                int count = 0;
                for (int i = 7; i >= 0; i--) {
                    boolean a = (b & (1 << i)) == 0;

                    if (a) {
                        count++;
                    } else {
                        break;
                    }
                }

                leadingZeros += count;
                break;
            }
        }
        return leadingZeros;
    }

}
