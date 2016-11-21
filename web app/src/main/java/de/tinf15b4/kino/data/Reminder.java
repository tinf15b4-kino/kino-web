package de.tinf15b4.kino.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Reminder {
    // FIXME: This should not have its own id
    @Id
    @GeneratedValue

    private long id;
    // @Id
    @ManyToOne
    private User user;

    // @Id
    @ManyToOne
    private Movie movie;

    public Reminder(User user, Movie movie) {
        this.user = user;
        this.movie = movie;
    }

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
