package de.tinf15b4.kino.data.ratedcinemas;

import java.util.List;

import de.tinf15b4.kino.data.cinemas.Cinema;

public interface RatedCinemaService {

    List<RatedCinema> findRatingsByCinema(Cinema c);

    void save(RatedCinema rc);

}
