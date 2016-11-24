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
        return favoriteRepository.findByUserId(u.getId());
    }

    @Override
    public Favorite findFavorite(long cinemaId, User u) {
        return favoriteRepository.findFavorite(cinemaId, u.getId());
    }

    @Override
    public boolean isCinemaFavorite(long cinemaId, User u) {
        if (u == null)
            return false;
        return findFavorite(cinemaId, u) != null;
    }

    @Override
    public void markFavorite(long cinemaId, User u) {
        if (!isCinemaFavorite(cinemaId, u)) {
            Favorite f = new Favorite(u, cinemaRepository.findOne(cinemaId));
            favoriteRepository.save(f);
        }
    }

    @Override
    public void unmarkFavorite(long cinemaId, User u) {
        if (isCinemaFavorite(cinemaId, u)) {
            Favorite fav = findFavorite(cinemaId, u);
            favoriteRepository.delete(fav);
        }
    }

    @Override
    public void save(Favorite f) {
        favoriteRepository.save(f);
    }

    @Override
    public void delete(Favorite f) {
        favoriteRepository.delete(f);
    }

}
