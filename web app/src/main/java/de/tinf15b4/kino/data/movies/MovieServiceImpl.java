package de.tinf15b4.kino.data.movies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public void save(Movie m) {
        movieRepository.save(m);
    }

    @Override
    public Movie findOne(long id) {
        return movieRepository.findOne(id);
    }

    @Override
    public List<Movie> findByFilter(AgeControl ac, Genre genre) {
        return movieRepository.findByFilter(ac, genre);
    }

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> allmightyFilter(MovieFilterData filterData) {
        return movieRepository.allmightyFilter(filterData.getAgeControl(), filterData.getGenre(),
                filterData.getUpperPrice(), filterData.getLowerPrice(), filterData.getUpperTime(),
                filterData.getLowerTime());
    }

}
