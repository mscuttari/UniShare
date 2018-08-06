package it.unishare.client.utils;

import java.io.File;
import java.util.prefs.Preferences;

public class Settings {

    /**
     * Constructor
     */
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
        return getPreferences().get("dataPath", getDefaultDataPath());
    }


    /**
     * Set data directory path
     *
     * @param   dataPath    data directory path
     */
    public static void setDataPath(String dataPath) {
        getPreferences().put("dataPath", dataPath);
    }


    /**
     * Get default data directory path
     *
     * @return  default data directory path
     */
    private static String getDefaultDataPath() {
        return System.getProperty("user.home") + File.separator + "UniShare" + File.separator;
    }

}
