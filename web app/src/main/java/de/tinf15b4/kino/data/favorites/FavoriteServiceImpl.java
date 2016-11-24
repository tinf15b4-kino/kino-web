package de.tinf15b4.kino.data.favorites;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.cinemas.CinemaRepository;
import de.tinf15b4.kino.data.users.User;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Override
    public List<Favorite> getAllFavoritesForUser(User u) {
        return favoriteRepository.findAll(); // FIXME what about the current
                                             // user?
    }

    @Override
    public Favorite getFavorite(long cinemaId) {
        return favoriteRepository.findByCinemaId(cinemaId).get(0);
    }

    @Override
    public boolean isCinemaFavorite(long cinemaId) {
        List<Favorite> faves = favoriteRepository.findByCinemaId(cinemaId);
        return !faves.isEmpty();
    }

    @Override
    public void markFavorite(long cinemaId, User u) {
        if (!isCinemaFavorite(cinemaId)) {
            Favorite f = new Favorite(u, cinemaRepository.findOne(cinemaId));
            favoriteRepository.save(f);
        }
    }

    @Override
    public void unmarkFavorite(long cinemaId) {
        List<Favorite> faves = favoriteRepository.findByCinemaId(cinemaId);
        faves.forEach(fav -> favoriteRepository.delete(fav));
    }

    @Override
    public void save(Favorite f) {
        favoriteRepository.save(f);
    }

    @Override
    public List<Favorite> findByCinemaId(long id) {
        return favoriteRepository.findByCinemaId(id);
    }

    @Override
    public void delete(List<Favorite> f) {
        favoriteRepository.delete(f);
    }

}
