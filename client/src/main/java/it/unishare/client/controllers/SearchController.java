package it.unishare.client.controllers;

import it.unishare.client.managers.ConnectionManager;
import it.unishare.client.managers.DownloadManager;
import it.unishare.client.layout.GuiFile;
import it.unishare.client.layout.MultipleIconButtonTableCell;
import it.unishare.client.utils.FileUtils;
import it.unishare.common.connection.kademlia.KademliaFile;
import it.unishare.common.connection.kademlia.KademliaFileData;
import it.unishare.common.connection.kademlia.KademliaNode;
import it.unishare.common.exceptions.NodeNotConnectedException;
import it.unishare.common.utils.Triple;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

public class SearchController extends AbstractController implements Initializable {

    @FXML private HiddenSidesPane hiddenSidesPane;

    // Share new files
    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtUniversity;
    @FXML private TextField txtDepartment;
    @FXML private TextField txtCourse;
    @FXML private TextField txtTeacher;

    @FXML private Label lblMessage;

    // Shared files
    @FXML private TableView<GuiFile> tableFiles;

    @FXML private TableColumn<GuiFile, Float> columnRating;
    @FXML private TableColumn<GuiFile, String> columnTitle;
    @FXML private TableColumn<GuiFile, String> columnAuthor;
    @FXML private TableColumn<GuiFile, String> columnUniversity;
    @FXML private TableColumn<GuiFile, String> columnDepartment;
    @FXML private TableColumn<GuiFile, String> columnCourse;
    @FXML private TableColumn<GuiFile, String> columnTeacher;
    @FXML private TableColumn<GuiFile, Void> columnActions;

    // Resources
    private ResourceBundle resources;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Resources
        this.resources = resources;

        // Share new file
        URL universitiesFile = getClass().getResource("/values/universities.txt");
        List<String> universities = FileUtils.readFileLines(universitiesFile);
        TextFields.bindAutoCompletion(txtUniversity, universities);

        // Search results
        // TODO: rating column
        columnTitle.setCellValueFactory(param -> param.getValue().titleProperty());
        columnAuthor.setCellValueFactory(param -> param.getValue().authorProperty());
        columnUniversity.setCellValueFactory(param -> param.getValue().universityProperty());
        columnDepartment.setCellValueFactory(param -> param.getValue().departmentProperty());
        columnCourse.setCellValueFactory(param -> param.getValue().courseProperty());
        columnTeacher.setCellValueFactory(param -> param.getValue().teacherProperty());

        columnActions.setCellFactory(param -> new MultipleIconButtonTableCell<>(
                new Triple<>("DOWNLOAD", resources.getString("download"), param1 -> {
                    download(param1.getFile());
                    return null;
                })
        ));
    }


    /**
     * Search files
     */
    @FXML
    private void search() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String university = txtUniversity.getText().trim();
        String department = txtDepartment.getText().trim();
        String course = txtCourse.getText().trim();
        String teacher = txtTeacher.getText().trim();

        KademliaFileData fileData = new KademliaFileData(title, author, university, department, course, teacher);
        KademliaNode node = ConnectionManager.getInstance().getNode();

        try {
            node.searchData(fileData, files -> tableFiles.setItems(getGuiFilesList(files)));
        } catch (NodeNotConnectedException e) {
            showErrorMessage(resources.getString("client_not_connected"));
        }
    }


    /**
     * Reset fields and search results
     */
    @FXML
    private void reset() {
        txtTitle.setText(null);
        txtUniversity.setText(null);
        txtDepartment.setText(null);
        txtCourse.setText(null);
        txtTeacher.setText(null);
        lblMessage.setText(null);
        tableFiles.setItems(null);
    }


    /**
     * Create observable GUI models list to be used for the table
     *
     * @param   files       files list
     * @return  GUI models list
     */
    private static ObservableList<GuiFile> getGuiFilesList(Collection<KademliaFile> files) {
        ObservableList<GuiFile> result = FXCollections.observableArrayList();
        files.forEach(file -> result.add(new GuiFile(file)));
        return result;
    }


    /**
     * Show failure message for the file sharing process
     *
     * @param   message     message
     */
    private void showErrorMessage(String message) {
        lblMessage.getStyleClass().remove("message-success");
        lblMessage.getStyleClass().add("message-failure");
        lblMessage.setText(message);
    }


    /**
     * Download file
     *
     * @param   file    file to be downloaded
     */
    private void download(KademliaFile file) {
        File downloadPath = chooseDownloadPath();

        if (downloadPath != null) {
            DownloadManager.getInstance().download(file, downloadPath);
        }
    }


    /**
     * Choose the download path
     *
     * @return  download path
     */
    private File chooseDownloadPath() {
        FileChooser fileChooser = new FileChooser();

        // PDF files only
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDF", "*.pdf");
        fileChooser.getExtensionFilters().add(extensionFilter);

        return fileChooser.showSaveDialog(hiddenSidesPane.getScene().getWindow());
    }

}