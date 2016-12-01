package de.tinf15b4.kino.data.ratedcinemas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.cinemas.Cinema;

@Service
public class RatedCinemaServiceImpl implements RatedCinemaService {

    @Autowired
    private RatedCinemaRepository ratedCinemaRepository;

    @Override
    public List<RatedCinema> findRatingsByCinema(Cinema c) {
        return ratedCinemaRepository.findRatingsByCinema(c);
    }

    @Override
    public void save(RatedCinema rc) {
        ratedCinemaRepository.save(rc);
    }

}
