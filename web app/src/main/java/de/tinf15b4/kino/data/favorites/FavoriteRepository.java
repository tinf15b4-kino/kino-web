package de.tinf15b4.kino.data.favorites;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.users.User;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // List<Favorite> findByIdUser(User user);

    @Query("SELECT f FROM Favorite f WHERE f.id.user = :user ORDER BY f.id.cinema.name")
    List<Favorite> findFavoritesByUser(@Param("user") User user);

    @Query("SELECT f FROM Favorite f WHERE f.id.user = :user and f.id.cinema = :cinema")
    Favorite findFavorite(@Param("cinema") Cinema cinema, @Param("user") User user);
}
