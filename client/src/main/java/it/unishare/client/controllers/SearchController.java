package it.unishare.client.controllers;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import it.unishare.client.connection.ConnectionManager;
import it.unishare.client.database.DatabaseManager;
import it.unishare.client.layout.GuiFile;
import it.unishare.client.layout.MultipleIconButtonTableCell;
import it.unishare.client.utils.FileUtils;
import it.unishare.client.utils.Settings;
import it.unishare.common.connection.kademlia.KademliaFile;
import it.unishare.common.connection.kademlia.KademliaFileData;
import it.unishare.common.connection.kademlia.KademliaNode;
import it.unishare.common.connection.kademlia.SearchListener;
import it.unishare.common.exceptions.NodeNotConnectedException;
import it.unishare.common.utils.HashingUtils;
import it.unishare.common.utils.LogUtils;
import it.unishare.common.utils.Triple;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.textfield.TextFields;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchController extends AbstractController implements Initializable {

    @FXML private HiddenSidesPane hiddenSidesPane;

    // Share new files
    @FXML private TextField txtTitle;
    @FXML private TextField txtUniversity;
    @FXML private TextField txtDepartment;
    @FXML private TextField txtCourse;
    @FXML private TextField txtTeacher;
    @FXML private TextField txtFilePath;

    @FXML private Label lblMessage;

    // Shared files
    @FXML private TableView<GuiFile> tableFiles;

    @FXML private TableColumn<GuiFile, Float> columnRating;
    @FXML private TableColumn<GuiFile, String> columnTitle;
    @FXML private TableColumn<GuiFile, String> columnUniversity;
    @FXML private TableColumn<GuiFile, String> columnDepartment;
    @FXML private TableColumn<GuiFile, String> columnCourse;
    @FXML private TableColumn<GuiFile, String> columnTeacher;
    @FXML private TableColumn<GuiFile, Void> columnActions;

    // PDF viewer
    @FXML private Pagination pdfViewer;
    @FXML private ScrollPane scroller;

    private ObjectProperty<PDFFile> currentFile = new SimpleObjectProperty<>();
    private ObjectProperty<ImageView> currentImage = new SimpleObjectProperty<>();

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
        columnUniversity.setCellValueFactory(param -> param.getValue().universityProperty());
        columnDepartment.setCellValueFactory(param -> param.getValue().departmentProperty());
        columnCourse.setCellValueFactory(param -> param.getValue().courseProperty());
        columnTeacher.setCellValueFactory(param -> param.getValue().teacherProperty());

        columnActions.setCellFactory(param -> new MultipleIconButtonTableCell<>());
    }


    /**
     * Search files
     */
    @FXML
    private void search() {
        String title = txtTitle.getText().trim();
        String university = txtUniversity.getText().trim();
        String department = txtDepartment.getText().trim();
        String course = txtCourse.getText().trim();
        String teacher = txtTeacher.getText().trim();

        KademliaFileData fileData = new KademliaFileData(title, university, department, course, teacher);
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
        txtCourse.setText(null);
        txtTeacher.setText(null);
        txtFilePath.setText(null);
        lblMessage.setText(null);
        tableFiles.setItems(null);
    }


    /**
     * Create observable GUI models list to be used for the table
     *
     * @param   files       files list
     * @return  GUI models list
     */
    private static ObservableList<GuiFile> getGuiFilesList(List<KademliaFile> files) {
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

}