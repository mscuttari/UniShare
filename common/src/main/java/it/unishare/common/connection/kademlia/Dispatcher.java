package it.unishare.common.connection.kademlia;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

class Dispatcher {

    private Map<Message, MessageListener> listeners;
    private DatagramSocket socket;


    /**
     * Constructor
     *
     * @throws  SocketException in case of initialization error
     */
    public Dispatcher() throws SocketException {
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
        final int TIMEOUT = 10000;

        CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
                objectOutputStream.close();

                byte[] data = byteOutputStream.toByteArray();
                NND destination = message.getDestination();
                DatagramPacket packet = new DatagramPacket(data, data.length, destination.getAddress(), destination.getPort());
                socket.send(packet);

                listeners.put(message, listener);

            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null)
                    listener.onFailure();
            }

            if (listener != null) {
                new Timer(true).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (listeners.containsKey(message))
                            listener.onFailure();
                    }
                }, TIMEOUT);
            }
        });
    }


    /**
     * Check if a message is a response
     *
     * @param   message     message
     * @return  true if the message is a response to a previously sent message
     */
    public boolean isResponse(Message message) {
        return listeners.containsKey(message);
    }


    /**
     * Dispatch received message
     *
     * @param   message     received message
     */
    public void dispatch(Message message) {
        MessageListener listener = listeners.get(message);

        if (listener != null)
            listener.onSuccess(message);

        listeners.remove(message);
    }

}
