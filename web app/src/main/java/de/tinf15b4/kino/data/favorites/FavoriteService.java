package de.tinf15b4.kino.data.favorites;

import java.util.List;

import de.tinf15b4.kino.data.users.User;

public interface FavoriteService {

    List<Favorite> getAllFavoritesForUser(User u);

    Favorite getFavorite(long cinemaId);

    boolean isCinemaFavorite(long cinemaId);

    void markFavorite(long cinemaId, User u);

    void unmarkFavorite(long cinemaId);

    void save(Favorite f);

    List<Favorite> findByCinemaId(long id);

    void delete(List<Favorite> f);

}
