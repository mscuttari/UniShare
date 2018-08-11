package it.unishare.common.connection.dht;

import it.unishare.common.models.Review;

import java.util.EventListener;
import java.util.List;

/**
 * Reviews listener
 *
 * This interface is used to wait for the response of the node owning the reviews
 */
public interface ReviewsListener extends EventListener {

    /**
     * Called on the response of the file owner node
     *
     * @param   page        reviews page
     * @param   reviews     reiews of that page
     */
    void onResponse(int page, List<Review> reviews);

}
