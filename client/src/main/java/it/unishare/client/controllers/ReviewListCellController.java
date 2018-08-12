package it.unishare.client.controllers;

import it.unishare.common.models.Review;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.controlsfx.control.Rating;

import java.net.URL;
import java.util.ResourceBundle;

public class ReviewListCellController extends AbstractController {

    @FXML private Rating rating;
    @FXML private Label lblAuthor;
    @FXML private Label lblBody;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void setWidth(ListView listView) {
        lblBody.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> listView.getWidth() - listView.getPadding().getLeft() - listView.getPadding().getRight() - 20,
                listView.widthProperty(), listView.paddingProperty())
        );
    }


    /**
     * Set the review associated to the cell
     *
     * @param   review      review
     */
    public void setReview(Review review) {
        rating.setRating(review.getRating());
        lblAuthor.setText(review.getAuthor());
        lblBody.setText(review.getBody());
    }

}
