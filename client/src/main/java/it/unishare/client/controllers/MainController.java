package it.unishare.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends AbstractController {

    @FXML private BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load left menu
        borderPaneSetLeft(borderPane, "menu", resources);
    }

}
