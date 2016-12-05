package de.tinf15b4.kino.data.movies;

import java.util.Date;
import java.util.List;

public interface MovieService {

    void save(Movie m);

    Movie findOne(long l);

    List<Movie> findByFilter(AgeControl ac, Genre genre);

    List<Movie> findAll();

    List<Movie> allmightyFilter(AgeControl ac, Genre genre, Integer gPrice, Integer lPrice, Date gTime, Date lTime);

}
