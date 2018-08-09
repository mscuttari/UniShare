package it.unishare.client.managers;

import it.unishare.client.utils.Settings;
import it.unishare.common.connection.kademlia.FileProvider;
import it.unishare.common.connection.kademlia.KademliaFile;

import java.io.File;

public class FileManager implements FileProvider {

    private long userId;


    /**
     * Constructor
     *
     * @param   userId      user ID
     */
    public FileManager(long userId) {
        this.userId = userId;
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
