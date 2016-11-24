package de.tinf15b4.kino.data.favorites;

import java.util.List;

import de.tinf15b4.kino.data.users.User;

public interface FavoriteService {

    List<Favorite> getAllFavoritesForUser(User u);

    Favorite findFavorite(long cinemaId, User u);

    boolean isCinemaFavorite(long cinemaId, User u);

    void markFavorite(long cinemaId, User u);

    void unmarkFavorite(long cinemaId, User u);

    void save(Favorite f);

    void delete(Favorite f);

}
