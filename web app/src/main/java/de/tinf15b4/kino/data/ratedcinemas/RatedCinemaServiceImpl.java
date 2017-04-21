package de.tinf15b4.kino.data.ratedcinemas;

import java.util.List;

import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.ServiceImplModel;
import de.tinf15b4.kino.data.cinemas.Cinema;

@Service
public class RatedCinemaServiceImpl extends ServiceImplModel<RatedCinema, RatedCinemaRepository>
        implements RatedCinemaService {

    @Override
    public List<RatedCinema> findRatingsByCinema(Cinema c) {
        return repository.findRatingsByCinema(c);
    }
}
