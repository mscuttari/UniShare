package it.unishare.common.connection.dht;

import it.unishare.common.kademlia.*;
import it.unishare.common.models.Review;

public final class UniShareNode extends KademliaNode<NoteFile, NoteMetadata> {

    /**
     * Default constructor
     */
    public UniShareNode() {
        this(null);
    }


    /**
     * Constructor
     *
     * @param   fileProvider    files and reviews provider
     */
    public UniShareNode(FilesProvider fileProvider) {
        super(fileProvider);
    }


    @Override
    protected void onMessageReceived(Message message) {
        if (message instanceof ReviewMessage) {
            // REVIEW
            log("Review message received from " + message.getSource().getId() + " for " + ((ReviewMessage) message).getFile().getKey());
            ReviewMessage response = ((ReviewMessage) message).createResponse();

            if (response.getType() == ReviewMessage.ReviewMessageType.GET) {
                response.setReviews(getFileProvider().getReviews(response.getFile(), response.getPage()));

            } else if (response.getType() == ReviewMessage.ReviewMessageType.SET) {
                getFileProvider().saveReview(response.getFile(), response.getReview());
            }

            sendMessage(response);
        }
    }


    /**
     * Get file reviews
     *
     * @param   file    file
     * @param   page    reviews page number
     */
    public void getFileReviews(NoteFile file, int page, ReviewsListener listener) {
        ReviewMessage message = new ReviewMessage(getInfo(), file.getOwner(), file, page);

        sendMessage(message, new MessageListener() {
            @Override
            public void onSuccess(Message response) {
                if (listener != null && response instanceof ReviewMessage) {
                    listener.onResponse(((ReviewMessage) response).getPage(), ((ReviewMessage) response).getReviews());
                }
            }

            @Override
            public void onFailure() {
                ping(message.getDestination());
            }
        });
    }


    /**
     * Send review
     *
     * @param   file        file
     * @param   review      review
     */
    public void sendReview(NoteFile file, Review review) {
        log("Sending review for file " + file.getKey());
        ReviewMessage message = new ReviewMessage(getInfo(), file.getOwner(), file, review);
        sendMessage(message);
    }

}
