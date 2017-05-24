package de.tinf15b4.kino.data.ratedcinemas;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.tinf15b4.kino.data.EntityModel;
import de.tinf15b4.kino.data.ImageContainer;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.users.User;

@Entity
@Table(name = "ratedCinema", uniqueConstraints = @UniqueConstraint(columnNames = { RatedCinema.FieldInfos.USER,
        RatedCinema.FieldInfos.CINEMA }))
public class RatedCinema extends EntityModel implements ImageContainer {

    static class FieldInfos {
        private FieldInfos() {
            // Just used to hide the public constructor
            // Sonarqube seems to like that
        }

        public static final String USER = "user";
        public static final String CINEMA = "cinema";
        public static final String RATING = "rating";
        public static final String DESCRIPTION = "description";
        public static final String TIME = "time";
    }

    @ManyToOne
    @JoinColumn(name = RatedCinema.FieldInfos.USER, nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = RatedCinema.FieldInfos.CINEMA, nullable = false)
    private Cinema cinema;

    @Column(name = RatedCinema.FieldInfos.RATING, nullable = false)
    private int rating;

    @Column(name = RatedCinema.FieldInfos.DESCRIPTION, nullable = true)
    private String description;

    @Column(name = RatedCinema.FieldInfos.TIME, nullable = false)
    private Date time;

    public RatedCinema() {
    }

    public RatedCinema(User user, Cinema cinema, int rating, String description, Date time) {
        this.user = user;
        this.cinema = cinema;
        this.rating = rating;
        this.description = description;
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

    @Override
    public void doFilter() {
        this.cinema.setImage(null);
    }
}