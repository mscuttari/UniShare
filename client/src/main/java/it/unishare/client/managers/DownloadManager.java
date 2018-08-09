package it.unishare.client.managers;

import com.google.common.util.concurrent.MoreExecutors;
import it.unishare.common.connection.kademlia.KademliaFile;
import it.unishare.common.connection.kademlia.KademliaNode;

import java.io.File;
import java.util.concurrent.*;

public class DownloadManager {

    // Singleton instance
    private static DownloadManager instance;
    private ExecutorService executorService;


    /**
     * Constructor
     */
    private DownloadManager() {
        // The executor will let its tasks finish, and at the same time will automatically
        // call their shutdown() method when application is complete

        executorService = MoreExecutors.getExitingExecutorService(
                (ThreadPoolExecutor) Executors.newCachedThreadPool(),
                Integer.MAX_VALUE, TimeUnit.DAYS  // Period after which executor will be automatically closed
        );
    }


    /**
     * Get singleton instance
     *
     * @return  singleton instance
     */
    public static DownloadManager getInstance() {
        if (instance == null)
            instance = new DownloadManager();

        return instance;
    }


    /**
     * Download file
     *
     * @param   file            file to be downloaded
     * @param   downloadPath    download path
     */
    public void download(KademliaFile file, File downloadPath) {
        KademliaNode node = ConnectionManager.getInstance().getNode();
        node.downloadFile(file, downloadPath);
    }

}
