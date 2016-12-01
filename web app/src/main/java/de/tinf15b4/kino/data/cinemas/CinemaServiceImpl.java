package de.tinf15b4.kino.data.cinemas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CinemaServiceImpl implements CinemaService {

    @Autowired
    private CinemaRepository cinemaRepository;
    
    @Override
    public Cinema findOne(long id) {
        return cinemaRepository.findOne(id);
    }

    @Override
    public List<Cinema> findAll() {
        return cinemaRepository.findAll();
    }

    @Override
    public void save(Cinema c) {
        cinemaRepository.save(c);
    }

}
