package de.tinf15b4.kino.data.movies;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.ServiceImplModel;

@Service
public class MovieServiceImpl extends ServiceImplModel<Movie, MovieRepository> implements MovieService {

    @Override
    public List<Movie> allmightyFilter(MovieFilterData filterData) {
        Set<AgeControl> ageControl = filterData.getAgeControl();
        Set<Genre> genre = filterData.getGenre();
        return repository.allmightyFilter(ageControl.isEmpty() ? null : ageControl, genre.isEmpty() ? null : genre,
                filterData.getLowerPrice(), filterData.getUpperPrice(), filterData.getLowerTime(),
                filterData.getUpperTime(), filterData.getUpperRating(), filterData.getLowerRating());
    }

}
