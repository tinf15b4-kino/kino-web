package de.tinf15b4.kino.data.favorites;

import java.util.List;

import de.tinf15b4.kino.data.ServiceModel;
import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.favorites.Favorite;
import de.tinf15b4.kino.data.users.User;

public interface FavoriteService extends ServiceModel<Favorite> {

    List<Favorite> getAllFavoritesForUser(User u);

    Favorite findFavorite(User u, Cinema c);

    boolean isCinemaFavorite(User u, Cinema c);

    void markFavorite(User u, Cinema c);

    void unmarkFavorite(User u, Cinema c);

}
