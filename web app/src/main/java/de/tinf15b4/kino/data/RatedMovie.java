package de.tinf15b4.kino.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class RatedMovie {

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

    private int rating;
    private String description;

    public RatedMovie(User user, Movie movie, int rating, String description) {
        this.user = user;
        this.movie = movie;
        this.rating = rating;
        this.description = description;
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
