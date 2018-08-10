package it.unishare.client.layout;

import it.unishare.common.connection.kademlia.KademliaFile;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Future;

public class Download {

    /**
     * Download status
     */
    public enum DownloadStatus {
        IN_PROGRESS,
        FINISHED,
        FAILED
    }

    private KademliaFile file;
    private File path;
    private DownloadStatus status;
    private Future<?> process;
    private Collection<DownloadListener> listeners = new HashSet<>();


    /**
     * Constructor for finished download
     *
     * @param   file    downloaded file
     */
    public Download(KademliaFile file, File path) {
        this(file, path, DownloadStatus.FINISHED, null);
    }


    /**
     * Constructor for download in progress
     *
     * @param   file                file
     * @param   downloadProcess     download process
     */
    public Download(KademliaFile file, File path, Future<?> downloadProcess) {
        this(file, path, DownloadStatus.IN_PROGRESS, downloadProcess);
    }


    /**
     * Constructor for download in progress
     *
     * @param   file                file
     * @param   path                destination path
     * @param   initialStatus       initial status
     * @param   downloadProcess     download process
     */
    public Download(KademliaFile file, File path, DownloadStatus initialStatus, Future<?> downloadProcess) {
        this.file = file;
        this.path = path;
        this.status = initialStatus;
        this.process = downloadProcess;
    }


    /**
     * Get downloaded file
     *
     * @return  downloaded file
     */
    public KademliaFile getFile() {
        return file;
    }


    /**
     * Get download path
     *
     * @return  download path
     */
    public File getPath() {
        return path;
    }


    /**
     * Get download status
     *
     * @return  download status
     */
    public DownloadStatus getStatus() {
        return status;
    }


    /**
     * Set download status
     *
     * @param   status      download status
     */
    public void setStatus(DownloadStatus status) {
        DownloadStatus oldValue = this.status;
        this.status = status;
        listeners.forEach(listener -> listener.onStatusChange(status, oldValue));
    }


    /**
     * Add status change listener
     *
     * @param   listener    listener
     */
    public void addStatusChangeListener(DownloadListener listener) {
        listeners.add(listener);
    }


    /**
     * Remove status change listener
     *
     * @param   listener    listener
     */
    public void removeStatusChangeListener(DownloadListener listener) {
        listeners.remove(listener);
    }


    /**
     * Get download process
     *
     * @return  download process
     */
    public Future<?> getProcess() {
        return process;
    }


    /**
     * Download status change listener
     */
    public interface DownloadListener {

        /**
         * Called on status change
         *
         * @param   newValue    new status
         * @param   oldValue    old status
         */
        void onStatusChange(DownloadStatus newValue, DownloadStatus oldValue);
    }

}