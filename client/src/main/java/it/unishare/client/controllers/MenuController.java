package it.unishare.client.controllers;

import it.unishare.client.connection.ConnectionManager;
import it.unishare.client.layout.SidebarButton;
import it.unishare.common.models.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController extends AbstractController implements Initializable {

    @FXML private BorderPane rootPane;

    @FXML private SidebarButton btnSearch;
    @FXML private SidebarButton btnMyFiles;
    @FXML private SidebarButton btnSettings;

    @FXML private HBox boxLogin;
    @FXML private Label lblLogin;
    @FXML private Label lblSignup;

    @FXML private BorderPane boxUser;
    @FXML private Label lblUser;
    @FXML private Button btnLogout;

    private ResourceBundle resources;
    private String currentView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        // Menu entries
        ToggleGroup toggleGroup = new ToggleGroup();

        btnSearch.setToggleGroup(toggleGroup);
        btnMyFiles.setToggleGroup(toggleGroup);
        btnSettings.setToggleGroup(toggleGroup);

        // Prevent the menu from not having a toggle selected
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                oldValue.setSelected(true);
        });

        // Select the network button
        btnSearch.setSelected(true);

        // Login
        lblLogin.setOnMouseClicked(event -> showLoginScreen());

        // Signup
        lblSignup.setOnMouseClicked(event -> showSignupScreen());

        // Logout button
        btnLogout.setOnAction(event -> logout());
        setTooltip(btnLogout, resources, "logout");

        // Login status
        setLoginEnabled(!ConnectionManager.getInstance().isLogged());

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
     * Show "Search notes" page
     */
    @FXML
    private void searchNotes() {

    }


    /**
     * Show "My files" page
     */
    @FXML
    private void myFiles() {

    }


    /**
     * Show "Settings" page
     */
    @FXML
    private void settings() {
        setView("settings");
    }


    /**
     * Get main application {@link BorderPane}
     *
     * @return  BorderPane
     */
    private BorderPane getParentPane() {
        return (BorderPane) rootPane.getParent();
    }


    /**
     * Set view
     *
     * @param   viewName    FXML file name to be loaded
     */
    private void setView(String viewName) {
        if (currentView == null || !currentView.equals(viewName)) {
            currentView = viewName;
            borderPaneSetCenter(getParentPane(), currentView, resources);
        }
    }


    /**
     * Enable the login button
     *
     * @param   enabled     whether the user has not logged in (true) or it is already logged in (false)
     */
    private void setLoginEnabled(boolean enabled) {
        if (enabled) {
            boxUser.setVisible(false);
            lblLogin.setVisible(true);
            lblSignup.setVisible(true);
            boxLogin.setVisible(true);
        } else {
            boxLogin.setVisible(false);
            lblLogin.setVisible(false);
            lblSignup.setVisible(false);
            boxUser.setVisible(true);
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
     * Show signup screen
     */
    private void showSignupScreen() {
        PopOver popOver = createPopOver("signup", resources);
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_LEFT);
        popOver.setAutoFix(true);
        popOver.setAutoHide(true);
        popOver.setHideOnEscape(true);
        popOver.setDetachable(false);
        popOver.show(lblSignup);
    }


    /**
     * Logout
     */
    private void logout() {
        ConnectionManager.getInstance().logout();
    }

}
