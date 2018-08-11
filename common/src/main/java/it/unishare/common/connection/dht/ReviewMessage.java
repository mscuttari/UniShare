package it.unishare.common.connection.dht;

import it.unishare.common.kademlia.KademliaFile;
import it.unishare.common.kademlia.Message;
import it.unishare.common.kademlia.NND;
import it.unishare.common.models.Review;

import java.util.ArrayList;
import java.util.List;

public final class ReviewMessage extends Message {

    public enum ReviewMessageType {
        GET,
        SET
    }

    // Serialization
    private static final long serialVersionUID = 647443147573216314L;

    // Basic data
    private ReviewMessageType type;
    private KademliaFile file;

    // Reviews request
    private int page;
    private List<Review> reviews = new ArrayList<>();

    // Set review request
    private Review review;


    /**
     * Constructor for reviews list request
     *
     * @param   source          source node
     * @param   destination     destination node
     * @param   file            file
     * @param   page            reviews page number
     */
    public ReviewMessage(NND source, NND destination, KademliaFile file, int page) {
        super(source, destination);

        this.type = ReviewMessageType.GET;
        this.file = file;
        this.page = page;
    }


    /**
     * Constructor for review save
     *
     * @param   source          source node
     * @param   destination     destination node
     * @param   file            file
     *
     */
    public ReviewMessage(NND source, NND destination, KademliaFile file, Review review) {
        super(source, destination);

        this.type = ReviewMessageType.SET;
        this.file = file;
        this.review = review;
    }


    /**
     * Create response message
     *
     * @return  response message
     */
    public ReviewMessage createResponse() {
        ReviewMessage response;

        if (type == ReviewMessage.ReviewMessageType.GET) {
            response = new ReviewMessage(getDestination(), getSource(), file, page);
        } else {
            response = new ReviewMessage(getDestination(), getSource(), file, review);
        }

        response.setId(getId());

        return response;
    }


    /**
     * Get type
     *
     * @return  message type
     */
    public ReviewMessageType getType() {
        return type;
    }


    /**
     * Get file
     *
     * @return  file
     */
    public KademliaFile getFile() {
        return file;
    }


    /**
     * Get page
     *
     * @return  page
     */
    public int getPage() {
        return page;
    }


    /**
     * Set page
     *
     * @param   page    page
     */
    public void setPage(int page) {
        this.page = page;
    }


    /**
     * Get reviews
     *
     * @return  file reviews
     */
    public List<Review> getReviews() {
        return reviews;
    }


    /**
     * Set reviews
     *
     * @param   reviews     file reviews
     */
    public void setReviews(List<Review> reviews) {
        if (reviews != null) {
            this.reviews.clear();
            this.reviews.addAll(reviews);
        }
    }


    /**
     * Get review
     *
     * @return  review to be saved
     */
    public Review getReview() {
        return review;
    }

}