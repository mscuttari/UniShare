package it.unishare.client.controllers;

import it.unishare.client.layout.ConfirmationDialogListener;
import it.unishare.common.utils.LogUtils;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

public abstract class AbstractController implements Initializable {

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
     * @param   title       title
     * @param   message     message
     */
    protected static void showConfirmationDialog(String title, String message, ConfirmationDialogListener listener) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
            alert.setTitle(title);
            alert.setHeaderText(null);

            Optional<ButtonType> result = alert.showAndWait();

            if (listener != null)
                listener.onResult(result.isPresent() && result.get() == ButtonType.YES);
        });
    }


    /**
     * Show information dialog
     *
     * @param   title       title
     * @param   message     message
     */
    protected static void showInformationDialog(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }


    /**
     * Show error dialog
     *
     * @param   title       title
     * @param   message     message
     */
    protected static void showErrorDialog(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }


    /**
     * Set top view of a {@link BorderPane}
     *
     * @param   borderPane      {@link BorderPane} to be populated
     * @param   fileName        FXML file name to be loaded
     */
    protected void borderPaneSetTop(BorderPane borderPane, String fileName) {
        borderPaneSetTop(borderPane, fileName, null);
    }


    /**
     * Set top view of a {@link BorderPane}
     *
     * @param   borderPane      {@link BorderPane} to be populated
     * @param   fileName        FXML file name to be loaded
     * @param   resources       {@link ResourceBundle} containing the resources
     */
    protected void borderPaneSetTop(BorderPane borderPane, String fileName, ResourceBundle resources) {
        try {
            URL url = this.getClass().getResource(getViewPath(fileName));
            borderPane.setTop(FXMLLoader.load(url, resources));
        } catch (IOException e) {
            LogUtils.e(this.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Set center view of a {@link BorderPane}
     *
     * @param   borderPane      {@link BorderPane} to be populated
     * @param   fileName        FXML file name to be loaded
     */
    protected void borderPaneSetCenter(BorderPane borderPane, String fileName) {
        borderPaneSetCenter(borderPane, fileName, null);
    }


    /**
     * Set center view of a {@link BorderPane}
     *
     * @param   borderPane      {@link BorderPane} to be populated
     * @param   fileName        FXML file name to be loaded
     * @param   resources       {@link ResourceBundle} containing the resources
     */
    protected void borderPaneSetCenter(BorderPane borderPane, String fileName, ResourceBundle resources) {
        try {
            URL url = this.getClass().getResource(getViewPath(fileName));
            borderPane.setCenter(FXMLLoader.load(url, resources));
        } catch (IOException e) {
            LogUtils.e(this.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Set bottom view of a {@link BorderPane}
     *
     * @param   borderPane      {@link BorderPane} to be populated
     * @param   fileName        FXML file name to be loaded
     */
    protected void borderPaneSetBottom(BorderPane borderPane, String fileName) {
        borderPaneSetBottom(borderPane, fileName, null);
    }


    /**
     * Set bottom view of a {@link BorderPane}
     *
     * @param   borderPane      {@link BorderPane} to be populated
     * @param   fileName        FXML file name to be loaded
     * @param   resources       {@link ResourceBundle} containing the resources
     */
    protected void borderPaneSetBottom(BorderPane borderPane, String fileName, ResourceBundle resources) {
        try {
            URL url = this.getClass().getResource(getViewPath(fileName));
            borderPane.setBottom(FXMLLoader.load(url, resources));
        } catch (IOException e) {
            LogUtils.e(this.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Set left view of a {@link BorderPane}
     *
     * @param   borderPane      {@link BorderPane} to be populated
     * @param   fileName        FXML file name to be loaded
     */
    protected void borderPaneSetLeft(BorderPane borderPane, String fileName) {
        borderPaneSetLeft(borderPane, fileName, null);
    }


    /**
     * Set left view of a {@link BorderPane}
     *
     * @param   borderPane      {@link BorderPane} to be populated
     * @param   fileName        FXML file name to be loaded
     * @param   resources       {@link ResourceBundle} containing the resources
     */
    protected void borderPaneSetLeft(BorderPane borderPane, String fileName, ResourceBundle resources) {
        try {
            URL url = this.getClass().getResource(getViewPath(fileName));
            borderPane.setLeft(FXMLLoader.load(url, resources));
        } catch (IOException e) {
            LogUtils.e(this.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Set right view of a {@link BorderPane}
     *
     * @param   borderPane      {@link BorderPane} to be populated
     * @param   fileName        FXML file name to be loaded
     */
    protected void borderPaneSetRight(BorderPane borderPane, String fileName) {
        borderPaneSetRight(borderPane, fileName, null);
    }


    /**
     * Set right view of a {@link BorderPane}
     *
     * @param   borderPane      {@link BorderPane} to be populated
     * @param   fileName        FXML file name to be loaded
     * @param   resources       {@link ResourceBundle} containing the resources
     */
    protected void borderPaneSetRight(BorderPane borderPane, String fileName, ResourceBundle resources) {
        try {
            URL url = this.getClass().getResource(getViewPath(fileName));
            borderPane.setRight(FXMLLoader.load(url, resources));
        } catch (IOException e) {
            LogUtils.e(this.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Create {@link PopOver} containing the specified FXML view
     *
     * @param   fileName    FXML file name to be loaded
     * @return  {@link PopOver} instance
     */
    protected PopOver createPopOver(String fileName) {
        return createPopOver(fileName, null);
    }


    /**
     * Create {@link PopOver} containing the specified FXML view
     *
     * @param   fileName    FXML file name to be loaded
     * @param   resources   {@link ResourceBundle} containing the resources
     *
     * @return  {@link PopOver} instance
     */
    protected PopOver createPopOver(String fileName, ResourceBundle resources) {
        PopOver popOver = new PopOver();

        try {
            URL url = this.getClass().getResource(getViewPath(fileName));
            Pane pane = FXMLLoader.load(url, resources);
            popOver.setContentNode(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return popOver;
    }


    /**
     * Get FXML view path
     *
     * @param   fileName    file name
     * @return  file path
     */
    private static String getViewPath(String fileName) {
        return "/views/" + fileName + ".fxml";
    }


    /**
     * Set node tooltip
     *
     * @param   node                node
     * @param   resourceBundle      resource bundle of the strings
     * @param   stringKey           key of the string in the resource bundle
     */
    protected void setTooltip(Node node, ResourceBundle resourceBundle, String stringKey) {
        String value = resourceBundle.getString(stringKey);
        Tooltip tooltip = new Tooltip(value);
        Tooltip.install(node, tooltip);
    }

}