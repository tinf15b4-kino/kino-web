package de.tinf15b4.kino.data.ratedmovies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.movies.Movie;

@Service
public class RatedMovieServiceImpl implements RatedMovieService {

    @Autowired
    private RatedMovieRepository ratedMovieRepository;

    @Override
    public void save(RatedMovie rm) {
        ratedMovieRepository.save(rm);
    }

    @Override
    public List<RatedMovie> findRatingsByMovie(Movie m) {
        return ratedMovieRepository.findRatingsByMovie(m);
    }

}