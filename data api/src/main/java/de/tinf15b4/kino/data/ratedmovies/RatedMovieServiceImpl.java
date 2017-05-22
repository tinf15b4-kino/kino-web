package de.tinf15b4.kino.data.ratedmovies;

import java.util.List;

import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.ServiceImplModel;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.ratedmovies.RatedMovie;

@Service
public class RatedMovieServiceImpl extends ServiceImplModel<RatedMovie, RatedMovieRepository>
        implements RatedMovieService {

    @Override
    public List<RatedMovie> findRatingsByMovie(Movie m) {
        return repository.findRatingsByMovie(m);
    }

    @Override
    public double getAverageRatingForMovie(Movie m) {
        List<Double> ratings = repository.getAverageRatingForMovie(m);

        if (ratings.size() == 0)
            return 0.0;
        else
            return ratings.get(0);
    }

}
