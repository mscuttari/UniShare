package unishare.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController extends AbstractController implements Initializable {

    @FXML private Label lblUser;
    @FXML private Button btnLogout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Logout button
        btnLogout.setOnAction(event -> logout());
        setTooltip(btnLogout, resources, "logout");
    }


    /**
     * Logout
     */
    private void logout() {
        // TODO: logout
    }

}
