package de.tinf15b4.kino.data.users;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User {
    // TODO: Make name or email a primary key, or at least enforce their
    // uniqueness
    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String email;
    private String favLocation; // TODO: Numeric Zip Code?

    // FIXME: PLAIN TEXT PASSWORD STORAGE OMG PLEASE KILL ME WHAT AM I DOING
    // WITH MY LIFE
    private String password;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
