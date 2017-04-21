package de.tinf15b4.kino.data.favorites;

import java.util.List;

import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.ServiceImplModel;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.users.User;

@Service
public class FavoriteServiceImpl extends ServiceImplModel<Favorite, FavoriteRepository> implements FavoriteService {

    @Override
    public List<Favorite> getAllFavoritesForUser(User u) {
        return repository.findFavoritesByUser(u);
    }

    @Override
    public Favorite findFavorite(User u, Cinema c) {
        return repository.findFavorite(c, u);
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
            repository.save(f);
        }
    }

    @Override
    public void unmarkFavorite(User u, Cinema c) {
        if (isCinemaFavorite(u, c)) {
            Favorite fav = findFavorite(u, c);
            repository.delete(fav);
        }
    }
}
