package de.tinf15b4.kino.data.movies;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    //@formatter:off
    
    @Query("select m from Movie m where lower(m.name) like concat('%', lower(:name), '%')")
    List<Movie> findByNameLike(@Param("name") String name);

    @Query("select m from Movie m where lower(m.description) like concat('%', lower(:d), '%')")
    List<Movie> findByDescriptionLike(@Param("d") String desc);

    @Query("SELECT m FROM Movie m WHERE (:ac is null OR m.ageControl = :ac) "
            + "AND (:genre is null OR m.genre = :genre) ORDER BY m.name")
    List<Movie> findByFilter(@Param("ac") AgeControl ac, @Param("genre") Genre genre);
    
    @Query("SELECT m " +
            "FROM Movie m, Playlist p, RatedMovie rm " + 
            "WHERE m = p.id.movie " + //"AND m = rm.id.movie "+
            "AND ((:ac) is null OR m.ageControl IN (:ac)) "+
            "AND ((:genre) is null OR m.genre IN (:genre)) "+
//            "AND (:gRating is null OR rm.rating >= :gRating) "+
//            "AND (:lRating is null OR rm.rating <= :lRating) "+
            "AND (:gPrice is null OR p.price >= :gPrice) "+
            "AND (:lPrice is null OR p.price <= :lPrice) "+
            "AND (:gTime is null OR p.time >= :gTime) "+
            "AND (:lTime is null OR p.time <= :lTime) "+ 
            "GROUP BY m")
    List<Movie> allmightyFilter(
            @Param("ac") Set<AgeControl> ac,
            @Param("genre") Set<Genre> genre,
//            @Param("gRating") Double gRating, // greater than this value
//            @Param("lRating") Double lRating, // less than...
            @Param("gPrice") Integer gPrice, // price in cents, greater than...
            @Param("lPrice") Integer lPrice, // price in cents, less than...
            @Param("gTime") Date gTime, // greater than...
            @Param("lTime") Date lTime // less than..
            );

    //@formatter:on
}
