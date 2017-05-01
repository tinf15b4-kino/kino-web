package de.tinf15b4.kino.data;

import java.util.List;
import java.util.Optional;

public interface ServiceModel<E extends EntityModel> {

    E findOne(long id);

    List<E> findAll();

    Optional<E> save(E e);

    void delete(E e);

    void deleteAll();

    long count();
}
