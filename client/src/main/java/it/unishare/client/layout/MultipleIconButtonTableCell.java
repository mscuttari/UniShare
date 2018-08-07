package it.unishare.client.layout;

import it.unishare.common.utils.Triple;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class MultipleIconButtonTableCell<S, T> extends TableCell<S, T> {

    private HBox hbox;


    /**
     * Constructor
     *
     * @param   buttons     list of {@link Triple} containing icon name, action description and callback of each button
     */
    @SafeVarargs
    public MultipleIconButtonTableCell(Triple<String, String, Callback<S, Object>>... buttons) {
        this.hbox = new HBox();
        this.hbox.setSpacing(5);
        this.hbox.setAlignment(Pos.CENTER);

        for (Triple<String, String, Callback<S, Object>> button : buttons) {
            String iconName = button.first;
            String description = button.second;
            Callback<S, Object> callback = button.third;

            IconButton iconButton = new IconButton(iconName, description);
            iconButton.setOnAction(event -> callback.call((getTableView().getItems().get(getIndex()))));

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
