package de.tinf15b4.kino.data.ratedcinemas;

import java.util.List;

import de.tinf15b4.kino.data.ServiceModel;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.ratedcinemas.RatedCinema;

public interface RatedCinemaService extends ServiceModel<RatedCinema> {

    List<RatedCinema> findRatingsByCinema(Cinema c);

}