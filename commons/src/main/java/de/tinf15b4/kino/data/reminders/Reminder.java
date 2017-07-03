package de.tinf15b4.kino.data.reminders;

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
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.users.User;

@Entity
@Table(name = "reminder", uniqueConstraints = @UniqueConstraint(columnNames = { Reminder.FieldInfos.USER,
        Reminder.FieldInfos.MOVIE, Reminder.FieldInfos.CINEMA, Reminder.FieldInfos.PLAYTIME }))
public class Reminder extends EntityModel implements ImageContainer {

    static class FieldInfos {
        private FieldInfos() {
            // Just used to hide the public constructor
            // Sonarqube seems to like that
        }

        public static final String USER = "user";
        public static final String MOVIE = "movie";
        public static final String CINEMA = "cinema";
        public static final String PLAYTIME = "playtime";
    }

    @ManyToOne
    @JoinColumn(name = Reminder.FieldInfos.USER, nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = Reminder.FieldInfos.MOVIE, nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = Reminder.FieldInfos.CINEMA, nullable = false)
    private Cinema cinema;

    @Column(name = Reminder.FieldInfos.PLAYTIME, nullable = false)
    private Date playtime;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public Date getPlaytime() {
        return playtime;
    }

    public void setPlaytime(Date playtime) {
        this.playtime = playtime;
    }

    @Override
    public void doFilter() {
        this.movie.setCover(null);
    }

}
