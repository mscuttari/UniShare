package it.unishare.client.managers;

import it.unishare.client.utils.Settings;
import it.unishare.common.connection.dht.ReviewsProvider;
import it.unishare.common.kademlia.FilesProvider;
import it.unishare.common.kademlia.KademliaFile;
import it.unishare.common.models.Review;
import it.unishare.common.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesManager implements FilesProvider {

    private User user;


    /**
     * Constructor
     *
     * @param   user    user
     */
    public FilesManager(User user) {
        this.user = user;
    }


    @Override
    public File getFile(KademliaFile file) {
        return new File(getFilePath(file));
    }


    /**
     * Get file path
     *
     * @param   file    file
     * @return  file path
     */
    private String getFilePath(KademliaFile file) {
        return getFilePath(user.getId(), file);
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
