package it.unishare.client.controllers;

import it.unishare.client.connection.ConnectionManager;
import it.unishare.common.exceptions.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class SignupController extends AbstractController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private Button btnSignup;

    private ResourceBundle resources;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        // Signup button
        btnSignup.setOnAction(event -> signup());

        // Submit form on enter key press
        EventHandler<KeyEvent> keyPressEvent = event -> {
            if (event.getCode() == KeyCode.ENTER)
                signup();
        };

        txtEmail.setOnKeyPressed(keyPressEvent);
        txtPassword.setOnKeyPressed(keyPressEvent);
        txtFirstName.setOnKeyPressed(keyPressEvent);
        txtLastName.setOnKeyPressed(keyPressEvent);
        btnSignup.setOnKeyPressed(keyPressEvent);
    }


    /**
     * Login
     */
    private void signup() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();

        try {
            ConnectionManager.getInstance().signup(email, password, firstName, lastName);

        } catch (RemoteException e) {
            showErrorDialog(resources.getString("signup"), resources.getString("connection_error"));

        } catch (MissingFieldException e) {
            showErrorDialog(
                    resources.getString("signup"),
                    resources.getString("missing_field") + ": " + resources.getString(e.getMissingField().toLowerCase())
            );

        } catch (InvalidDataException e) {
            showErrorDialog(
                    resources.getString("signup"),
                    resources.getString("invalid_data") + ": " + resources.getString(e.getInvalidField().toLowerCase())
            );

        } catch (EmailAlreadyInUseException e) {
            showErrorDialog(resources.getString("signup"), resources.getString("email_already_in_use"));
        }
    }

}