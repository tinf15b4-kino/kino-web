package de.tinf15b4.kino.data.movies;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("select m from Movie m where lower(m.name) like concat('%', lower(:name), '%')")
    List<Movie> findByNameLike(@Param("name") String name);

    @Query("select m from Movie m where lower(m.description) like concat('%', lower(:d), '%')")
    List<Movie> findByDescriptionLike(@Param("d") String desc);

    @Query("SELECT m FROM Movie m WHERE (:ac is null OR m.ageControl = :ac) "
            + "AND (:genre is null OR m.genre = :genre) ORDER BY m.name")
    List<Movie> findByFilter(@Param("ac") AgeControl ac, @Param("genre") Genre genre);
}
