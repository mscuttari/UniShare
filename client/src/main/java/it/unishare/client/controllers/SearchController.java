package it.unishare.client.controllers;

import it.unishare.client.layout.Download;
import it.unishare.client.layout.ReviewListCell;
import it.unishare.client.managers.ConnectionManager;
import it.unishare.client.managers.DatabaseManager;
import it.unishare.client.managers.DownloadManager;
import it.unishare.client.layout.GuiFile;
import it.unishare.client.layout.MultipleIconButtonTableCell;
import it.unishare.client.utils.FileUtils;
import it.unishare.client.utils.GUIUtils;
import it.unishare.common.connection.kademlia.KademliaFile;
import it.unishare.common.connection.kademlia.KademliaFileData;
import it.unishare.common.connection.kademlia.KademliaNode;
import it.unishare.common.exceptions.NodeNotConnectedException;
import it.unishare.common.models.Review;
import it.unishare.common.models.User;
import it.unishare.common.utils.Quaternary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.Rating;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

public class SearchController extends AbstractController implements Initializable {

    @FXML private HiddenSidesPane hiddenSidesPane;

    // Share new file
    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtUniversity;
    @FXML private TextField txtDepartment;
    @FXML private TextField txtCourse;
    @FXML private TextField txtTeacher;

    @FXML private Label lblMessage;

    // Shared files
    @FXML private TableView<GuiFile> tableFiles;

    @FXML private TableColumn<GuiFile, String> columnTitle;
    @FXML private TableColumn<GuiFile, String> columnAuthor;
    @FXML private TableColumn<GuiFile, String> columnUniversity;
    @FXML private TableColumn<GuiFile, String> columnDepartment;
    @FXML private TableColumn<GuiFile, String> columnCourse;
    @FXML private TableColumn<GuiFile, String> columnTeacher;
    @FXML private TableColumn<GuiFile, Void> columnActions;

    // Reviews
    private KademliaFile reviewsCurrentFile;
    private int currentReviewsPage;
    @FXML private VBox boxMyReview;
    @FXML private Rating ratingReview;
    @FXML private TextArea txtReviewBody;
    @FXML private ListView<Review> lvReviews;

    // Resources
    private ResourceBundle resources;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Resources
        this.resources = resources;

        // Share new file
        List<String> universities = FileUtils.readFileLines(getClass().getResourceAsStream("/values/universities.txt"));
        TextFields.bindAutoCompletion(txtUniversity, universities);

        // Search results
        columnTitle.setCellValueFactory(param -> param.getValue().titleProperty());
        columnAuthor.setCellValueFactory(param -> param.getValue().authorProperty());
        columnUniversity.setCellValueFactory(param -> param.getValue().universityProperty());
        columnDepartment.setCellValueFactory(param -> param.getValue().departmentProperty());
        columnCourse.setCellValueFactory(param -> param.getValue().courseProperty());
        columnTeacher.setCellValueFactory(param -> param.getValue().teacherProperty());

        columnActions.setCellFactory(param -> new MultipleIconButtonTableCell<>(
                new Quaternary<>(
                        "DOWNLOAD",
                        resources.getString("download"),
                        ConnectionManager.getInstance().loggedProperty(),
                        param1 -> {
                            download(param1.getFile());
                            return null;
                        }),
                new Quaternary<>(
                        "STAR",
                        resources.getString("reviews"),
                        ConnectionManager.getInstance().loggedProperty(),
                        param1 -> {
                            showFileReviews(param1.getFile());
                            return null;
                        }
                )
        ));

        // Reviews
        boxMyReview.managedProperty().bind(boxMyReview.visibleProperty());

        lvReviews.setCellFactory(param -> new ReviewListCell(resources));
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
            node.searchData(fileData, files -> {
                tableFiles.setItems(getGuiFilesList(files));
                GUIUtils.autoResizeColumns(tableFiles);
            });

        } catch (NodeNotConnectedException e) {
            showErrorMessage(resources.getString("client_not_connected"));
        }
    }


    /**
     * Reset fields and search results
     */
    @FXML
    private void reset() {
        txtTitle.clear();
        txtTitle.clear();
        txtUniversity.clear();
        txtDepartment.clear();
        txtCourse.clear();
        txtTeacher.clear();
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


    /**
     * Show file reviews
     *
     * @param   file    file
     */
    private void showFileReviews(KademliaFile file) {
        currentReviewsPage = 1;
        boxMyReview.setVisible(isFileDownloaded(file));

        hiddenSidesPane.setPinnedSide(Side.RIGHT);
        loadFileReviews(file);
    }


    /**
     * Load file reviews
     *
     * @param   file    file
     */
    private void loadFileReviews(KademliaFile file) {
        reviewsCurrentFile = file;
        KademliaNode node = ConnectionManager.getInstance().getNode();

        node.getFileReviews(file, currentReviewsPage, (page, reviews) -> {
            User user = ConnectionManager.getInstance().getUser();

            for (Review review : reviews) {
                if (review.getAuthor().equals(user.getFullName())) {
                    ratingReview.setRating(review.getRating());
                    txtReviewBody.setText(review.getBody());
                    break;
                }
            }

            lvReviews.setItems(FXCollections.observableList(reviews));
        });
    }


    /**
     * Check whether the file has been previously downloaded
     *
     * @param   file    file
     * @return  true if the file has been downloaded; false otherwise
     */
    private boolean isFileDownloaded(KademliaFile file) {
        User user = ConnectionManager.getInstance().getUser();
        List<Download> downloads = DatabaseManager.getInstance().getDownloadedFiles(user);

        for (Download download : downloads) {
            if (download.getFile().equals(file))
                return true;
        }

        return false;
    }


    /**
     * Close the reviews page
     */
    @FXML
    private void closeReviews() {
        hiddenSidesPane.setPinnedSide(null);
    }


    /**
     * Save my review
     */
    @FXML
    private void saveReview() {
        User user = ConnectionManager.getInstance().getUser();

        int rating = (int) ratingReview.getRating();
        String author = user.getFullName();
        String body = txtReviewBody.getText().trim();
        body = body.isEmpty() ? null : body;

        Review review = new Review(author, rating, body);

        KademliaNode node = ConnectionManager.getInstance().getNode();

        if (reviewsCurrentFile != null)
            node.sendReview(reviewsCurrentFile, review);
    }


    /**
     * Delete my review
     */
    @FXML
    private void deleteReview() {
        User user = ConnectionManager.getInstance().getUser();

        int rating = (int) ratingReview.getRating();
        String author = user.getFullName();
        Review review = new Review(author, rating, null);

        KademliaNode node = ConnectionManager.getInstance().getNode();

        if (reviewsCurrentFile != null)
            node.sendReview(reviewsCurrentFile, review);

        txtReviewBody.clear();
    }


    /**
     * Load more reviews
     */
    @FXML
    private void loadMoreReviews() {
        currentReviewsPage++;
        loadFileReviews(reviewsCurrentFile);
    }

}