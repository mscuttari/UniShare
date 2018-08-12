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
            ReviewMessage response = ((ReviewMessage) message).createResponse();

            FilesProvider filesProvider = getFilesProvider();
            if (filesProvider == null) return;

            switch (response.getType()) {
                case GET:
                    log("Reviews list requested from " + message.getSource().getId() + " for " + ((ReviewMessage) message).getFile());
                    response.setReviews(filesProvider.getReviews(response.getFile(), response.getPage()));
                    break;

                case SET:
                    log("Saving review " + ((ReviewMessage) message).getReview() + " for file " + ((ReviewMessage) message).getFile());
                    filesProvider.saveReview(response.getFile(), response.getReview());
                    response.setReviews(filesProvider.getReviews(((ReviewMessage) message).getFile(), 1));
                    break;
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
                listener.onFailure();
            }
        });
    }


    /**
     * Send review
     *
     * @param   file        file
     * @param   review      review
     */
    public void sendReview(NoteFile file, Review review, ReviewsListener listener) {
        log("Sending review for file " + file.getKey());
        ReviewMessage message = new ReviewMessage(getInfo(), file.getOwner(), file, review);

        sendMessage(message, new MessageListener() {
            @Override
            public void onSuccess(Message response) {
                if (response instanceof ReviewMessage)
                    listener.onResponse(1, ((ReviewMessage) response).getReviews());
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

}
