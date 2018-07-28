package it.unishare.common.connection.kademlia;

import it.unishare.common.connection.kademlia.rpc.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

class Dispatcher {

    private Map<Long, MessageListener> listeners;
    private DatagramSocket socket;


    /**
     * Constructor
     *
     * @throws  Exception   in case of initialization error
     */
    public Dispatcher() throws Exception {
        this.listeners = new HashMap<>();
        this.socket = new DatagramSocket();
    }


    /**
     * Send message
     *
     * @param   message         message to be sent
     */
    public void sendMessage(Message message) {
        sendMessage(message, null);
    }


    /**
     * Send message
     *
     * @param   message         message to be sent
     * @param   listener        response listener
     */
    public void sendMessage(Message message, MessageListener listener) {
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            objectOutputStream.close();

            byte[] data = byteOutputStream.toByteArray();
            NND destination = message.getDestination();
            DatagramPacket packet = new DatagramPacket(data, data.length, destination.getIp(), destination.getPort());
            socket.send(packet);

            listeners.put(message.getId(), listener);

        } catch (Exception e) {
            if (listener != null)
                listener.onFailure();
        }
    }


    /**
     * Check if a message is a response
     *
     * @param   message     message
     * @return  true if the message is a response to a previously sent message
     */
    public boolean isResponse(Message message) {
        return listeners.containsKey(message.getId());
    }


    /**
     * Dispatch received message
     *
     * @param   message     received message
     */
    public void dispatch(Message message) {
        MessageListener listener = listeners.get(message.getId());

        if (listener != null)
            listener.onSuccess();

        listeners.remove(message.getId());
    }


    public static abstract class MessageListener {
        public abstract void onSuccess();
        public abstract void onFailure();
    }

}
