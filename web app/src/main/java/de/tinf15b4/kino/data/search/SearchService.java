package de.tinf15b4.kino.data.search;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.cinemas.CinemaRepository;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieRepository;

@Service
public class SearchService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    public SearchResult search(String searchTerm) {
        Set<Cinema> cineSet = new HashSet<>();
        Set<Movie> movieSet = new HashSet<>();

        movieSet.addAll(movieRepository.findByNameLike(searchTerm));
        cineSet.addAll(cinemaRepository.findNameLike(searchTerm));
        movieSet.addAll(movieRepository.findByDescriptionLike(searchTerm));
        cineSet.addAll(cinemaRepository.findByAddressLike(searchTerm));

        return new SearchResult(Lists.newArrayList(cineSet), Lists.newArrayList(movieSet));
    }
}
