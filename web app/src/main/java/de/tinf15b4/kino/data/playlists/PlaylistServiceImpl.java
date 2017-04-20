package de.tinf15b4.kino.data.playlists;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.ServiceImplModel;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;

@Service
public class PlaylistServiceImpl extends ServiceImplModel<Playlist, PlaylistRepository> implements PlaylistService {

    @Override
    public List<Playlist> findForCinema(Cinema c, Date from, Date to) {
        return repository.findForCinema(c, from, to);
    }

    @Override
    public List<Playlist> findForMovie(Movie m, Date from, Date to) {
        return repository.findForMovie(m, from, to);
    }

}
