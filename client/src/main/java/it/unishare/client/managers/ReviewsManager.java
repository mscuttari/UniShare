package it.unishare.client.managers;

import it.unishare.common.connection.dht.ReviewsProvider;
import it.unishare.common.kademlia.KademliaFile;
import it.unishare.common.models.Review;
import it.unishare.common.models.User;

import java.util.ArrayList;
import java.util.List;

public class ReviewsManager implements ReviewsProvider {

    private User user;


    /**
     * Constructor
     *
     * @param   user    user
     */
    public ReviewsManager(User user) {
        this.user = user;
    }


    @Override
    public List<Review> getReviews(KademliaFile file, int page) {
        if (user == null)
            return new ArrayList<>();

        List<Review> allReviews = DatabaseManager.getInstance().getFileReviews(user, file);

        int from = (page - 1) * 10;
        int to = page * 10 - 1;

        List<Review> pageReviews;

        if (allReviews.size() >= to + 1) {
            pageReviews = allReviews.subList(from, to + 1);
        } else if (allReviews.size() < from) {
            pageReviews = new ArrayList<>();
        } else {
            pageReviews = allReviews.subList(from, allReviews.size());
        }

        return new ArrayList<>(pageReviews);
    }


    @Override
    public void saveReview(KademliaFile file, Review review) {
        if (user != null)
            DatabaseManager.getInstance().saveReview(user, file, review);
    }

}
