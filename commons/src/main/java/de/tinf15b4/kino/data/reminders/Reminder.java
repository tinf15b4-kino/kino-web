package de.tinf15b4.kino.data.reminders;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.tinf15b4.kino.data.EntityModel;
import de.tinf15b4.kino.data.ImageContainer;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.users.User;

@Entity
@Table(name = "reminder", uniqueConstraints = @UniqueConstraint(columnNames = { Reminder.FieldInfos.USER,
        Reminder.FieldInfos.MOVIE }))
public class Reminder extends EntityModel implements ImageContainer {

    static class FieldInfos {
        private FieldInfos() {
            // Just used to hide the public constructor
            // Sonarqube seems to like that
        }

        public static final String USER = "user";
        public static final String MOVIE = "movie";
    }

    @ManyToOne
    @JoinColumn(name = Reminder.FieldInfos.USER, nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = Reminder.FieldInfos.MOVIE, nullable = false)
    private Movie movie;

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

    @Override
    public void doFilter() {
        this.movie.setCover(null);
    }

}
