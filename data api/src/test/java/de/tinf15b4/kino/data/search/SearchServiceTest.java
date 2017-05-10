package de.tinf15b4.kino.data.search;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Random;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.javafaker.Faker;
import com.google.common.collect.Lists;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaRepository;
import de.tinf15b4.kino.data.movies.AgeControl;
import de.tinf15b4.kino.data.movies.Genre;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieRepository;
import de.tinf15b4.kino.data.search.SearchResult;
import de.tinf15b4.kino.data.search.SearchService;

public class SearchServiceTest {
    @InjectMocks
    private SearchService searchService;

    @Mock
    private MovieRepository movieRepo;

    @Mock
    private CinemaRepository cineRepo;

    @Test
    public void checkBug195() {
        MockitoAnnotations.initMocks(this);

        // Bug: "Movies / Cinemas appear twice in search Results"
        // Happens when the search term is found in both description and title

        // Setup: Mock movie and cinema repo with exactly one Cinema and Movie
        String movieName = "Guaranteed Unique Movie";
        String cinemaName = "Guaranteed Unique Cinema";

        Faker faker = new Faker(new Locale("de"));
        Random rnd = new Random();

        Movie m = new Movie();
        m.setId(1);
        m.setName(movieName);
        m.setDescription(movieName);
        m.setLengthMinutes(faker.number().numberBetween(20, 240));
        m.setGenre(Genre.values()[rnd.nextInt(Genre.values().length)]);
        m.setAgeControl(AgeControl.values()[rnd.nextInt(AgeControl.values().length)]);

        Cinema c = new Cinema();
        c.setId(1);
        c.setName(cinemaName);
        c.setStreet(cinemaName + " Street");
        c.setHnr("1");
        c.setPostcode("11111");
        c.setCity(cinemaName + " City");
        c.setCountry(cinemaName + " Country");

        when(movieRepo.findByNameLike(movieName)).thenReturn(Lists.newArrayList(m));
        when(movieRepo.findByDescriptionLike(movieName)).thenReturn(Lists.newArrayList(m));
        when(cineRepo.findNameLike(cinemaName)).thenReturn(Lists.newArrayList(c));
        when(cineRepo.findByAddressLike(cinemaName)).thenReturn(Lists.newArrayList(c));

        // now search for the movie. There should be only one result
        SearchResult movieResult = searchService.search(movieName);
        assertEquals(1, movieResult.getMovies().size());

        // and the cinema
        SearchResult cineResult = searchService.search(cinemaName);
        assertEquals(1, cineResult.getCinemas().size());
    }
}
