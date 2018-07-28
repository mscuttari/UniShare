package it.unishare.common.connection.server;

import it.unishare.common.connection.kademlia.NND;
import it.unishare.common.exceptions.*;
import it.unishare.common.models.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiServerInterface extends Remote {

    /**
     * Login
     *
     * @param   user    user with non empty email and password
     *
     * @throws  RemoteException         in case of connection error
     * @throws  MissingFieldException   if email or password are missing
     * @throws  NotFoundException       if no user has the specified email
     * @throws  WrongPasswordException  if the password is wrong
     */
    User login(User user) throws RemoteException, MissingFieldException, NotFoundException, WrongPasswordException;


    /**
     * Signup
     *
     * @param   user    user with non empty email, clear password, first name and last name
     *
     * @throws  RemoteException         in case of connection error
     * @throws  MissingFieldException   if email, password, first name or last name are missing
     * @throws  InvalidDataException    if email or password are invalid
     */
    User signup(User user) throws RemoteException, MissingFieldException, InvalidDataException, EmailAlreadyInUseException;


    /**
     * Get information representing the server in the Kademlia network
     *
     * @return  node information {see {@link NND}}
     * @throws  RemoteException in case of connection error
     */
    NND getKademliaInfo() throws RemoteException;

}