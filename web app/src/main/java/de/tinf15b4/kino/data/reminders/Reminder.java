package de.tinf15b4.kino.data.reminders;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.tinf15b4.kino.data.EntityModel;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.users.User;

@Entity
@Table(name = "reminder", uniqueConstraints = @UniqueConstraint(columnNames = { Reminder.FieldInfos.USER,
        Reminder.FieldInfos.MOVIE }))
public class Reminder extends EntityModel {

    public interface FieldInfos {
        String USER = "user";
        String MOVIE = "movie";

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

}
