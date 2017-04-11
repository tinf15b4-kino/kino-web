package de.tinf15b4.kino.api.rest;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieService;
import de.tinf15b4.kino.data.playlists.Playlist;
import de.tinf15b4.kino.data.playlists.PlaylistService;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaService;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieService;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserService;

@RestController
public class KinoRestController {

    public static final String NOT_NULL = "Parameters or request body must not be null";
    public static final String WRONG_PASSWORD = "Wrong username or password";
    public static final String USER_LOGGED_IN = "This user is already logged in";
    public static final String TOKEN_INVALID = "Token invalid or expired";
    public static final String LOGOUT_SUCCESS = "Logout successful";
    public static final String INVALID_ID = "Id does not exist";

    private ConcurrentHashMap<Token, String> tokens = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Token> users = new ConcurrentHashMap<>();

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private RatedCinemaService ratedCinemaService;

    @Autowired
    private RatedMovieService ratedMovieService;

    @Autowired
    private PlaylistService playlistService;

    @RequestMapping(value = "rest/authorize", method = RequestMethod.GET)
    public ResponseEntity<?> authorize(@RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password) throws UnsupportedEncodingException {
        if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(NOT_NULL);

        User user = userService.findByName(username);

        if (users.containsKey(username))
            return ResponseEntity.badRequest().body(USER_LOGGED_IN);

        if (user != null && password.equals(user.getPassword())) {
            SecureRandom random = new SecureRandom();
            String tokenKey = new BigInteger(256, random).toString(32);
            Token token = new Token(tokenKey);
            updateTokenForUser(username, token);
            return ResponseEntity.ok(tokenKey);
        } else
            return ResponseEntity.badRequest().body(WRONG_PASSWORD);
    }

    @RequestMapping(value = "rest/logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout(@RequestParam(name = "token") String token) {
        ResponseEntity<?> response = checkToken(token);
        if (response.getStatusCode() == HttpStatus.OK) {
            synchronized (tokens) {
                users.remove(tokens.remove(new Token(token)));
            }
            return ResponseEntity.ok(LOGOUT_SUCCESS);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALID);
    }

    @RequestMapping(value = "rest/getUser", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@RequestParam(name = "token") String token) {
        ResponseEntity<?> response = checkToken(token);
        if (response.getStatusCode() != HttpStatus.OK)
            return response;
        return ResponseEntity.ok(userService.findByName((String) response.getBody()));
    }

    @RequestMapping(value = "rest/getCinemas", method = RequestMethod.GET)
    public ResponseEntity<?> getCinemas() {
        return ResponseEntity.ok(cinemaService.findAll().toArray(new Cinema[0]));
    }

    @RequestMapping(value = "rest/getMovies", method = RequestMethod.GET)
    public ResponseEntity<?> getMovies() {
        return ResponseEntity.ok(movieService.findAll().toArray(new Movie[0]));
    }

    @RequestMapping(value = "rest/getPlaylistForCinema", method = RequestMethod.GET)
    public ResponseEntity<?> getPlaylistForCinema(@RequestParam(name = "cinemaId") long cinemaId,
            @RequestParam(name = "from") String from, @RequestParam(name = "to") String to) throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Cinema cinema = cinemaService.findOne(cinemaId);
            if (cinema != null)
                return ResponseEntity
                        .ok(playlistService.findForCinema(cinema, dateFormatter.parse(from), dateFormatter.parse(to))
                                .toArray(new Playlist[0]));
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.badRequest().body(INVALID_ID);
    }

    @RequestMapping(value = "rest/getPlaylistForMovie", method = RequestMethod.GET)
    public ResponseEntity<?> getPlaylistForMovie(@RequestParam(name = "movieId") long movieId,
            @RequestParam(name = "from") String from, @RequestParam(name = "to") String to) throws ParseException {
        try {
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Movie movie = movieService.findOne(movieId);
            if (movie != null)
                return ResponseEntity
                        .ok(playlistService.findForMovie(movie, dateFormatter.parse(from), dateFormatter.parse(to))
                                .toArray(new Playlist[0]));
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.badRequest().body(INVALID_ID);
    }

    @RequestMapping(value = "rest/getRatedMovies", method = RequestMethod.GET)
    public ResponseEntity<?> getRatedMovies(@RequestParam(name = "movieId") long movieId) {
        Movie movie = movieService.findOne(movieId);
        if (movie != null)
            return ResponseEntity.ok(ratedMovieService.findRatingsByMovie(movie).toArray(new RatedMovie[0]));
        return ResponseEntity.badRequest().body(INVALID_ID);
    }

    @RequestMapping(value = "rest/getRatedCinemas", method = RequestMethod.GET)
    public ResponseEntity<?> getRatedCinemas(@RequestParam(name = "cinemaId") long cinemaId) {
        Cinema cinema = cinemaService.findOne(cinemaId);
        if (cinema != null)
            return ResponseEntity.ok(
                    ratedCinemaService.findRatingsByCinema(cinemaService.findOne(cinemaId)).toArray(new RatedMovie[0]));
        return ResponseEntity.badRequest().body(INVALID_ID);
    }

    @RequestMapping(value = "rest/getFavorites", method = RequestMethod.GET)
    public ResponseEntity<?> getFavorites(@RequestParam(name = "token") String token) {
        ResponseEntity<?> response = checkToken(token);
        if (response.getStatusCode() != HttpStatus.OK)
            return response;
        User user = userService.findByName((String) response.getBody());
        return ResponseEntity.ok(favoriteService.getAllFavoritesForUser(user).toArray(new Favorite[0]));
    }

    private ResponseEntity<?> checkToken(String tokenKey) {
        if (!Strings.isNullOrEmpty(tokenKey)) {
            Token token = new Token(tokenKey);
            String username;
            if (token.isValid() && !Strings.isNullOrEmpty(username = tokens.get(token))) {
                updateTokenForUser(username, token);
                return ResponseEntity.ok(username);
            } else {
                removeToken(token);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_INVALID);
    }

    private void removeToken(Token token) {
        updateTokenForUser(null, token);
    }

    private void updateTokenForUser(String username, Token token) {
        if (username == null) {
            synchronized (tokens) {
                String tokenKey = tokens.remove(token);
                if (!Strings.isNullOrEmpty(tokenKey))
                    users.remove(tokenKey);
            }
        } else {
            synchronized (tokens) {
                tokens.put(token, username);
                users.put(username, token);
            }
        }
    }
}
