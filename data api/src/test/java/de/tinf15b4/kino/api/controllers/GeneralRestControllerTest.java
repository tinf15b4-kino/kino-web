
package de.tinf15b4.kino.api.controllers;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.Lists;

import de.tinf15b4.kino.api.utils.RestControllerConstants;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieFilterData;
import de.tinf15b4.kino.data.movies.MovieService;
import de.tinf15b4.kino.data.playlists.PlaylistService;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinemaService;
import de.tinf15b4.kino.data.ratedmovies.RatedMovieService;
import de.tinf15b4.kino.data.search.SearchResult;
import de.tinf15b4.kino.data.search.SearchService;

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

    @Mock
    private SearchService searchService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(movieService.findOne(0)).thenReturn(new Movie());
        when(cinemaService.findOne(0)).thenReturn(new Cinema());
    }

    @Test
    public void testGetCallsWithValidIdsAndDates() throws Exception {
        ResponseEntity<?> response = underTest.getPlaylistForCinema(0L, 0L, 0L);
        assertValidResponse(response);
        response = underTest.getPlaylistForMovie(0L, 0L, 0L);
        assertValidResponse(response);
    }

    @Test
    public void testGetCallsWithValidIdsAndUnsetDates() throws Exception {
        ResponseEntity<?> response = underTest.getPlaylistForCinema(0L, null, null);
        assertValidResponse(response);
        response = underTest.getPlaylistForMovie(0L, null, null);
        assertValidResponse(response);
    }

    @Test
    public void testGetCallsWithInvalidIdsAndValidDates() throws Exception {
        ResponseEntity<?> response = underTest.getPlaylistForCinema(-1L, 0L, 0L);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);
        response = underTest.getPlaylistForMovie(10L, 0L, 0L);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);
    }

    @Test
    public void testGetCallsWithInvalidIdsAndUnsetDates() throws Exception {
        ResponseEntity<?> response = underTest.getPlaylistForCinema(-1L, null, null);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);
        response = underTest.getPlaylistForMovie(10L, null, null);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);
    }

    @Test
    public void testGetCallsWithValidIds() throws Exception {
        ResponseEntity<?> response = underTest.getRatedMovies(0);
        assertValidResponse(response);
        response = underTest.getRatedCinemas(0);
        assertValidResponse(response);

        response = underTest.getCinemas();
        assertValidResponse(response);
        response = underTest.getCinema(0);
        assertValidResponse(response);

        response = underTest.getMovies();
        assertValidResponse(response);
        response = underTest.getMovie(0);
        assertValidResponse(response);
        response = underTest.getAverageRatingForMovie(0);
        assertValidResponse(response);

        Mockito.when(searchService.search(Mockito.anyString()))
                .thenReturn(new SearchResult(Lists.newArrayList(), Lists.newArrayList()));
        response = underTest.getSearchResult("DoesntMatter");
        assertValidResponse(response);

        response = underTest.getFilteredMovies(new MovieFilterData());
        assertValidResponse(response);
    }

    @Test
    public void testGetCallsWithInvalidIds() throws Exception {
        ResponseEntity<?> response = underTest.getRatedMovies(-1);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);
        response = underTest.getRatedCinemas(10);

        response = underTest.getCinema(10);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);

        response = underTest.getMovie(10);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);
        response = underTest.getAverageRatingForMovie(10);
        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);

        assertInvalidResponse(response, HttpStatus.BAD_REQUEST, RestControllerConstants.INVALID_ID);
    }

}
