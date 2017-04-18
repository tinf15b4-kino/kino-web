package de.tinf15b4.kino.data.favorites;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.users.User;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Override
    public List<Favorite> getAllFavoritesForUser(User u) {
        return favoriteRepository.findFavoritesByUser(u);
    }

    @Override
    public Favorite findFavorite(User u, Cinema c) {
        return favoriteRepository.findFavorite(c, u);
    }

    @Override
    public boolean isCinemaFavorite(User u, Cinema c) {
        if (u == null)
            return false;
        return findFavorite(u, c) != null;
    }

    @Override
    public void markFavorite(User u, Cinema c) {
        if (!isCinemaFavorite(u, c)) {
            Favorite f = new Favorite(u, c);
            favoriteRepository.save(f);
        }
    }

    @Override
    public void unmarkFavorite(User u, Cinema c) {
        if (isCinemaFavorite(u, c)) {
            Favorite fav = findFavorite(u, c);
            favoriteRepository.delete(fav);
        }
    }

    @Override
    public Favorite save(Favorite f) {
        return favoriteRepository.save(f);
    }

    @Override
    public void delete(Favorite f) {
        favoriteRepository.delete(f);
    }

}
