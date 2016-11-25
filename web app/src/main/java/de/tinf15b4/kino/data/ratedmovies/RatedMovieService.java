package de.tinf15b4.kino.data.ratedmovies;

import java.util.List;

import de.tinf15b4.kino.data.movies.Movie;

public interface RatedMovieService {

    void save(RatedMovie rm);

    List<RatedMovie> findRatingsByMovie(Movie m);

}
