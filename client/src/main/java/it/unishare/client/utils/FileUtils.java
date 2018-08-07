package it.unishare.client.utils;

import java.io.*;

public class FileUtils {

    private FileUtils() {

    }


    /**
     * Copy file
     *
     * @param   source          source file
     * @param   destination     destination file
     */
    public static void copyFile(File source, File destination) {
        try {
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(source));
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(destination));

            byte[] buffer = new byte[1024 * 1024];

            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }

            is.close();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
