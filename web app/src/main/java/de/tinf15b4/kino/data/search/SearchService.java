package de.tinf15b4.kino.data.search;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<Movie> movies = movieRepository.findByNameLike(searchTerm);
        List<Cinema> cinemas = cinemaRepository.findNameLike(searchTerm);
        List<Movie> movieDescriptions = movieRepository.findByDescriptionLike(searchTerm);
        List<Cinema> cinemaAdresses = cinemaRepository.findByAddressLike(searchTerm);

        List<Movie> resultMovies = new ArrayList<>(movies.size() + movieDescriptions.size());
        for (Movie m: movies)
            resultMovies.add(m);
        for (Movie m: movieDescriptions)
            resultMovies.add(m);

        List<Cinema> resultCinemas = new ArrayList<>();
        for (Cinema c : cinemas)
            resultCinemas.add(c);
        for (Cinema c : cinemaAdresses)
            resultCinemas.add(c);

        return new SearchResult(resultCinemas, resultMovies);
    }
}
