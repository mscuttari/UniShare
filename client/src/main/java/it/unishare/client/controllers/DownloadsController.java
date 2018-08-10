package it.unishare.client.controllers;

import it.unishare.client.layout.Download;
import it.unishare.client.layout.DownloadListCell;
import it.unishare.client.managers.DownloadManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class DownloadsController extends AbstractController {

    @FXML private ListView<Download> listDownloads;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listDownloads.setCellFactory(param -> new DownloadListCell(resources));
        listDownloads.setItems(DownloadManager.getInstance().getDownloads());
    }

}
