package de.tinf15b4.kino.data;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import de.tinf15b4.kino.data.EntityModel;

public class ServiceImplModel<E extends EntityModel, R extends JpaRepository<E, Long>> implements ServiceModel<E> {

    @Autowired
    protected R repository;

    @Override
    public E findOne(long id) {
        return repository.findOne(id);
    }

    @Override
    public List<E> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<E> save(E e) {

        try {
            return Optional.ofNullable(repository.save(e));

        } catch (DataIntegrityViolationException ex) {
            // ex.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void delete(E e) {
        repository.delete(e);
    }

    @Override
    public long count() {
        return repository.count();
    }

}
