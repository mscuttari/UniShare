package it.unishare.client.controllers;

import it.unishare.client.layout.Download;
import it.unishare.client.layout.Download.DownloadStatus;
import it.unishare.client.managers.DownloadManager;
import it.unishare.common.connection.kademlia.KademliaFile;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DownloadListCellController extends AbstractController {

    @FXML private HBox actions;
    @FXML private ProgressBar progressBar;

    @FXML private Label lblTitle;
    @FXML private Label lblDescription;

    private Download download;
    private ResourceBundle resources;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        actions.managedProperty().bind(actions.visibleProperty());
        progressBar.managedProperty().bind(progressBar.visibleProperty());
    }


    /**
     * Set the download associated to the cell
     *
     * @param   download    download
     */
    public void setDownload(Download download) {
        this.download = download;

        KademliaFile file = download.getFile();
        lblTitle.setText(file.getData().getTitle());
        lblDescription.setText("[" +
                file.getData().getUniversity() + ", " +
                file.getData().getDepartment() + ", " +
                file.getData().getCourse() + ", " +
                file.getData().getTeacher() +
                "] - [" +
                resources.getString("author") +
                ": " + file.getData().getAuthor() +
                "]"
        );

        updateView(download.getStatus());
        download.addStatusChangeListener((newValue, oldValue) -> updateView(newValue));
    }


    /**
     * Open file
     */
    @FXML
    private void open() {
        try {
            Desktop.getDesktop().open(download.getPath());
        } catch (IOException e) {
            // TODO: error message
        }
    }


    /**
     * Open file folder
     */
    @FXML
    private void openFolder() {
        try {
            Desktop.getDesktop().open(download.getPath().getParentFile());
        } catch (IOException e) {
            // TODO: error message
        }
    }


    /**
     * Remove the file from the list
     */
    @FXML
    private void remove() {
        // TODO: remove from list
    }


    /**
     * Update view according to download status
     *
     * @param   downloadStatus      download status
     */
    private void updateView(DownloadStatus downloadStatus) {
        if (downloadStatus == DownloadStatus.IN_PROGRESS) {
            actions.setVisible(false);
            progressBar.setVisible(true);

        } else {
            progressBar.setVisible(false);
            actions.setVisible(true);
        }
    }

}
