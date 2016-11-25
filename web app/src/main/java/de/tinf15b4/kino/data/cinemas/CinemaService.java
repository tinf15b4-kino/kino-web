package de.tinf15b4.kino.data.cinemas;

import java.util.List;

public interface CinemaService {

    Cinema findOne(long id);

    List<Cinema> findAll();

    void save(Cinema c);

}
