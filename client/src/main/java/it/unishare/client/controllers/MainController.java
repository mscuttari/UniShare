package it.unishare.client.controllers;

import it.unishare.client.connection.ConnectionManager;
import it.unishare.client.database.DatabaseManager;
import it.unishare.common.connection.kademlia.KademliaNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class MainController extends AbstractController {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConnectionManager.getInstance().tryAutomaticLogin();
    }

}
