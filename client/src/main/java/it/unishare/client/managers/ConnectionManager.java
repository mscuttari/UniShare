package it.unishare.client.managers;

import it.unishare.common.connection.dht.NoteFile;
import it.unishare.common.connection.dht.UniShareNode;
import it.unishare.client.utils.Settings;
import it.unishare.common.connection.server.RmiServerInterface;
import it.unishare.common.exceptions.*;
import it.unishare.common.models.User;
import it.unishare.common.utils.LogUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.prefs.Preferences;

public class ConnectionManager {

    // Debug
    private static final String TAG = "ConnectionManager";

    // Singleton
    private static ConnectionManager instance;

    // Connection
    private UniShareNode node = new UniShareNode();
    private User user;
    private BooleanProperty logged = new SimpleBooleanProperty(false);


    /**
     * Constructor
     */
    private ConnectionManager() {
        nodeBootstrap();

        logged.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Logged in
                node.setFilesProvider(new FilesManager(user));
                node.setReviewsProvider(new ReviewsManager(user));

                Collection<NoteFile> files = DatabaseManager.getInstance().getSharedFiles(user);
                node.storeFiles(files);

            } else {
                // Logged out
                node.setFilesProvider(null);
                node.setReviewsProvider(null);
                node.deleteAllFiles();
            }
        });
    }


    /**
     * Get singleton instance
     *
     * @return  singleton instance
     */
    public static ConnectionManager getInstance() {
        if (instance == null)
            instance = new ConnectionManager();

        return instance;
    }


    /**
     * Get server
     *
     * @return  server
     *
     * @throws  RemoteException         in case of managers error
     * @throws  NotBoundException       in case of no bind
     * @throws  MalformedURLException   in case of malformed URL
     */
    private RmiServerInterface getServer() throws RemoteException, NotBoundException, MalformedURLException {
        String serverAddress = Settings.getServerAddress();
        String serverPort = Settings.getServerPort();
        String rmiServerAddress = "rmi://" + serverAddress + ":" + serverPort + "/unishare";
        return (RmiServerInterface)Naming.lookup(rmiServerAddress);
    }


    /**
     * Get the logged property
     *
     * @return  logged property
     */
    public BooleanProperty loggedProperty() {
        return logged;
    }


    /**
     * Get logged in user information
     *
     * @return  user (null if not logged)
     */
    public User getUser() {
        return user;
    }


    /**
     * Try automatic login if the user logged in during the previous session
     */
    public void tryAutomaticLogin() {
        Preferences preferences = Preferences.userNodeForPackage(ConnectionManager.class);

        String email = preferences.get("email", "");
        String password = preferences.get("password", "");

        if (!email.isEmpty() && !password.isEmpty()) {
            CompletableFuture.runAsync(() -> {
                try {
                    login(email, password);
                } catch (RemoteException | MissingFieldException | NotFoundException | WrongPasswordException e) {
                    preferences.remove("email");
                    preferences.remove("password");
                }
            });
        }
    }


    /**
     * Login
     *
     * @param   email       email
     * @param   password    clear password
     *
     * @throws  RemoteException         in case of managers error
     * @throws  MissingFieldException   in case of missing field
     * @throws  NotFoundException       if the email is not associated with any user
     * @throws  WrongPasswordException  if the password is wrong
     */
    public void login(String email, String password) throws RemoteException, MissingFieldException, NotFoundException, WrongPasswordException {
        this.user = null;
        this.logged.set(false);

        User credentials = new User(email, password, null, null, null);
        LogUtils.d(TAG, "Logging in with " + email + ":" + password);

        try {
            this.user = getServer().login(credentials);
            this.logged.set(true);

            Preferences preferences = Preferences.userNodeForPackage(ConnectionManager.class);
            preferences.put("email", email);
            preferences.put("password", password);

            LogUtils.d(TAG, "Logged in");

        } catch (NotBoundException | MalformedURLException e) {
            throw new RemoteException(e.getMessage(), e);
        }
    }


    /**
     * Logout
     */
    public void logout() {
        this.user = null;
        this.logged.set(false);

        Preferences preferences = Preferences.userNodeForPackage(ConnectionManager.class);
        preferences.put("email", "");
        preferences.put("password", "");
    }


    /**
     * Signup
     *
     * @param   email       email
     * @param   password    clear password
     * @param   firstName   first name
     * @param   lastName    last name
     *
     * @throws  RemoteException             in case of managers error
     * @throws  MissingFieldException       in case of missing field
     * @throws  InvalidDataException        in case of invalid data
     * @throws  EmailAlreadyInUseException  if the email is already used by another user
     */
    public void signup(String email, String password, String firstName, String lastName) throws RemoteException, MissingFieldException, InvalidDataException, EmailAlreadyInUseException {
        this.user = null;
        this.logged.set(false);

        User user = new User(email, password, null, firstName, lastName);
        LogUtils.d(TAG, "Signing up with " + email + ":" + password);

        try {
            this.user = getServer().signup(user);
            this.logged.set(true);
            LogUtils.d(TAG, "Logged in");

        } catch (NotBoundException | MalformedURLException e) {
            throw new RemoteException(e.getMessage(), e);
        }
    }


    /**
     * Get Kademlia node
     *
     * @return  node
     */
    public UniShareNode getNode() {
        return node;
    }


    /**
     * Kademlia node bootstrap
     */
    private void nodeBootstrap() {
        // Connection retry period
        final int PERIOD = 30000;

        CompletableFuture.runAsync(() -> {
            try {
                LogUtils.d(TAG, "Node bootstrap");
                node.bootstrap(getServer().getKademliaInfo());
            } catch (Exception e) {
                LogUtils.e(TAG, "Node bootstrap failed. Retrying in " + (PERIOD / 1000) + "s");

                new Timer(true).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        nodeBootstrap();
                    }
                }, PERIOD);
            }
        });
    }

}
