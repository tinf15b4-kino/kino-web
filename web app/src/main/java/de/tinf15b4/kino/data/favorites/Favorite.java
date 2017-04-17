package de.tinf15b4.kino.data.favorites;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.tinf15b4.kino.data.EntityModel;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.users.User;

@Entity
@Table(name = "favorite", uniqueConstraints = @UniqueConstraint(columnNames = { Favorite.FieldInfos.USER,
        Favorite.FieldInfos.CINEMA }))
public class Favorite extends EntityModel {

    public interface FieldInfos {
        String USER = "user";
        String CINEMA = "cinema";
    }

    @ManyToOne
    @JoinColumn(name = Favorite.FieldInfos.USER, nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = Favorite.FieldInfos.CINEMA, nullable = false)
    private Cinema cinema;

    public Favorite() {
    }

    public Favorite(User user, Cinema cinema) {
        this.user = user;
        this.cinema = cinema;
    }


    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
