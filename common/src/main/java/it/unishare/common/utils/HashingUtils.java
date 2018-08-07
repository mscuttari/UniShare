package it.unishare.common.utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtils {

    private HashingUtils() {

    }


    /**
     * Get SHA-1 fingerprint of a file
     *
     * @param   filePath    file path
     * @return  SHA-1 fingerprint
     */
    public static byte[] fileSHA1(String filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            File file = new File(filePath);

            InputStream fis = new FileInputStream(file);
            int n = 0;
            byte[] buffer = new byte[8192];

            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    digest.update(buffer, 0, n);
                }
            }

            return digest.digest();

        } catch (NoSuchAlgorithmException | IOException e) {
            return null;
        }
    }

}
