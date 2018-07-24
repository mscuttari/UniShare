package it.unishare.client.connection.server;


import it.unishare.common.connection.RmiClientInterface;
import it.unishare.common.connection.RmiServerInterface;
import it.unishare.common.exceptions.*;
import it.unishare.common.models.User;
import it.unishare.common.utils.LogUtils;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class RmiClient implements RmiClientInterface {

    // Debug
    private static final String TAG = "RmiClient";

    // Connection
    private RmiServerInterface server;


    /**
     * Constructor
     *
     * @param   serverAddress       RMI server address
     * @throws  Exception           in case of connection error
     */
    public RmiClient(String serverAddress) throws Exception {
        server = (RmiServerInterface)Naming.lookup(serverAddress);
    }


    /**
     * Login
     *
     * @param   email       email
     * @param   password    clear password
     *
     * @return  user instance (null in case of login failure)
     */
    public User login(String email, String password) {
        try {
            User user = new User(email, password, null, null, null);
            LogUtils.d(TAG, "Logging in with " + email + ":" + password);
            return server.login(user);

        } catch (RemoteException e) {
            e.printStackTrace();

        } catch (MissingFieldException e) {
            LogUtils.e(TAG, "Missing field: " + e.getMissingField());

        } catch (NotFoundException e) {
            LogUtils.e(TAG, "Email not found");

        } catch (WrongPasswordException e) {
            LogUtils.e(TAG, "Wrong password");
        }

        return null;
    }


    /**
     * Signup
     *
     * @param   email       email
     * @param   password    clear password
     * @param   firstName   first name
     * @param   lastName    last name
     *
     * @return  user instance (null in case of signup failure)
     */
    public User signup(String email, String password, String firstName, String lastName) {
        try {
            User user = new User(email, password, null, firstName, lastName);
            LogUtils.d(TAG, "Signing up with " + email + ":" + password);
            return server.signup(user);

        } catch (RemoteException e) {
            e.printStackTrace();

        } catch (MissingFieldException e) {
            LogUtils.e(TAG, "Missing field: " + e.getMissingField());

        } catch (InvalidDataException e) {
            LogUtils.e(TAG, "Invalid field: " + e.getInvalidField());

        } catch (EmailAlreadyInUseException e) {
            LogUtils.e(TAG, "Email is already in use");
        }

        return null;
    }


    public static void main(String[] args) {
        try {
            RmiClient client = new RmiClient("rmi://127.0.0.1/unishare");
            User signup = client.signup("michele.scuttari@outlook.com", "provapassword", "Michele", "Scuttari");
            User login = client.login("michele.scuttari@outlook.com", "provapassword");
            LogUtils.d(TAG, "Everything good");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
