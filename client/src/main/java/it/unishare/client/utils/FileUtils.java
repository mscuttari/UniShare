package it.unishare.client.utils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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


    /**
     * Read file lines
     *
     * @param   fileURL     file URL
     * @return  lines
     */
    public static List<String> readFileLines(URL fileURL) {
        List<String> result = new ArrayList<>();

        try {
            URI fileURI = fileURL.toURI();
            File file = new File(fileURI);
            BufferedReader input = new BufferedReader(new FileReader(file));

            String line;
            while ((line = input.readLine()) != null) {
                if (!line.isEmpty())
                    result.add(line);
            }

            input.close();

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Read file lines
     *
     * @param   inputStream     file input stream
     * @return  lines
     */
    public static List<String> readFileLines(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader.lines().collect(Collectors.toList());
    }

}
