package de.tinf15b4.kino.data.ratedcinemas;

import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.users.User;

@Entity
public class RatedCinema {

    @EmbeddedId
    private RatedCinemaId id;

    private int rating;
    private String description;
    private Date time;

    public RatedCinema() {
    }

    public RatedCinema(RatedCinemaId id, int rating, String description, Date time) {
        this.id = id;
        this.rating = rating;
        this.description = description;
        this.time = time;
    }

    public RatedCinema(User user, Cinema cinema, int rating, String description, Date time) {
        this(new RatedCinemaId(user, cinema), rating, description, time);
    }

    public RatedCinemaId getId() {
        return id;
    }

    public void setId(RatedCinemaId id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public User getUser() {
        return id.getUser();
    }

    public void setUser(User user) {
        id.setUser(user);
    }

    public Cinema getCinema() {
        return id.getCinema();
    }

    public void setCinema(Cinema cinema) {
        id.setCinema(cinema);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}