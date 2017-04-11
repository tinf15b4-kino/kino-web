
package de.tinf15b4.kino.api.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Strings;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.favorites.FavoriteService;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieService;
import de.tinf15b4.kino.data.playlists.PlaylistService;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaService;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieService;
import de.tinf15b4.kino.data.users.User;
import de.tinf15b4.kino.data.users.UserService;

public class KinoRestControllerTest {

    @InjectMocks
    private KinoRestController underTest;

    @Mock
    private UserService userService;

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private PlaylistService playlistService;

    @Mock
    private CinemaService cinemaService;

    @Mock
    private MovieService movieService;

    @Mock
    private RatedCinemaService ratedCinemaService;

    @Mock
    private RatedMovieService ratedMovieService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        User user = new User();
        user.setName("Mustermann");
        user.setPassword("muster");
        when(userService.findByName("Mustermann")).thenReturn(user);
        when(movieService.findOne(0)).thenReturn(new Movie());
        when(cinemaService.findOne(0)).thenReturn(new Cinema());
    }

    @Test
    public void testAuthorizationValid() throws Exception {
        ResponseEntity<?> response = underTest.authorize("Mustermann", "muster");
        assertValidResponse(response);
        assertFalse(Strings.isNullOrEmpty((String) response.getBody()));
    }

    @Test
    public void testAuthorizationWrongPassword() throws Exception {
        ResponseEntity<?> response = underTest.authorize("Mustermann", "wrongPassword");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAuthorizationWrongUser() throws Exception {
        ResponseEntity<?> response = underTest.authorize("Musterfrau", "muster");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAuthorizationEmptyUserAndPassword() throws Exception {
        ResponseEntity<?> response = underTest.authorize("", "");
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(KinoRestController.NOT_NULL, response.getBody());
    }

    @Test
    public void testLoginTwice() throws Exception {
        ResponseEntity<?> response1 = underTest.authorize("Mustermann", "muster");
        assertValidResponse(response1);
        ResponseEntity<?> response2 = underTest.authorize("Mustermann", "muster");
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void testLogoutValid() throws Exception {
        String token = (String) underTest.authorize("Mustermann", "muster").getBody();
        ResponseEntity<?> response = underTest.logout(token);
        assertValidResponse(response);
    }

    @Test
    public void testLogoutInvalidToken() throws Exception {
        String token = (String) underTest.authorize("Mustermann", "muster").getBody();
        token = token.substring(1);
        ResponseEntity<?> response = underTest.logout(token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testLogoutTwice() throws Exception {
        String token = (String) underTest.authorize("Mustermann", "muster").getBody();
        underTest.logout(token);
        ResponseEntity<?> response = underTest.logout(token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetCallsWithValidToken() throws Exception {
        String token = (String) underTest.authorize("Mustermann", "muster").getBody();
        ResponseEntity<?> response = underTest.getFavorites(token);
        assertValidResponse(response);

        response = underTest.getUser(token);
        assertValidResponse(response);
    }

    @Test
    public void testGetCallsWithInvalidToken() throws Exception {
        String token = ((String) underTest.authorize("Mustermann", "muster").getBody()).substring(1);
        ResponseEntity<?> response = underTest.getFavorites(token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        response = underTest.getUser(token);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetCallsWithValidIdsAndDates() throws Exception {
        ResponseEntity<?> response = underTest.getPlaylistForCinema(0, "1234-12-12", "1234-12-12");
        assertValidResponse(response);
        response = underTest.getPlaylistForMovie(0, "1234-12-12", "1234-12-12");
        assertValidResponse(response);
    }

    @Test
    public void testGetCallsWithInvalidIdsAndValidDates() throws Exception {
        ResponseEntity<?> response = underTest.getPlaylistForCinema(-1, "1234-12-12", "1234-12-12");
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, KinoRestController.INVALID_ID);
        response = underTest.getPlaylistForMovie(10, "1234-12-12", "1234-12-12");
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, KinoRestController.INVALID_ID);
    }

    @Test
    public void testGetCallsWithValidIdsAndInvalidDates() throws Exception {
        ResponseEntity<?> response = underTest.getPlaylistForCinema(0, "1234-12.12", "1234-12-12");
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, "Unparseable date");
        response = underTest.getPlaylistForCinema(0, "1234-12-12", "1234-12.12");
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, "Unparseable date");

        response = underTest.getPlaylistForMovie(0, "1234-12-12", "1234-12.12");
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, "Unparseable date");
        response = underTest.getPlaylistForMovie(0, "1234-12.12", "1234-12-12");
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, "Unparseable date");
    }

    @Test
    public void testGetCallsWithInvalidIdsAndDates() throws Exception {
        ResponseEntity<?> response = underTest.getPlaylistForCinema(10, "1234-12-12", "1234-12-12");
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, KinoRestController.INVALID_ID);
        response = underTest.getPlaylistForMovie(-1, "1234-12-12", "1234-12-12");
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, KinoRestController.INVALID_ID);
    }

    @Test
    public void testGetCallsWithValidIds() throws Exception {
        ResponseEntity<?> response = underTest.getRatedMovies(0);
        assertValidResponse(response);
        response = underTest.getRatedCinemas(0);
        assertValidResponse(response);
    }

    @Test
    public void testGetCallsWithInvalidIds() throws Exception {
        ResponseEntity<?> response = underTest.getRatedMovies(-1);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, KinoRestController.INVALID_ID);
        response = underTest.getRatedCinemas(10);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, KinoRestController.INVALID_ID);
    }

    private void assertInvalidResponse(ResponseEntity<?> response, HttpStatus expected, String containedError) {
        assertEquals(expected, response.getStatusCode());
        assertTrue(((String) response.getBody()).contains(containedError));
    }

    private void assertValidResponse(ResponseEntity<?> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
