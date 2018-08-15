package it.unishare.client.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SettingsTest {

    private String backupDataPath;
    private String backupServerAddress;
    private String backupServerPort;


    @Before
    public void backupSettings() {
        backupDataPath = Settings.getDataPath();
        backupServerAddress = Settings.getServerAddress();
        backupServerPort = Settings.getServerPort();
    }


    @Test
    public void dataPathPersistenceTest() {
        Settings.setDataPath("C:\\UniShare");
        assertEquals(Settings.getDataPath(), "C:\\UniShare");
    }


    @Test
    public void serverAddressPersistenceTest() {
        Settings.setServerAddress("127.0.0.1");
        assertEquals(Settings.getServerAddress(), "127.0.0.1");
    }


    @Test
    public void serverPortPersistenceTest() {
        Settings.setServerPort("5723");
        assertEquals(Settings.getServerPort(), "5723");
    }


    @After
    public void restoreSettings() {
        Settings.setDataPath(backupDataPath);
        Settings.setServerAddress(backupServerAddress);
        Settings.setServerPort(backupServerPort);
    }

}
