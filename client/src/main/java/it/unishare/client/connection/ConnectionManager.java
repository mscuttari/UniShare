package it.unishare.client.connection;

import it.unishare.common.connection.kademlia.KademliaNode;
import it.unishare.common.connection.kademlia.NND;
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
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionManager {

    // Debug
    private static final String TAG = "ConnectionManager";

    // Singleton
    private static ConnectionManager instance;

    // Connection
    private static final String SERVER_ADDRESS = "rmi://127.0.0.1/unishare";
    private KademliaNode node = new KademliaNode();
    private User user;
    private BooleanProperty logged = new SimpleBooleanProperty(false);


    /**
     * Constructor
     */
    private ConnectionManager() {
        nodeBootstrap();
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
     * @throws  RemoteException         in case of connection error
     * @throws  NotBoundException       in case of no bind
     * @throws  MalformedURLException   in case of malformed URL
     */
    private RmiServerInterface getServer() throws RemoteException, NotBoundException, MalformedURLException {
        return (RmiServerInterface)Naming.lookup(SERVER_ADDRESS);
    }


    /**
     * Check whether the user is logged in or not
     *
     * @return  true if logged in; false otherwise
     */
    public boolean isLogged() {
        return logged.get();
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
     * Login
     *
     * @param   email       email
     * @param   password    clear password
     *
     * @throws  RemoteException         in case of connection error
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
    }


    /**
     * Signup
     *
     * @param   email       email
     * @param   password    clear password
     * @param   firstName   first name
     * @param   lastName    last name
     *
     * @throws  RemoteException             in case of connection error
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
    public KademliaNode getNode() {
        return node;
    }


    /**
     * Kademlia node bootstrap
     */
    private void nodeBootstrap() {
        // Connection retry period
        final int PERIOD = 30000;

        try {
            LogUtils.d(TAG, "Node bootstrap");
            node.bootstrap(getServer().getKademliaInfo());
        } catch (Exception e) {
            LogUtils.e(TAG, "Node bootstrap failed. Retrying in " + (PERIOD / 1000) + "s");

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    nodeBootstrap();
                }
            }, PERIOD);
        }
    }


    /**
     * Get Kademlia access point information
     *
     * @return  access point information
     */
    private NND getKademliaBootstrapNodeInfo() {
        try {
            return getServer().getKademliaInfo();

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            LogUtils.e(TAG, "Can't get Kademlia bootstrap node information");
            e.printStackTrace();
        }

        return null;
    }

}
