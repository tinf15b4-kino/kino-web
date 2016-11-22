package de.tinf15b4.kino.data;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Reminder {

    @EmbeddedId
    private ReminderId id;

    public ReminderId getId() {
        return id;
    }

    public void setId(ReminderId id) {
        this.id = id;
    }

    public User getUser() {
        return id.getUser();
    }

    public void setUser(User user) {
        this.id.setUser(user);
    }

    public Movie getMovie() {
        return id.getMovie();
    }

    public void setMovie(Movie movie) {
        this.id.setMovie(movie);
    }

}

@Embeddable
class ReminderId implements Serializable {

    private static final long serialVersionUID = 27317125927302502L;

    @ManyToOne
    private User user;

    @ManyToOne
    private Movie movie;

    public ReminderId(User user, Movie movie) {
        this.user = user;
        this.movie = movie;
    }

    public ReminderId() {
    }

    @Override
    public int hashCode() {
        return movie.hashCode() + user.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RatedMovieId))
            return false;

        RatedMovieId that = (RatedMovieId) o;
        return user.getId() == that.getUser().getId() && movie.getId() == that.getMovie().getId();
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

