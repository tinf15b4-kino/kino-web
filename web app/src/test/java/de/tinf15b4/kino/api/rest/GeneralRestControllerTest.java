
package de.tinf15b4.kino.api.rest;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieService;
import de.tinf15b4.kino.data.playlists.PlaylistService;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaService;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieService;

public class GeneralRestControllerTest extends AbstractRestControllerTest {

    @InjectMocks
    private GeneralRestController underTest;

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
        when(movieService.findOne(0)).thenReturn(new Movie());
        when(cinemaService.findOne(0)).thenReturn(new Cinema());
    }

    @Test
    public void testGetCallsWithValidIdsAndDates() throws Exception {
        ResponseEntity<?> response = underTest.getPlaylistForCinema(0, 0, 0);
        assertValidResponse(response);
        response = underTest.getPlaylistForMovie(0, 0, 0);
        assertValidResponse(response);
    }

    @Test
    public void testGetCallsWithInvalidIdsAndValidDates() throws Exception {
        ResponseEntity<?> response = underTest.getPlaylistForCinema(-1, 0, 0);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);
        response = underTest.getPlaylistForMovie(10, 0, 0);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);
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
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);
        response = underTest.getRatedCinemas(10);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);
    }

}
