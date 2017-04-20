package de.tinf15b4.kino.data.cinemas;

import java.util.List;

import de.tinf15b4.kino.data.ServiceModel;

public interface CinemaService extends ServiceModel<Cinema> {

    Cinema findOne(long id);

    List<Cinema> findAll();

}
