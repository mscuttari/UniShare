package it.unishare.client.layout;

import it.unishare.client.controllers.ReviewListCellController;
import it.unishare.common.models.Review;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReviewListCell extends ListCell<Review> {

    private Node graphic;
    private ReviewListCellController controller;


    /**
     * Constructor
     */
    public ReviewListCell(ResourceBundle resources) {
        URL url = getClass().getResource("/views/review_list_cell.fxml");
        FXMLLoader loader = new FXMLLoader(url, resources);

        try {
            this.graphic = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.controller = loader.getController();
    }


    @Override
    protected void updateItem(Review item, boolean empty) {
        if (empty || item == null){
            setGraphic(null);

        } else {
            controller.setReview(item);
            setGraphic(graphic);
        }
    }

}