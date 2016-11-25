package de.tinf15b4.kino.data.ratedmovies;

import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.users.User;

@Entity
public class RatedMovie {

    // FIXME: This should not have its own id
    @EmbeddedId
    private RatedMovieId id;

    private int rating;
    private String description;
    private Date time;

    public RatedMovie() {

    }

    public RatedMovie(RatedMovieId id, int rating, String description, Date time) {
        this.id = id;
        this.rating = rating;
        this.description = description;
        this.time = time;
    }

    public RatedMovie(User user, Movie movie, int rating, String description, Date time) {
        this(new RatedMovieId(user, movie), rating, description, time);
    }

    public RatedMovieId getId() {
        return id;
    }

    public void setId(RatedMovieId id) {
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
