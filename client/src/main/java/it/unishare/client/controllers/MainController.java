package it.unishare.client.controllers;

import it.unishare.client.connection.server.RmiClient;
import it.unishare.common.connection.kademlia.KademliaFile;
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

            /*Node<String> node1 = new Node<>();
            node1.bootstrap(dhtAccessPoint);

            Node<String> node2 = new Node<>();
            node2.bootstrap(dhtAccessPoint);*/

            Node node3 = new Node();
            node3.bootstrap(dhtAccessPoint);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    KademliaFile file = new KademliaFile("prova", node3.getInfo());
                    node3.storeData(file);
                }
            }, 5000);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
