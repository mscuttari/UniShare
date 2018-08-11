package it.unishare.client.utils;

import java.io.File;
import java.util.prefs.Preferences;

public class Settings {

    private static final String DATA_PATH_SETTING = "dataPath";
    private static final String SERVER_ADDRESS_SETTING = "serverAddress";
    private static final String SERVER_PORT_SETTING = "serverPort";


    private Settings() {

    }


    /**
     * Get preferences
     *
     * @return  preferences
     */
    private static Preferences getPreferences() {
        return Preferences.userNodeForPackage(Settings.class);
    }


    /**
     * Get data directory path
     *
     * @return  data directory path
     */
    public static String getDataPath() {
        return getPreferences().get(DATA_PATH_SETTING, getDefaultDataPath());
    }


    /**
     * Set data directory path
     *
     * @param   dataPath    data directory path
     */
    public static void setDataPath(String dataPath) {
        getPreferences().put(DATA_PATH_SETTING, dataPath);
    }


    /**
     * Get default data directory path
     *
     * @return  default data directory path
     */
    private static String getDefaultDataPath() {
        return System.getProperty("user.home") + File.separator + "UniShare";
    }


    /**
     * Get server address
     *
     * @return  server address
     */
    public static String getServerAddress() {
        return getPreferences().get(SERVER_ADDRESS_SETTING, "127.0.0.1");
    }


    /**
     * Set server address
     *
     * @param   serverAddress   server address
     */
    public static void setServerAddress(String serverAddress) {
        getPreferences().put(SERVER_ADDRESS_SETTING, serverAddress);
    }


    /**
     * Get server port
     *
     * @return  server port
     */
    public static String getServerPort() {
        return getPreferences().get(SERVER_PORT_SETTING, "1099");
    }


    /**
     * Set server port
     *
     * @param   serverPort      server port
     */
    public static void setServerPort(String serverPort) {
        getPreferences().put(SERVER_PORT_SETTING, serverPort);
    }

}
