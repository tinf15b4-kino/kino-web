package de.tinf15b4.kino.retrieval.scraper;

import java.util.List;

import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;

public class GatheringResult {

    private List<Movie> movies;
    private List<Playlist> playlists;

    public GatheringResult(List<Movie> movies, List<Playlist> playlists) {
        this.movies = movies;
        this.playlists = playlists;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

}
