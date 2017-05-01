package de.tinf15b4.kino.data;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

public class ServiceImplModel<E extends EntityModel, R extends JpaRepository<E, Long>> implements ServiceModel<E> {

    @Autowired
    protected R repository;

    @Autowired
    protected EntityManager entityManager;

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
    @Transactional
    public Optional<E> saveWithId(E e) {
        /*
            HACK: We save it and then adjust the id behind hibernate's back using a native SQL statement.
            Please kill me.
         */

        long oldid = e.getId();

        Optional<E> oe = save(e);
        if (oe.isPresent()) {
            e = oe.get();

            long newid = e.getId();

            if (newid != oldid && oldid != 0) {
                String table = e.getClass().getAnnotation(Table.class).name();

                // detach old entity
                entityManager.flush();
                entityManager.detach(e);

                // adjust the id
                entityManager.createNativeQuery("UPDATE " + table + " SET id = " + oldid + " WHERE id = " + newid).executeUpdate();

                // retrieve it again from the database
                e = repository.findOne(oldid);

                oe = Optional.ofNullable(e);
            }
        }

        return oe;
    }

    @Override
    public void delete(E e) {
        repository.delete(e);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public long count() {
        return repository.count();
    }

}
