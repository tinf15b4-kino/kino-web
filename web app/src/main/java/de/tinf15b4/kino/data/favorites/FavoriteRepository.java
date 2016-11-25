package de.tinf15b4.kino.data.favorites;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(long id);

    @Query("SELECT f FROM Favorite f WHERE f.user.id = :user and f.cinema.id = :cinema")
    Favorite findFavorite(@Param("cinema") long cinema, @Param("user") long user);
}
