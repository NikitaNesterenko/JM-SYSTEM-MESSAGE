package jm.api.dao;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

@Transactional
public abstract class AbstractDao<T> implements IGenericDao<T> {
    private Class<T> clazz;

    @PersistenceContext
    private EntityManager em;

    public void setClazz(Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    public List getAllEntities() {
        return em.createQuery("from " + clazz.getName()).getResultList();
    }

    public void persistEntity(T t) {
        em.persist(t);
    }

    public T findEntityById(int id) {
        return (T) em.find(clazz, id);
    }

    public T mergeEntity(T t) {
        return (T) em.merge(t);
    }

    public void deleteEntity(T t) {
        em.remove(t);
    }

    public void deleteEntityById(int id) {
        em.remove(findEntityById(id));
    }
}
