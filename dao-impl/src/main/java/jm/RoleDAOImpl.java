package jm;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
@Transactional
public class RoleDAOImpl implements RoleDAO {

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

    @Override
    public void addRoleForUser(User user, String role) {
        Role userRole = (Role) entityManager.createQuery("from Role where role = :role").setParameter("role", role).getSingleResult();
        entityManager.createNativeQuery("insert into users_roles (user_id, role_id) values (" + user.getId() + ", " + userRole.getId() + ")").executeUpdate();
    }

    @Override
    public void updateUserRole(User user, String role) {
        Role userRole = (Role) entityManager.createQuery("from Role where role = :role").setParameter("role", role).getSingleResult();
        entityManager.createNativeQuery("insert into users_roles (user_id, role_id) values (" + user.getId() + ", " + userRole.getId() + ")").executeUpdate();
    }

}
