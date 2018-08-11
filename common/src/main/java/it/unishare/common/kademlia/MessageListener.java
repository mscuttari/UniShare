package it.unishare.common.kademlia;

import java.util.EventListener;

/**
 * Message response listener interface
 */
public interface MessageListener extends EventListener {

    /**
     * Called on message response
     *
     * @param   response    response
     */
    void onSuccess(Message response);


    /**
     * Called if the message could not be sent or if timeout has expired
     */
    void onFailure();

}