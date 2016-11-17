package de.tinf15b4.kino.web.controllers;

import de.tinf15b4.kino.data.Favorite;

import java.util.List;

public interface FavoriteListController {
    List<Favorite> getAllFavesForCurrentUser();

    Favorite getFave(long cinemaId);

    boolean isCinemaFavorite(long cinemaId);
    void markFavorite(long cinemaId);
    void unmarkFavorite(long cinemaId);
}
