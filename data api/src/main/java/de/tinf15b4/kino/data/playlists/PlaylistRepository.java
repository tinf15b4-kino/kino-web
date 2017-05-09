package de.tinf15b4.kino.data.playlists;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import de.tinf15b4.kino.data.cinemas.Cinema;
import de.tinf15b4.kino.data.movies.Movie;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    //@formatter:off
    
    @Query("SELECT p "
            + "FROM Playlist p "
            + "WHERE p.cinema = :cinema "
            + "AND (:from is null OR p.time > :from) "
            + "AND (:to is null OR p.time < :to) "
            + "ORDER BY p.time ASC")
    List<Playlist> findForCinema(@Param("cinema") Cinema cinema, @Param("from") Date from, @Param("to") Date to);

    @Query("SELECT p "
            + "FROM Playlist p "
            + "WHERE p.movie = :movie "
            + "AND (:from is null OR p.time > :from) "
            + "AND (:to is null OR p.time < :to) "
            + "ORDER BY p.time ASC")
    List<Playlist> findForMovie(@Param("movie") Movie movie, @Param("from") Date from, @Param("to") Date to);

    @Modifying
    @Transactional
    @Query("DELETE FROM Playlist WHERE time > CURRENT_DATE AND cinema = :cinema")
    void deleteFutureForCinema(@Param("cinema") Cinema cinema);
    
    //@formatter:on
}
