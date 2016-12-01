package de.tinf15b4.kino.data.favorites;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.users.User;

@Entity
public class Favorite {

    @EmbeddedId
    private FavoriteId id;

    public Favorite() {
    }

    public Favorite(FavoriteId id) {
        this.id = id;
    }

    public Favorite(User user, Cinema cinema) {
        this(new FavoriteId(user, cinema));
    }


    public Cinema getCinema() {
        return id.getCinema();
    }

    public void setCinema(Cinema cinema) {
        this.id.setCinema(cinema);
    }

    public User getUser() {
        return id.getUser();
    }

    public void setUser(User user) {
        this.id.setUser(user);
        ;
    }

}
