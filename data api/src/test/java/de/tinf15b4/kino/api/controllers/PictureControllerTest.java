package de.tinf15b4.kino.api.controllers;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaService;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieService;

public class PictureControllerTest {

    private static final byte[] DEFAULT_MOVIE_IMAGE = new byte[] { 1, 0, 0, 1 };
    private static final byte[] DEFAULT_CINEMA_IMAGE = new byte[] { 0, 1, 1, 0 };

    @Mock
    private CinemaService cinemaService;

    @Mock
    private MovieService movieService;

    @Spy
    @InjectMocks
    private PictureController underTest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(underTest.loadDefaultCinemaImage()).thenReturn(DEFAULT_CINEMA_IMAGE);
        Mockito.when(underTest.loadDefaultMovieCover()).thenReturn(DEFAULT_MOVIE_IMAGE);
    }

    @Test
    public void testGetCinemaPicture() throws Exception {
        Cinema cinema = new Cinema();
        Mockito.when(cinemaService.findOne(0)).thenReturn(cinema);

        byte[] cinemaPicture = underTest.getCinemaPicture(0);
        assertArrayEquals(DEFAULT_CINEMA_IMAGE, cinemaPicture);

        byte[] image = new byte[] { 1, 0 };
        cinema.setImage(image);

        cinemaPicture = underTest.getCinemaPicture(0);
        assertArrayEquals(image, cinemaPicture);

        // test caching.
        Mockito.when(underTest.loadDefaultCinemaImage()).thenReturn(null);
        cinemaPicture = underTest.getCinemaPicture(10);
        assertArrayEquals(DEFAULT_CINEMA_IMAGE, cinemaPicture);

        cinemaPicture = underTest.getCinemaPicture(9);
        assertArrayEquals(DEFAULT_CINEMA_IMAGE, cinemaPicture);

        Mockito.verify(underTest).loadDefaultCinemaImage();
    }

    @Test
    public void testGetMoviePicture() throws Exception {
        Movie movie = new Movie();
        Mockito.when(movieService.findOne(0)).thenReturn(movie);

        byte[] moviePicture = underTest.getMoviePicture(0);
        assertArrayEquals(DEFAULT_MOVIE_IMAGE, moviePicture);

        byte[] image = new byte[] { 0, 1 };
        movie.setCover(image);

        moviePicture = underTest.getMoviePicture(0);
        assertArrayEquals(image, moviePicture);

        // test caching.
        Mockito.when(underTest.loadDefaultMovieCover()).thenReturn(null);
        moviePicture = underTest.getMoviePicture(10);
        assertArrayEquals(DEFAULT_MOVIE_IMAGE, moviePicture);

        moviePicture = underTest.getMoviePicture(9);
        assertArrayEquals(DEFAULT_MOVIE_IMAGE, moviePicture);

        Mockito.verify(underTest).loadDefaultMovieCover();
    }

}
