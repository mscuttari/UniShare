package it.unishare.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

public class Client extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage primaryStage) {
        try {
            // Load the view
            ResourceBundle resources = ResourceBundle.getBundle("values.strings");
            Parent root = FXMLLoader.load(getClass().getResource("/views/main.fxml"), resources);

            Scene scene = new Scene(root);

            // Set minimum width and height
            try {
                InputStream is = getClass().getResourceAsStream("/values/dimen.properties");
                Properties prop = new Properties();
                prop.load(is);

                double min_width = Double.valueOf((String) prop.get("main_min_width"));
                double min_height = Double.valueOf((String) prop.get("main_min_height"));

                primaryStage.setMinWidth(min_width);
                primaryStage.setMinHeight(min_height);

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Show window
            primaryStage.setScene(scene);
            primaryStage.setTitle("UniShare");
            primaryStage.getIcons().add(new Image("/images/documents.png"));
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}