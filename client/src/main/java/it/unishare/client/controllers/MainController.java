package it.unishare.client.controllers;

import it.unishare.client.connection.server.RmiClient;
import it.unishare.common.connection.kademlia.NND;
import it.unishare.common.connection.kademlia.Node;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MainController extends AbstractController implements Initializable {

    @FXML private BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load left menu
        borderPaneSetLeft(borderPane, "menu", resources);

        try {
            RmiClient rmiClient = new RmiClient("rmi://127.0.0.1/unishare");
            NND dhtAccessPoint = rmiClient.getKademliaAccessPointInfo();

            Node node = new Node();
            node.bootstrap(dhtAccessPoint);

            System.out.println("DHT network joined");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
