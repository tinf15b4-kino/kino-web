package de.tinf15b4.kino.data.playlists;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Override
    public void save(Playlist p) {
        playlistRepository.save(p);
    }

    @Override
    public List<Playlist> findForCinema(Cinema c, Date from, Date to) {
        return playlistRepository.findForCinema(c, from, to);
    }

    @Override
    public List<Playlist> findForMovie(Movie m, Date from, Date to) {
        return playlistRepository.findForMovie(m, from, to);
    }

}
