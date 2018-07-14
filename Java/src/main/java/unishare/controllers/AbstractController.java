package unishare.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public abstract class AbstractController {

    /**
     * Load properties from a file
     *
     * @param   fileName        properties file name
     * @return  properties (empty in case of I/O error)
     */
    protected Properties loadProperties(String fileName) {
        Properties prop = new Properties();

        try {
            InputStream is = getClass().getResourceAsStream("/values/" + fileName);
            prop.load(is);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return prop;
    }


    /**
     * Show confirmation dialog
     *
     * @param   message     message
     * @return  true for "Yes" answer; false for "No" answer
     */
    protected static boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Attenzione");
        alert.setHeaderText(null);

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.YES;
    }


    /**
     * Show information dialog
     *
     * @param   message     message
     */
    protected static void showInformationDialog(String message) {
        showInformationDialog("Informazioni", message);
    }


    /**
     * Show information dialog
     *
     * @param   title       title
     * @param   message     message
     */
    protected static void showInformationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }


    /**
     * Show error dialog
     *
     * @param   message     message
     */
    protected static void showErrorDialog(String message) {
        showErrorDialog("Errore", message);
    }


    /**
     * Show error dialog
     *
     * @param   title       title
     * @param   message     message
     */
    protected static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

}