package de.tinf15b4.kino.data.search;

import java.util.Collections;
import java.util.List;

import de.tinf15b4.kino.data.ImageContainer;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;

public class SearchResult implements ImageContainer {
    private List<Cinema> cinemas;
    private List<Movie> movies;

    public List<Cinema> getCinemas() {
        return cinemas;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public boolean hasMovies() {
        return !movies.isEmpty();
    }

    public boolean hasCinemas() {
        return !cinemas.isEmpty();
    }

    public SearchResult(List<Cinema> cinemas, List<Movie> movies) {
        this.cinemas = Collections.unmodifiableList(cinemas);
        this.movies = Collections.unmodifiableList(movies);
    }

    @Override
    public void doFilter() {
        cinemas.stream().forEach(Cinema::doFilter);
        movies.stream().forEach(Movie::doFilter);
    }
}