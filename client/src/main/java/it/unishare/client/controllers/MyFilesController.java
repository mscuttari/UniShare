package it.unishare.client.controllers;

import it.unishare.client.layout.GuiFile;
import it.unishare.client.utils.Settings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MyFilesController extends AbstractController implements Initializable {

    @FXML private TextField txtTitle;
    @FXML private TextField txtUniversity;
    @FXML private TextField txtCourse;
    @FXML private TextField txtTeacher;
    @FXML private TextField txtFilePath;
    @FXML private Button btnFileSelection;

    @FXML private TableView<GuiFile> tableFiles;

    @FXML private TableColumn<GuiFile, String> columnTitle;
    @FXML private TableColumn<GuiFile, String> columnUniversity;
    @FXML private TableColumn<GuiFile, String> columnCourse;
    @FXML private TableColumn<GuiFile, String> columnTeacher;
    @FXML private TableColumn<GuiFile, Void> columnActions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnFileSelection.setOnAction(event -> selectFile());
    }


    /**
     * Select file
     */
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();

        // PDF files only
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDF", "*.pdf");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        txtFilePath.setText(selectedFile.getAbsolutePath());
    }


    /**
     * Share file
     */
    @FXML
    private void share() {
        
    }


    /**
     * Reset all fields
     */
    @FXML
    private void reset() {
        txtTitle.setText(null);
        txtUniversity.setText(null);
        txtCourse.setText(null);
        txtTeacher.setText(null);
        txtFilePath.setText(null);
    }

}
