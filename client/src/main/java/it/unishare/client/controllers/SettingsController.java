package it.unishare.client.controllers;

import it.unishare.client.utils.Settings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends AbstractController implements Initializable {

    @FXML private TextField txtDataPath;
    @FXML private Button btnDirectorySelection;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtDataPath.setText(Settings.getDataPath());
        btnDirectorySelection.setOnAction(event -> selectDataDirectory());
    }


    /**
     * Select data directory
     */
    private void selectDataDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        if (selectedDirectory != null)
            Settings.setDataPath(selectedDirectory.getAbsolutePath());

        txtDataPath.setText(Settings.getDataPath());
    }

}