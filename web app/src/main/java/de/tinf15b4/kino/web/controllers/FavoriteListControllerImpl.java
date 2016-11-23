package de.tinf15b4.kino.web.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.tinf15b4.kino.data.CinemaRepository;
import de.tinf15b4.kino.data.Favorite;
import de.tinf15b4.kino.data.FavoriteRepository;
import de.tinf15b4.kino.data.users.UserLoginBean;

@Service
/*
 * FIXME: This is not really a controller, more like a random collection of
 * service functions that are used by the favorite list view
 */
public class FavoriteListControllerImpl implements FavoriteListController {
    @Autowired
    private FavoriteRepository faveRepo;

    @Autowired
    private CinemaRepository cineRepo;

    @Autowired
    private UserLoginBean userBean;

    @Override
    @Transactional
    public List<Favorite> getAllFavesForCurrentUser() {
        return faveRepo.findAll(); // FIXME what about the current user?
    }

    @Override
    @Transactional
    public Favorite getFave(long cinemaId) {
        return faveRepo.findByCinemaId(cinemaId).get(0);
    }

    @Override
    @Transactional
    public boolean isCinemaFavorite(long cinemaId) {
        List<Favorite> faves = faveRepo.findByCinemaId(cinemaId);
        return !faves.isEmpty();
    }

    @Override
    @Transactional
    public void markFavorite(long cinemaId) {
        if (!isCinemaFavorite(cinemaId)) {
            Favorite f = new Favorite(userBean.getCurrentUser(), cineRepo.findOne(cinemaId));
            faveRepo.save(f);
        }
    }

    @Override
    @Transactional
    public void unmarkFavorite(long cinemaId) {
        List<Favorite> faves = faveRepo.findByCinemaId(cinemaId);
        faves.forEach(fav -> faveRepo.delete(fav));
    }
}
