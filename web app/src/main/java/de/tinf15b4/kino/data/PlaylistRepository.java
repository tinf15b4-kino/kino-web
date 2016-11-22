package de.tinf15b4.kino.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    @Query("SELECT p FROM Playlist p WHERE p.cinema = :cinema AND p.time > :from AND p.time < :to ORDER BY p.time ASC")
    List<Playlist> findForCinema(@Param("cinema") Cinema cinema,
                                 @Param("from")   Date from,
                                 @Param("to")     Date to);

    @Query("SELECT p FROM Playlist p WHERE p.movie = :movie AND p.time > :from AND p.time < :to ORDER BY p.time ASC")
    List<Playlist> findForMovie(@Param("movie") Movie movie,
                                @Param("from")  Date from,
                                @Param("to")    Date to);
}
