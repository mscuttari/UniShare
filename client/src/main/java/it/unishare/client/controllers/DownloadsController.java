package it.unishare.client.controllers;

import it.unishare.client.layout.Download;
import it.unishare.client.layout.DownloadListCell;
import it.unishare.client.managers.DownloadsManager;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DownloadsController extends AbstractController {

    @FXML private Label lblEmptyList;
    @FXML private ListView<Download> listDownloads;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblEmptyList.managedProperty().bind(lblEmptyList.visibleProperty());

        listDownloads.setCellFactory(param -> new DownloadListCell(resources));

        ObservableList<Download> downloads = DownloadsManager.getInstance().getDownloads();
        downloads.addListener((ListChangeListener<Download>) c -> updateView(downloads));

        listDownloads.setItems(downloads);
        updateView(downloads);
    }


    /**
     * Update view according to the downloads list size
     *
     * @param   downloads   downloads list
     */
    private void updateView(List<Download> downloads) {
        if (downloads.size() == 0) {
            listDownloads.setVisible(false);
            lblEmptyList.setVisible(true);
        } else {
            lblEmptyList.setVisible(false);
            listDownloads.setVisible(true);
        }
    }

}
