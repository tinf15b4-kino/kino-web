package de.tinf15b4.kino.data.playlists;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.tinf15b4.kino.data.EntityModel;
import de.tinf15b4.kino.data.ImageContainer;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;

@Entity
@Table(name = "playlist")
public class Playlist extends EntityModel implements ImageContainer {

    public interface FieldInfos {
        String CINEMA = "cinema";
        String MOVIE = "movie";
        String TIME = "time";
        String PRICE = "price";
    }

    @ManyToOne
    @JoinColumn(name = Playlist.FieldInfos.CINEMA, nullable = false)
    private Cinema cinema;

    @ManyToOne
    @JoinColumn(name = Playlist.FieldInfos.MOVIE, nullable = false)
    private Movie movie;

    @Column(name = Playlist.FieldInfos.TIME, nullable = false)
    private Date time;

    @Column(name = Playlist.FieldInfos.PRICE, nullable = false)
    private int price; // in cents

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    @Override
    public void doFilter() {
        this.cinema.setImage(null);
        this.movie.setCover(null);
    }
}
