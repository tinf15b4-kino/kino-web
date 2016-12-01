package de.tinf15b4.kino.data.ratedmovies;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.users.User;

@Embeddable
public class RatedMovieId implements Serializable {

    private static final long serialVersionUID = -2222477687071137133L;

    @ManyToOne
    private User user;

    @ManyToOne
    private Movie movie;

    public RatedMovieId(User user, Movie movie) {
        this.user = user;
        this.movie = movie;
    }

    public RatedMovieId() {
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
