package it.unishare.client.layout;

import it.unishare.common.utils.Quaternary;
import it.unishare.common.utils.Triple;
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class MultipleIconButtonTableCell<S, T> extends TableCell<S, T> {

    private HBox hbox;


    /**
     * Constructor
     *
     * @param   buttons     list of {@link Triple} containing icon name, action description, visibility and callback of each button
     */
    @SafeVarargs
    public MultipleIconButtonTableCell(Quaternary<String, String, ObservableBooleanValue, Callback<S, Object>>... buttons) {
        this.hbox = new HBox();
        this.hbox.setSpacing(5);
        this.hbox.setAlignment(Pos.CENTER);

        for (Quaternary<String, String, ObservableBooleanValue, Callback<S, Object>> button : buttons) {
            String iconName = button.first;
            String description = button.second;
            ObservableBooleanValue visibility = button.third;
            Callback<S, Object> callback = button.fourth;

            IconButton iconButton = new IconButton(iconName, description);
            iconButton.setOnAction(event -> callback.call((getTableView().getItems().get(getIndex()))));

            if (visibility != null)
                iconButton.visibleProperty().bind(visibility);

            hbox.getChildren().add(iconButton);
        }
    }


    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(hbox);
        }

        setText(null);
    }

}
