package de.tinf15b4.kino.data.ratedcinemas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.users.User;

public interface RatedCinemaRepository extends JpaRepository<RatedCinema, Long> {
    @Query("SELECT rc FROM RatedCinema rc WHERE rc.cinema = :cinema ORDER BY rc.time ASC")
    List<RatedCinema> findRatingsByCinema(@Param("cinema") Cinema cinema);

    @Query("SELECT rc FROM RatedCinema rc WHERE rc.user = :user ORDER BY rc.time ASC")
    List<RatedCinema> findRatingsByUser(@Param("user") User user);

    @Query("SELECT rc FROM RatedCinema rc WHERE rc.user = :user AND rc.cinema = :cinema ORDER BY rc.time ASC")
    RatedCinema findRatingByCinemaAndUser(@Param("user") User user, @Param("cinema") Cinema cinema);
}
