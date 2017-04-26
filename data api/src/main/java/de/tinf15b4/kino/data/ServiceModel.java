package de.tinf15b4.kino.data;

import java.util.List;
import java.util.Optional;

import de.tinf15b4.kino.data.EntityModel;

public interface ServiceModel<E extends EntityModel> {

    E findOne(long id);

    List<E> findAll();

    Optional<E> save(E e);

    void delete(E e);

    long count();
}
