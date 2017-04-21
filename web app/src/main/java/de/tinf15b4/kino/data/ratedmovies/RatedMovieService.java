package de.tinf15b4.kino.data.ratedmovies;

import java.util.List;

import de.tinf15b4.kino.data.ServiceModel;
import de.tinf15b4.kino.data.movies.Movie;

public interface RatedMovieService extends ServiceModel<RatedMovie> {

    List<RatedMovie> findRatingsByMovie(Movie m);

    double getAverageRatingForMovie(Movie m);

}
