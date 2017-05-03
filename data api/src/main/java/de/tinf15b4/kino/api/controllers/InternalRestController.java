package de.tinf15b4.kino.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieService;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.playlists.PlaylistService;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaService;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieService;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserService;

@RestController
public class InternalRestController {

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private RatedCinemaService ratedCinemaService;

    @Autowired
    private RatedMovieService ratedMovieService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "rest-private/clearEverything", method = RequestMethod.POST)
    public ResponseEntity<?> clearEverything() {
        ratedMovieService.deleteAll();
        ratedCinemaService.deleteAll();
        playlistService.deleteAll();
        favoriteService.deleteAll();
        movieService.deleteAll();
        cinemaService.deleteAll();
        userService.deleteAll();

        return ResponseEntity.ok("");
    }

    @RequestMapping(value = "rest-private/insertCinema", method = RequestMethod.POST)
    public ResponseEntity<?> insertCinema(@RequestBody Cinema cinema) {
        return ResponseEntity.ok(cinemaService.saveWithId(cinema));
    }

    @RequestMapping(value = "rest-private/insertMovie", method = RequestMethod.POST)
    public ResponseEntity<?> inserMovie(@RequestBody Movie movie) {
        return ResponseEntity.ok(movieService.saveWithId(movie));
    }

    @RequestMapping(value = "rest-private/insertFavorite", method = RequestMethod.POST)
    public ResponseEntity<?> insertFavorite(@RequestBody Favorite favorite) {
        return ResponseEntity.ok(favoriteService.saveWithId(favorite));
    }

    @RequestMapping(value = "rest-private/insertPlaylist", method = RequestMethod.POST)
    public ResponseEntity<?> insertPlaylist(@RequestBody Playlist playlist) {
        return ResponseEntity.ok(playlistService.saveWithId(playlist));
    }

    @RequestMapping(value = "rest-private/insertRatedCinema", method = RequestMethod.POST)
    public ResponseEntity<?> insertRatedCinema(@RequestBody RatedCinema ratedCinema) {
        return ResponseEntity.ok(ratedCinemaService.saveWithId(ratedCinema));
    }

    @RequestMapping(value = "rest-private/insertRatedMovie", method = RequestMethod.POST)
    public ResponseEntity<?> insertRatedMovie(@RequestBody RatedMovie ratedMovie) {
        return ResponseEntity.ok(ratedMovieService.saveWithId(ratedMovie));
    }

    @RequestMapping(value = "rest-private/insertUser", method = RequestMethod.POST)
    public ResponseEntity<?> insertCinema(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveWithId(user));
    }

    @RequestMapping(value = "rest-private/getAllUsers", method = RequestMethod.GET)
    public ResponseEntity<?> getUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users.toArray(new User[users.size()]));
    }

}
