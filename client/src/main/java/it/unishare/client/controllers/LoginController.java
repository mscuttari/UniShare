package it.unishare.client.controllers;

import it.unishare.client.managers.ConnectionManager;
import it.unishare.common.exceptions.MissingFieldException;
import it.unishare.common.exceptions.NotFoundException;
import it.unishare.common.exceptions.WrongPasswordException;
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
import java.util.concurrent.CompletableFuture;

public class LoginController extends AbstractController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;

    private ResourceBundle resources;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        // Login button
        btnLogin.setOnAction(event -> login());

        // Submit form on enter key press
        EventHandler<KeyEvent> keyPressEvent = event -> {
            if (event.getCode() == KeyCode.ENTER)
                login();
        };

        txtEmail.setOnKeyPressed(keyPressEvent);
        txtPassword.setOnKeyPressed(keyPressEvent);
        btnLogin.setOnKeyPressed(keyPressEvent);
    }


    /**
     * Login
     */
    private void login() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        CompletableFuture.runAsync(() -> {
            try {
                ConnectionManager.getInstance().login(email, password);

            } catch (RemoteException e) {
                showErrorDialog(resources.getString("login"), resources.getString("connection_error"));

            } catch (MissingFieldException e) {
                showErrorDialog(
                        resources.getString("login"),
                        resources.getString("missing_field") + ": " + resources.getString(e.getMissingField().toLowerCase())
                );

            } catch (NotFoundException e) {
                showErrorDialog(resources.getString("login"), resources.getString("user_not_found"));

            } catch (WrongPasswordException e) {
                showErrorDialog(resources.getString("login"), resources.getString("wrong_password"));
            }
        });
    }

}
