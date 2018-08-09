package it.unishare.client.utils;

import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class GUIUtils {

    private GUIUtils() {

    }

    /**
     * Resize the able columns to fit the content width
     *
     * @param   table       table
     */
    public static void autoResizeColumns(TableView<?> table) {
        // Set the right resize policy
        table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);

        table.getColumns().forEach( (column) -> {
            // Minimum  width = column header
            Text t = new Text( column.getText() );
            double max = t.getLayoutBounds().getWidth();

            for (int i = 0; i < table.getItems().size(); i++) {
                // Cell must not be empty
                if (column.getCellData( i ) != null ) {
                    t = new Text( column.getCellData( i ).toString() );
                    double calcWidth = t.getLayoutBounds().getWidth();

                    // Remember new max-width
                    if (calcWidth > max) {
                        max = calcWidth;
                    }
                }
            }

            // Set the new max-widht with some extra space
            column.setPrefWidth( max + 20.0d );
        } );
    }

}
