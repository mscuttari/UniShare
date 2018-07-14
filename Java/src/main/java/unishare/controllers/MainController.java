package unishare.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends AbstractController implements Initializable {

    @FXML private TextField txtEmail;
    @FXML private TextField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
