package de.tinf15b4.kino.data.favorites;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.users.User;

@Embeddable
public class FavoriteId implements Serializable {

    private static final long serialVersionUID = 7236718476657754622L;

    @ManyToOne
    private User user;

    @ManyToOne
    private Cinema cinema;

    public FavoriteId(User user, Cinema cinema) {
        this.user = user;
        this.cinema = cinema;
    }

    public FavoriteId() {

    }

    @Override
    public int hashCode() {
        return cinema.hashCode() + user.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof FavoriteId))
            return false;

        FavoriteId that = (FavoriteId) o;
        return user.getId() == that.getUser().getId() && cinema.getId() == that.getCinema().getId();

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }
}