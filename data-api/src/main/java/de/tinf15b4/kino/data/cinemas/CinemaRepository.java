package de.tinf15b4.kino.data.cinemas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.tinf15b4.kino.data.cinemas.Cinema;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    @Query("select c from Cinema c where lower(c.name) like concat('%', lower(:name), '%')")
    List<Cinema> findNameLike(@Param("name") String name);

    @Query("select c from Cinema c where lower(c.street) like concat('%', lower(:q), '%')" +
        "or lower(c.city) like concat('%', lower(:q), '%')")
    List<Cinema> findByAddressLike(@Param("q") String q);
}
