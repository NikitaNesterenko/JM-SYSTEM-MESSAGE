package jm.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public abstract class AbstractDao<T> {

    @PersistenceContext
    EntityManager entityManager;

    private Class persistentClass;

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        persistentClass = (Class) ((java.lang.reflect.ParameterizedType)
                this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public List<T> getAll() {
        return entityManager.createQuery("from " + persistentClass.getName()).getResultList();
    }

    public void persist(T t) {
        entityManager.persist(t);
    }

    public T getById(Long id) {
        return (T) entityManager.find(persistentClass, id);
    }

    public T merge(T t) {
        return (T) entityManager.merge(t);
    }

    public void deleteById(Long id) {
        entityManager.remove(getById(id));
    }

    public Boolean twoParametersMethodToSearchEntity(String parameterType, String parameterName) {
        Long lon = (Long) entityManager.createQuery("select count(id) from "
                + persistentClass.getName() + " where " + parameterType + " ='" + parameterName + "'").getSingleResult();
        return lon > 0;
    }

    public Boolean haveEntityWithThisId(Long entityId) {
        Long lon = (Long) entityManager.createQuery("select count(id) from "
                + persistentClass.getName() + " where id =" + entityId).getSingleResult();
        return lon > 0;
    }

    public Boolean haveEntityWithEntityNameAndId(Long entityId, String parameterType, String entityName) {
        Long lon = (Long) entityManager.createQuery("select count(id) from " + persistentClass.getName() +
                " where " + parameterType + " ='" + entityName + "' and id = " + entityId).getSingleResult();
        return lon > 0;
    }

}
