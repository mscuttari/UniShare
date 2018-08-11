package it.unishare.common.connection.kademlia;

import it.unishare.common.utils.LogUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;

class Uploader implements Runnable {

    // Debug
    private static final String TAG = "Uploader";

    private KademliaNode uploader;
    private Socket socket;


    /**
     * Constructor
     *
     * @param   uploader    uploader node
     * @param   socket      request socket
     */
    Uploader(KademliaNode uploader, Socket socket) {
        this.uploader = uploader;
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            OutputStream out = socket.getOutputStream();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object obj = in.readObject();

            if ((obj instanceof KademliaFile)) {
                KademliaFile requestedFile = (KademliaFile) obj;
                sendFile(requestedFile, out);
            }

            in.close();
            out.close();
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Send file
     *
     * @param   file    file to be sent
     * @param   out     output stream
     */
    private void sendFile(KademliaFile file, OutputStream out) {
        LogUtils.d(TAG, "Received request for file " + file.getKey());

        if (!uploader.getInfo().equals(file.getOwner()))
            return;

        FileProvider fileProvider = uploader.getFileProvider();
        if (fileProvider == null)
            return;

        File myFile = fileProvider.getFile(file);

        LogUtils.d(TAG, "Uploading the file " + file.getKey());

        try (InputStream in = new FileInputStream(myFile)) {
            IOUtils.copy(in, out);
            LogUtils.d(TAG, "Upload completed for the file " + file.getKey());

        } catch (IOException e) {
            LogUtils.e(TAG, "Upload failed for the file " + file.getKey());
            e.printStackTrace();
        }
    }

}
