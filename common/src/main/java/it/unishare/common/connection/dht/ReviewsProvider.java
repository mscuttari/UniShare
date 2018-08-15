package it.unishare.common.connection.dht;

import it.unishare.common.kademlia.KademliaFile;
import it.unishare.common.models.Review;

import java.util.List;

public interface ReviewsProvider {

    /**
     * Get file reviews
     *
     * @param   file    file
     * @param   page    reviews page number
     *
     * @return  reviews list
     */
    List<Review> getReviews(KademliaFile file, int page);


    /**
     * Save review for file
     *
     * @param   file        file
     * @param   review      review
     */
    void saveReview(KademliaFile file, Review review);

}
