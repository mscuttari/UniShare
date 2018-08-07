package it.unishare.client.controllers;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import it.unishare.client.connection.ConnectionManager;
import it.unishare.client.layout.GuiFile;
import it.unishare.client.layout.MultipleIconButtonTableCell;
import it.unishare.common.connection.kademlia.KademliaFile;
import it.unishare.common.connection.kademlia.KademliaFileData;
import it.unishare.common.connection.kademlia.KademliaNode;
import it.unishare.common.utils.HashingUtils;
import it.unishare.common.utils.Triple;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.controlsfx.control.HiddenSidesPane;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyFilesController extends AbstractController implements Initializable {

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
        columnTitle.setCellValueFactory(param -> param.getValue().titleProperty());
        columnUniversity.setCellValueFactory(param -> param.getValue().universityProperty());
        columnDepartment.setCellValueFactory(param -> param.getValue().departmentProperty());
        columnCourse.setCellValueFactory(param -> param.getValue().courseProperty());
        columnTeacher.setCellValueFactory(param -> param.getValue().teacherProperty());

        columnActions.setCellFactory(param -> new MultipleIconButtonTableCell<>(
                new Triple<>("EYE", resources.getString("preview"), param1 -> {
                    //preview(param1.getFile());
                    return null;
                }),
                new Triple<>("FILE", resources.getString("open"), param1 -> {
                    open(param1.getFile());
                    return null;
                }),
                new Triple<>("TRASH", resources.getString("delete"), param1 -> {
                    delete(param1.getFile());
                    return null;
                })
        ));

        // PDF viewer
        scroller.contentProperty().bind(currentImage);
        bindPaginationToCurrentFile();
        createPaginationPageFactory();
    }


    /**
     * Select file
     */
    @FXML
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();

        // PDF files only
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDF", "*.pdf");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File selectedFile = fileChooser.showOpenDialog(txtFilePath.getScene().getWindow());
        txtFilePath.setText(selectedFile.getAbsolutePath());
    }


    /**
     * Share file
     */
    @FXML
    private void share() {
        String title = txtTitle.getText().trim();
        String university = txtUniversity.getText().trim();
        String department = txtDepartment.getText().trim();
        String course = txtCourse.getText().trim();
        String teacher = txtTeacher.getText().trim();
        String filePath = txtFilePath.getText().trim();

        if (title.isEmpty() ||university.isEmpty() || department.isEmpty() || course.isEmpty() || teacher.isEmpty() || filePath.isEmpty()) {
            lblMessage.getStyleClass().remove("message-success");
            lblMessage.getStyleClass().add("message-failure");

            List<String> fields = new ArrayList<>();
            if (title.isEmpty()) fields.add(resources.getString("title").toLowerCase());
            if (university.isEmpty()) fields.add(resources.getString("university").toLowerCase());
            if (department.isEmpty()) fields.add(resources.getString("department").toLowerCase());
            if (course.isEmpty()) fields.add(resources.getString("course").toLowerCase());
            if (teacher.isEmpty()) fields.add(resources.getString("teacher").toLowerCase());
            if (filePath.isEmpty()) fields.add(resources.getString("file").toLowerCase());

            lblMessage.setText(resources.getString("missing_fields") + ": " + String.join(", ", fields));
            return;
        }

        lblMessage.getStyleClass().remove("message-failure");
        lblMessage.getStyleClass().add("message-success");
        lblMessage.setText(resources.getString("file_added"));

        KademliaNode node = ConnectionManager.getInstance().getNode();
        KademliaFileData data = new KademliaFileData(title, university, department, course, teacher);

        KademliaFile file = new KademliaFile(
                HashingUtils.fileSHA1(filePath),
                node.getInfo(),
                data
        );

        node.storeData(file, filePath);
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
        lblMessage.setText(null);
    }


    /**
     * Preview file
     *
     * @param   filePath    file path
     */
    private void preview(String filePath) {
        // Load PDF
        Task<PDFFile> loadFileTask = new Task<PDFFile>() {
            @Override
            protected PDFFile call() throws Exception {
                try (
                        RandomAccessFile raf = new RandomAccessFile(filePath, "r");
                        FileChannel channel = raf.getChannel()
                ) {
                    ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                    return new PDFFile(buffer);
                }
            }
        };

        // Show PDF
        loadFileTask.setOnSucceeded(event -> {
            final PDFFile pdfFile = loadFileTask.getValue();
            currentFile.set(pdfFile);
            hiddenSidesPane.setPinnedSide(Side.RIGHT);
        });

        // Error opening the PDF
        loadFileTask.setOnFailed(event -> {
            // TODO: error message
        });

        getThreadExecutor().submit(loadFileTask);
    }


    /**
     * Close the preview page
     */
    @FXML
    private void closePreview() {
        hiddenSidesPane.setPinnedSide(null);
    }


    /**
     * Bind pagination to current file
     */
    private void bindPaginationToCurrentFile() {
        currentFile.addListener((observable, oldFile, newFile) -> {
            if (newFile != null) {
                pdfViewer.setCurrentPageIndex(0);
            }
        });

        pdfViewer.pageCountProperty().bind(new IntegerBinding() {
            {
                super.bind(currentFile);
            }

            @Override
            protected int computeValue() {
                return currentFile.get()==null ? 0 : currentFile.get().getNumPages();
            }
        });

        pdfViewer.disableProperty().bind(Bindings.isNull(currentFile));
    }


    /**
     * Create page factory for the PDF viewer
     */
    private void createPaginationPageFactory() {
        pdfViewer.setPageFactory(pageNumber -> {
            if (currentFile.get() == null) {
                return null ;
            } else {
                if (pageNumber >= currentFile.get().getNumPages() || pageNumber < 0) {
                    return null ;
                } else {
                    updateImage(currentFile.get(), pageNumber);
                    return scroller;
                }
            }
        });
    }


    /**
     * Get image of a PDF page
     *
     * @param   file            PDF file
     * @param   pageNumber      page number
     */
    private void updateImage(PDFFile file, int pageNumber) {
        final Task<ImageView> updateImageTask = new Task<ImageView>() {
            @Override
            protected ImageView call() {
                PDFPage page = file.getPage(pageNumber + 1);
                Rectangle2D bbox = page.getBBox();

                // Get dimensions
                int width  = (int) bbox.getWidth();
                int height = (int) bbox.getHeight();

                // Get image
                java.awt.Image awtImage = page.getImage(width, height, bbox, null, true, true);

                // Draw image to buffered image
                BufferedImage buffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                buffImage.createGraphics().drawImage(awtImage, 0, 0, null);

                // Convert to JavaFX image
                Image image = SwingFXUtils.toFXImage(buffImage, null);

                // Wrap in image view
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);

                return imageView ;
            }
        };

        updateImageTask.setOnSucceeded(event -> {
            currentImage.set(updateImageTask.getValue());
        });

        updateImageTask.setOnFailed(event -> {
            updateImageTask.getException().printStackTrace();
        });

        getThreadExecutor().submit(updateImageTask);
    }


    /**
     * Open file
     *
     * @param   file    file
     */
    private void open(KademliaFile file) {
        // TODO: open
    }


    /**
     * Delete file
     *
     * @param   file    file
     */
    private void delete(KademliaFile file) {
        // TODO: delete
    }


    /**
     * Get {@link ExecutorService} to be used to run a {@link Task}
     *
     * @return  ExecutorService
     */
    private static ExecutorService getThreadExecutor() {
        return Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
    }

}
