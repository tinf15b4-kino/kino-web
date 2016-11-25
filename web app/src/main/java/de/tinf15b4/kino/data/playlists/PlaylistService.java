package de.tinf15b4.kino.data.playlists;

import java.util.Date;
import java.util.List;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;

public interface PlaylistService {

    void save(Playlist p);

    List<Playlist> findForCinema(Cinema c, Date from, Date to);

    List<Playlist> findForMovie(Movie m, Date from, Date to);

}
