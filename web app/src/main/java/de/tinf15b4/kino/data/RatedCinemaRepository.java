package de.tinf15b4.kino.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatedCinemaRepository extends JpaRepository<RatedCinema, Long> {
    @Query("SELECT rc FROM RatedCinema rc WHERE rc.id.cinema = :cinema ORDER BY rc.time ASC")
    List<RatedCinema> findRatings(@Param("cinema") Cinema cinema);
}
