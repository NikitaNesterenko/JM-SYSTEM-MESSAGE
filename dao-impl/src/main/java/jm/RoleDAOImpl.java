package jm;

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
    public Role getRole(String roleName) {
        try {
            return (Role) entityManager.createNativeQuery("select * from Role where role='" + roleName + "'").getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
