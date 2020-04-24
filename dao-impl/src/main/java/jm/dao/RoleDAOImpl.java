package jm.dao;

import jm.api.dao.RoleDAO;
import jm.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class RoleDAOImpl extends AbstractDao<Role> implements RoleDAO {
    private static final Logger logger = LoggerFactory.getLogger(RoleDAOImpl.class);

    @Override
    public Optional<Role> getRoleByRolename(String role) {
        try {
            Role roleObj = (Role) entityManager.createQuery("from Role where role  = :role").setParameter("role", role)
                    .getSingleResult();
            return Optional.of(roleObj);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
