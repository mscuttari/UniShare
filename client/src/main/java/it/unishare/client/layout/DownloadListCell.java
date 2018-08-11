package it.unishare.client.layout;

import it.unishare.client.controllers.DownloadListCellController;
import it.unishare.common.kademlia.KademliaFile;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DownloadListCell extends ListCell<Download> {

    private Node graphic;
    private DownloadListCellController controller;


    /**
     * Constructor
     */
    public DownloadListCell(ResourceBundle resources) {
        URL url = getClass().getResource("/views/download_list_cell.fxml");
        FXMLLoader loader = new FXMLLoader(url, resources);

        try {
            this.graphic = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.controller = loader.getController();
    }


    @Override
    protected void updateItem(Download item, boolean empty) {
        if (empty || item == null){
            setGraphic(null);

        } else {
            controller.setDownload(item);
            setGraphic(graphic);
        }
    }

}