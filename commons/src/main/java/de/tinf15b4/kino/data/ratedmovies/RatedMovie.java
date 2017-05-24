package de.tinf15b4.kino.data.ratedmovies;

import java.util.Date;

import javax.persistence.Column;
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
@Table(name = "ratedMovie", uniqueConstraints = @UniqueConstraint(columnNames = { RatedMovie.FieldInfos.USER,
        RatedMovie.FieldInfos.MOVIE }))
public class RatedMovie extends EntityModel implements ImageContainer {

    static class FieldInfos {
        private FieldInfos() {
            // Just used to hide the public constructor
            // Sonarqube seems to like that
        }

        public static final String USER = "user";
        public static final String MOVIE = "movie";
        public static final String RATING = "rating";
        public static final String DESCRIPTION = "description";
        public static final String TIME = "time";
    }

    @ManyToOne
    @JoinColumn(name = RatedMovie.FieldInfos.USER, nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = RatedMovie.FieldInfos.MOVIE, nullable = false)
    private Movie movie;

    @Column(name = RatedMovie.FieldInfos.RATING, nullable = false)
    private int rating;

    @Column(name = RatedMovie.FieldInfos.DESCRIPTION, nullable = true)
    private String description;

    @Column(name = RatedMovie.FieldInfos.TIME, nullable = false)
    private Date time;

    public RatedMovie() {

    }

    public RatedMovie(User user, Movie movie, int rating, String description, Date time) {
        this.user = user;
        this.movie = movie;
        this.rating = rating;
        this.description = description;
        this.time = time;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public void doFilter() {
        this.movie.setCover(null);
    }

}
