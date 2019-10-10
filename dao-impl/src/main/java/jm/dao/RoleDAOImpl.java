package jm.dao;

import jm.api.dao.RoleDAO;
import jm.model.Role;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
@Transactional
public class RoleDAOImpl implements RoleDAO {
    private static final Logger logger = LoggerFactory.getLogger(RoleDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addRole(String role) {
        entityManager.createNativeQuery("insert into roles (role) values ('" + role + "')").executeUpdate();
    }

    @Override
    public Role getRole(String role) {
        try {
            return (Role) entityManager.createQuery("from Role where role  = :role").setParameter("role", role).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
