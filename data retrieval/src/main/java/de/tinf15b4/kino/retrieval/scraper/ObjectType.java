package de.tinf15b4.kino.retrieval.scraper;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.utils.GsonFactory;

public enum ObjectType {
    CINEMA("/rest-private/insertCinema", "/rest/getCinemas"), MOVIE("/rest-private/insertMovie",
            "/rest/getMovies"), PLAYLIST("/rest-private/insertPlaylist", null);

    private String postUrl;
    private String getUrl;

    private ObjectType(String postUrl, String getUrl) {
        this.postUrl = postUrl;
        this.getUrl = getUrl;
    }

    public Object findObject(Reader r, Object object) {
        Gson gson = GsonFactory.buildGson();
        switch (this) {
        case MOVIE:
            List<Movie> movies = Lists.newArrayList(gson.fromJson(r, Movie[].class));
            Optional<Movie> foundMovie = movies.stream()
                    .filter(movie -> movie != null && movie.getTmdbId() == ((Movie) object).getTmdbId()).findAny();
            return foundMovie.orElse(null);
        case CINEMA:
            List<Cinema> cinemas = Lists.newArrayList(gson.fromJson(r, Cinema[].class));
            Optional<Cinema> foundCinema = cinemas.stream()
                    .filter(cinema -> cinema != null && cinema.getName().equals(((Cinema) object).getName())).findAny();
            return foundCinema.orElse(null);
        case PLAYLIST:
            // not needed
            break;
        }
        return null;
    }

    public String getGetUrl() {
        return getUrl;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public static ObjectType forClass(Class<?> clazz) {
        if (clazz.equals(Movie.class))
            return MOVIE;
        if (clazz.equals(Cinema.class))
            return CINEMA;
        if (clazz.equals(Playlist.class))
            return PLAYLIST;
        throw new IllegalArgumentException("Unknown object to save: " + clazz.getName());
    }
}
