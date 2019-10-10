package jm.dao;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
public abstract class AbstractDao<T> {

    @PersistenceContext
    private EntityManager em;

    private Class persistentClass;

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        persistentClass = (Class) ((java.lang.reflect.ParameterizedType)
                this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public List getAllEntities() {
        return em.createQuery("from " + persistentClass.getName()).getResultList();
    }

    public void persistEntity(T t) {
        em.persist(t);
    }

    public T findEntityById(Long id) {
        return (T) em.find(persistentClass, id);
    }

    public T mergeEntity(T t) {
        return (T) em.merge(t);
    }

    public void deleteEntity(T t) {
        em.remove(t);
    }

    public void deleteEntityById(Long id) {
        em.remove(findEntityById(id));
    }

}
