package it.unishare.common.connection.kademlia;

import it.unishare.common.utils.LogUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

class Downloader implements Runnable {

    // Debug
    private static final String TAG = "Downloader";

    private KademliaFile file;
    private File destination;


    /**
     * Constructor
     *
     * @param   file            file to be downloaded
     * @param   destination     where the file should be saved
     */
    public Downloader(KademliaFile file, File destination) {
        this.file = file;
        this.destination = destination;
    }


    @Override
    public void run() {
        try {
            InetAddress address = file.getOwner().getAddress();
            int port = file.getOwner().getPort();

            LogUtils.d(TAG, "Connecting to " + address.toString() + ":" + port);
            Socket socket = new Socket(address, port);

            InputStream in = socket.getInputStream();
            OutputStream fileOut = new FileOutputStream(destination);

            LogUtils.d(TAG, "Sending request for file " + file.getKey());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(file);

            LogUtils.d(TAG, "Downloading the file " + file.getKey());
            IOUtils.copy(in, fileOut);

            in.close();
            out.close();
            fileOut.close();
            socket.close();

            LogUtils.d(TAG, "Download completed for the file " + file.getKey());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}