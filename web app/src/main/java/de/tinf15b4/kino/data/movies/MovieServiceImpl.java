package de.tinf15b4.kino.data.movies;

import java.util.List;
import java.util.Set;

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
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> allmightyFilter(MovieFilterData filterData) {
        Set<AgeControl> ageControl = filterData.getAgeControl();
        Set<Genre> genre = filterData.getGenre();
        return movieRepository.allmightyFilter(ageControl.isEmpty() ? null : ageControl, genre.isEmpty() ? null : genre,
                filterData.getLowerPrice(), filterData.getUpperPrice(), filterData.getLowerTime(),
                filterData.getUpperTime(), filterData.getUpperRating(), filterData.getLowerRating());
    }

}
