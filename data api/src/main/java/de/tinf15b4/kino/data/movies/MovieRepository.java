package de.tinf15b4.kino.data.movies;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.tinf15b4.kino.data.movies.AgeControl;
import de.tinf15b4.kino.data.movies.Genre;
import de.tinf15b4.kino.data.movies.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    //@formatter:off
    
    @Query("select m from Movie m where lower(m.name) like concat('%', lower(:name), '%')")
    List<Movie> findByNameLike(@Param("name") String name);

    @Query("select m from Movie m where lower(m.description) like concat('%', lower(:d), '%')")
    List<Movie> findByDescriptionLike(@Param("d") String desc);
    
    @Query("SELECT m " +
            "FROM RatedMovie rm RIGHT JOIN rm.id.movie m, Playlist p " + 
            "WHERE m = p.id.movie " + 
            "AND ((:ac) is null OR m.ageControl IN (:ac)) " +
            "AND ((:genre) is null OR m.genre IN (:genre)) " +
            "AND (:upperPrice is null OR p.price >= :upperPrice) " + 
            "AND (:lowerPrice is null OR p.price <= :lowerPrice) " +
            "AND (:upperTime is null OR p.time >= :upperTime) " +
            "AND (:lowerTime is null OR p.time <= :lowerTime) " + 
            "GROUP BY m " + 
            "HAVING (:upperRating is null OR (AVG(rm.rating) IS NOT null AND AVG(rm.rating) >= :upperRating)) " +
            "AND (:lowerRating is null OR (AVG(rm.rating) IS NOT null AND AVG(rm.rating) <= :lowerRating))")
    List<Movie> allmightyFilter(
            @Param("ac") Set<AgeControl> ac,
            @Param("genre") Set<Genre> genre,
            @Param("upperPrice") Integer upperPrice, // price in cents, greater than...
            @Param("lowerPrice") Integer lowerPrice, // price in cents, less than...
            @Param("upperTime") Date upperTime, // greater than tahn this value
            @Param("lowerTime") Date lowerTime, // less than..
            @Param("upperRating") Double upperRating, // greater than...
            @Param("lowerRating") Double lowerRating // less than...
            );

    //@formatter:on
}
