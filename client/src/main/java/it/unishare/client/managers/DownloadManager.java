package it.unishare.client.managers;

import it.unishare.client.layout.Download;
import it.unishare.common.connection.kademlia.KademliaFile;
import it.unishare.common.connection.kademlia.KademliaNode;
import it.unishare.common.utils.LogUtils;
import javafx.collections.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class DownloadManager {

    // Debug
    private static final String TAG = "DownloadManager";

    // Singleton instance
    private static DownloadManager instance;

    private ObservableList<Download> downloads;
    private ExecutorService executorService;


    /**
     * Constructor
     */
    private DownloadManager() {
        downloads = FXCollections.observableList(new ArrayList<>());

        executorService = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
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
     * Get downloads
     *
     * @return  unmodifiable downloads list
     */
    public ObservableList<Download> getDownloads() {
        return FXCollections.unmodifiableObservableList(downloads);
    }


    /**
     * Download file
     *
     * @param   file            file to be downloaded
     * @param   downloadPath    download path
     */
    public void download(KademliaFile file, File downloadPath) {
        LogUtils.d(TAG, "Starting download of file " + file.getKey());

        KademliaNode node = ConnectionManager.getInstance().getNode();
        Future<?> downloadProcess = node.downloadFile(file, downloadPath);
        Download download = new Download(file, downloadPath, downloadProcess);
        executorService.submit(new DownloadTracker(download, downloadProcess));
        downloads.add(download);

        LogUtils.d(TAG, "Download started for file " + file.getKey());
    }


    /**
     * Stop download process
     *
     * @param   download    download to be stopped
     */
    public void stopDownload(Download download) {
        LogUtils.d(TAG, "Stopping download of file " + download.getFile().getKey());
        Future<?> process = download.getProcess();

        if (!process.isDone() && !process.isCancelled())
            process.cancel(true);

        downloads.remove(download);
        LogUtils.d(TAG, "Download stopped for file " + download.getFile().getKey());
    }


    /**
     * Download tracker
     */
    private static class DownloadTracker implements Runnable {

        private final Download download;
        private final Future<?> downloadProcess;


        /**
         * Constructor
         *
         * @param   download            download
         * @param   downloadProcess     download process
         */
        public DownloadTracker(Download download, Future<?> downloadProcess) {
            this.download = download;
            this.downloadProcess = downloadProcess;
        }


        @Override
        public void run() {
            if (download.getStatus() == Download.DownloadStatus.IN_PROGRESS) {
                try {
                    synchronized (download.getFile()) {
                        while (!downloadProcess.isDone()) {
                            wait();
                        }

                        download.setStatus(Download.DownloadStatus.FINISHED);
                    }

                } catch (InterruptedException e) {
                    download.setStatus(Download.DownloadStatus.FAILED);
                    e.printStackTrace();
                }
            }
        }
    }

}