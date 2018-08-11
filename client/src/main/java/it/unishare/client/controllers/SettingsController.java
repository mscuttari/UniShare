package it.unishare.client.controllers;

import it.unishare.client.utils.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends AbstractController {

    @FXML private TextField txtDataPath;
    @FXML private TextField txtServerAddress;
    @FXML private TextField txtServerPort;

    private ResourceBundle resources;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        // Numeric only TextField
        txtServerPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtServerPort.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        loadSettings();
    }


    /**
     * Select data directory
     */
    @FXML
    private void selectDataDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(txtDataPath.getScene().getWindow());

        if (selectedDirectory != null)
            txtDataPath.setText(selectedDirectory.getAbsolutePath());
    }


    /**
     * Save settings
     */
    @FXML
    private void save() {
        // Data path
        String dataPath = txtDataPath.getText().trim();

        if (dataPath.isEmpty()) {
            showErrorDialog(resources.getString("settings"), resources.getString("missing_directory"));
            return;
        } else if (!isDataPathValid(dataPath)) {
            showErrorDialog(resources.getString("settings"), resources.getString("invalid_directory"));
            return;
        }


        // Server address
        String serverAddress = txtServerAddress.getText().trim();

        if (serverAddress.isEmpty()) {
            showErrorDialog(resources.getString("settings"), resources.getString("missing_server_address"));
            return;
        } else if (!isServerAddressValid(serverAddress)) {
            showErrorDialog(resources.getString("settings"), resources.getString("invalid_server_address"));
            return;
        }


        // Server port
        String serverPort = txtServerPort.getText().trim();

        if (serverPort.isEmpty()) {
            showErrorDialog(resources.getString("settings"), resources.getString("missing_server_port"));
            return;
        } else if (!isServerPortValid(serverPort)) {
            showErrorDialog(resources.getString("settings"), resources.getString("invalid_server_port"));
            return;
        }


        // Save settings
        Settings.setDataPath(dataPath);
        Settings.setServerAddress(serverAddress);
        Settings.setServerPort(serverPort);

        showInformationDialog(resources.getString("settings"), resources.getString("settings_saved"));
    }


    /**
     * Reset fields
     */
    @FXML
    private void reset() {
        loadSettings();
    }


    /**
     * Load current settings
     */
    private void loadSettings() {
        txtDataPath.setText(Settings.getDataPath());
        txtServerAddress.setText(Settings.getServerAddress());
        txtServerPort.setText(Settings.getServerPort());
    }


    /**
     * Check if the data path is valid
     *
     * @param   dataPath        data path
     * @return  true if the file denoted by the path exists and it is a directory; false otherwise
     */
    private static boolean isDataPathValid(String dataPath) {
        return new File(dataPath).isDirectory();
    }


    /**
     * Check if the server address is valid
     *
     * @param   ip      server IP address
     * @return  true if the string is a valid (not necessarily existing) IPv4 address; false otherwise
     */
    private static boolean isServerAddressValid(String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4)
                return false;

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255))
                    return false;
            }

            if (ip.endsWith("."))
                return false;

            return true;

        } catch (NumberFormatException nfe) {
            return false;
        }
    }


    /**
     * Check if the server port is valid
     *
     * @param   serverPort      server port
     * @return  true if the port is between 0 and 65535 (both included); false otherwise
     */
    private static boolean isServerPortValid(String serverPort) {
        try {
            Integer port = Integer.valueOf(serverPort);
            return port >= 0 && port <= 65535;

        } catch (NumberFormatException e) {
            return false;
        }
    }

}
