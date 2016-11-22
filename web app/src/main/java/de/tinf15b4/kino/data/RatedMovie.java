package de.tinf15b4.kino.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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

@Embeddable
class RatedMovieId implements Serializable {

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
