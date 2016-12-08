package de.tinf15b4.kino.data.ratedmovies;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.tinf15b4.kino.data.movies.Movie;
import de.tinf15b4.kino.data.users.User;

public interface RatedMovieRepository extends JpaRepository<RatedMovie, Long> {
    @Query("SELECT rm FROM RatedMovie rm WHERE rm.id.movie = :movie ORDER BY rm.time ASC")
    List<RatedMovie> findRatingsByMovie(@Param("movie") Movie movie);

    @Query("SELECT rm FROM RatedMovie rm WHERE rm.id.user = :user ORDER BY rm.time ASC")
    List<RatedMovie> findRatingsByUser(@Param("user") User user);

    @Query("SELECT avg(rm.rating) FROM RatedMovie rm WHERE rm.id.movie = :movie GROUP BY rm.id.movie")
    List<Double> getAverageRatingForMovie(@Param("movie") Movie movie);
}