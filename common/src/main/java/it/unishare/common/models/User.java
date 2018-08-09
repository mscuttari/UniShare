package it.unishare.common.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 6456911078026444714L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 150, nullable = false)
    private String password;

    @Column(name = "salt", length = 16, nullable = false)
    private String salt;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;


    /**
     * Default constructor
     */
    public User() {
        this(null, null, null, null, null);
    }


    /**
     * Constructor
     *
     * @param   email       email
     * @param   password    hashed password
     * @param   salt        salt used to hash the password
     * @param   firstName   first name
     * @param   lastName    last name
     */
    public User(String email, String password, String salt, String firstName, String lastName) {
        setEmail(email);
        setPassword(password);
        setSalt(salt);
        setFirstName(firstName);
        setLastName(lastName);
    }


    /**
     * Get ID
     *
     * @return  ID
     */
    public long getId() {
        return id;
    }


    /**
     * Get email
     *
     * @return  email
     */
    public String getEmail() {
        return email;
    }


    /**
     * Set email
     *
     * @param   email   email
     */
    public void setEmail(String email) {
        this.email = email == null || email.isEmpty() ? null : email;
    }


    /**
     * Get password (clear password if {@link #getSalt()} is null)
     *
     * @return  password
     */
    public String getPassword() {
        return password;
    }


    /**
     * Set password
     *
     * @param   password    password
     */
    public void setPassword(String password) {
        this.password = password == null || password.isEmpty() ? null : password;
    }


    /**
     * Get salt to be used to encrypt the password
     *
     * @return  salt
     */
    public String getSalt() {
        return salt;
    }


    /**
     * Set salt to be used to encrypt the password
     *
     * @param   salt    salt
     */
    public void setSalt(String salt) {
        this.salt = salt == null || salt.isEmpty() ? null : salt;
    }


    /**
     * Get first name
     *
     * @return  first name
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * Set first name
     *
     * @param   firstName   first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName == null || firstName.isEmpty() ? null : firstName;
    }


    /**
     * Get last name
     *
     * @return  last name
     */
    public String getLastName() {
        return lastName;
    }


    /**
     * Set last name
     *
     * @param   lastName    last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName == null || lastName.isEmpty() ? null : lastName;
    }

}
