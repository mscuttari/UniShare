package it.unishare.client.controllers;

import it.unishare.client.connection.ConnectionManager;
import it.unishare.common.models.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController extends AbstractController implements Initializable {

    @FXML private Label lblUser;
    @FXML private Label lblLogin;
    @FXML private Button btnLogout;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        // Login
        lblLogin.setText(resources.getString("login"));
        lblLogin.setOnMouseClicked(event -> showLoginScreen());
        setLoginEnabled(true);

        // Logout button
        btnLogout.setOnAction(event -> logout());
        setTooltip(btnLogout, resources, "logout");

        // Login status
        ConnectionManager.getInstance().loggedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                User user = ConnectionManager.getInstance().getUser();
                lblUser.setText(user.getFirstName() + " " + user.getLastName());
                setLoginEnabled(false);
            } else {
                setLoginEnabled(true);
            }
        });
    }


    /**
     * Enable the login button
     *
     * @param   enabled     whether the user has not logged in (true) or it is already logged in (false)
     */
    private void setLoginEnabled(boolean enabled) {
        if (enabled) {
            lblLogin.setVisible(true);
            lblUser.setVisible(false);
            btnLogout.setVisible(false);
        } else {
            lblLogin.setVisible(false);
            lblUser.setVisible(true);
            btnLogout.setVisible(true);
        }
    }


    /**
     * Show login screen
     */
    private void showLoginScreen() {
        PopOver popOver = createPopOver("login", resources);
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_LEFT);
        popOver.setAutoFix(true);
        popOver.setAutoHide(true);
        popOver.setHideOnEscape(true);
        popOver.setDetachable(false);
        popOver.show(lblLogin);
    }


    /**
     * Logout
     */
    private void logout() {
        ConnectionManager.getInstance().logout();
    }

}
