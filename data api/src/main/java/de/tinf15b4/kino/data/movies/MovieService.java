package de.tinf15b4.kino.data.movies;

import java.util.List;

import de.tinf15b4.kino.data.ServiceModel;
import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.movies.MovieFilterData;

public interface MovieService extends ServiceModel<Movie> {

    List<Movie> allmightyFilter(MovieFilterData filterData);

}
