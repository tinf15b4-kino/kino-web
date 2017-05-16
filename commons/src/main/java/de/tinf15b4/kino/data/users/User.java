package de.tinf15b4.kino.data.users;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.tinf15b4.kino.data.EntityModel;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = { User.FieldInfos.NAME }))
public class User extends EntityModel implements Serializable {
    // TODO: Make name or email a primary key, or at least enforce their
    // uniqueness
    // --- Made name unique for now to fit cucumber tests, Simon (17.04.2017)

    private static final long serialVersionUID = -6096242601376594653L;

    public interface FieldInfos {
        String NAME = "name";
        String EMAIL = "email";
        String PASSWORD = "password";
        String FAVORITE_LOCATION = "favLocation";
    }

    @Column(name = User.FieldInfos.NAME, nullable = false)
    private String name;

    @Column(name = User.FieldInfos.EMAIL)
    private String email;

    @Column(name = User.FieldInfos.FAVORITE_LOCATION)
    private String favLocation; // TODO: Numeric Zip Code?

    // FIXME: PLAIN TEXT PASSWORD STORAGE OMG PLEASE KILL ME WHAT AM I DOING
    // WITH MY LIFE
    @Column(name = User.FieldInfos.PASSWORD)
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFavLocation() {
        return favLocation;
    }

    public void setFavLocation(String favLocation) {
        this.favLocation = favLocation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
