package it.unishare.common.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 6456911078026444714L;

    @Id
    @Column(name = "email")
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
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
