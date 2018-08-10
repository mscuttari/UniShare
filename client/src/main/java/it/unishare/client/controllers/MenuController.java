package it.unishare.client.controllers;

import it.unishare.client.managers.ConnectionManager;
import it.unishare.client.layout.SidebarButton;
import it.unishare.common.models.User;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController extends AbstractController {

    @FXML private BorderPane rootPane;

    @FXML private SidebarButton btnSearch;
    @FXML private SidebarButton btnDownloads;
    @FXML private SidebarButton btnShare;
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
        btnDownloads.setToggleGroup(toggleGroup);
        btnShare.setToggleGroup(toggleGroup);
        btnSettings.setToggleGroup(toggleGroup);

        // Prevent the menu from not having a toggle selected
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                oldValue.setSelected(true);
        });

        // Login
        lblLogin.setOnMouseClicked(event -> showLoginScreen());

        // Signup
        lblSignup.setOnMouseClicked(event -> showSignupScreen());

        // Logout button
        btnLogout.setOnAction(event -> logout());
        setTooltip(btnLogout, resources, "logout");

        // Login status
        btnDownloads.disableProperty().bind(Bindings.not(ConnectionManager.getInstance().loggedProperty()));
        btnShare.disableProperty().bind(Bindings.not(ConnectionManager.getInstance().loggedProperty()));
        ConnectionManager.getInstance().loggedProperty().addListener((observable, oldValue, newValue) -> checkLoginStatus(newValue));
    }


    /**
     * Show "Search notes" page
     */
    @FXML
    private void searchNotes() {
        setView("search");
    }


    /**
     * Show "Downloads" page
     */
    @FXML
    private void downloads() {
        setView("downloads");
    }


    /**
     * Show "My files" page
     */
    @FXML
    private void share() {
        setView("share");
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
     * Check the login status and consequentially enable or disable some features
     *
     * @param   logged      whether the user has logged in (true) or not (false)
     */
    private void checkLoginStatus(boolean logged) {
        if (logged) {
            // Hide the login box
            boxLogin.setVisible(false);
            lblLogin.setVisible(false);
            lblSignup.setVisible(false);

            // Show the user info
            User user = ConnectionManager.getInstance().getUser();
            lblUser.setText(user.getFullName());
            boxUser.setVisible(true);

        } else {
            // Hide the user info
            boxUser.setVisible(false);

            // Show the login box
            lblLogin.setVisible(true);
            lblSignup.setVisible(true);
            boxLogin.setVisible(true);

            // If the last shown page is a private one, show the "Search files" page
            if (btnDownloads.isSelected() || btnShare.isSelected()) {
                searchNotes();
            }

            btnSearch.setSelected(true);
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
