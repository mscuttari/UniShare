package it.unishare.common.connection.kademlia;

import it.unishare.common.models.Review;

import java.util.EventListener;
import java.util.List;

public interface ReviewsListener extends EventListener {

    void onResponse(int page, List<Review> reviews);

}
