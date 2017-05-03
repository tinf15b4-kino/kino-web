package de.tinf15b4.kino.data.playlists;

import java.util.Date;
import java.util.List;

import de.tinf15b4.kino.data.ServiceModel;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.playlists.Playlist;

public interface PlaylistService extends ServiceModel<Playlist> {

    List<Playlist> findForCinema(Cinema c, Date from, Date to);

    List<Playlist> findForMovie(Movie m, Date from, Date to);

}
