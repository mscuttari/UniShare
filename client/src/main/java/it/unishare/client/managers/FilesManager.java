package it.unishare.client.managers;

import it.unishare.client.utils.Settings;
import it.unishare.common.connection.kademlia.FilesProvider;
import it.unishare.common.connection.kademlia.KademliaFile;
import it.unishare.common.models.Review;
import it.unishare.common.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesManager implements FilesProvider {

    private long userId;


    /**
     * Constructor
     *
     * @param   userId      user ID
     */
    public FilesManager(long userId) {
        this.userId = userId;
    }


    @Override
    public File getFile(KademliaFile file) {
        return new File(getFilePath(file));
    }


    @Override
    public List<Review> getReviews(KademliaFile file, int page) {
        User user = ConnectionManager.getInstance().getUser();

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
        User user = ConnectionManager.getInstance().getUser();

        if (user == null)
            return;

        DatabaseManager.getInstance().saveReview(user, file, review);
    }


    /**
     * Get file path
     *
     * @param   file    file
     * @return  file path
     */
    private String getFilePath(KademliaFile file) {
        return getFilePath(userId, file);
    }


    /**
     * Get file path
     *
     * @param   userId      user ID
     * @param   file        file
     *
     * @return  file path
     */
    public static String getFilePath(long userId, KademliaFile file) {
        String fileName = file.getKey().toString() + ".pdf";
        return Settings.getDataPath() + File.separator + userId + "_" + fileName;
    }

}
