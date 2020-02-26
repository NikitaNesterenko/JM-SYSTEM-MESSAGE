package jm.dao;

import jm.api.dao.InviteTokenDAO;
import jm.model.InviteToken;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Repository
@Transactional
public class InviteTokenDAOImpl extends AbstractDao<InviteToken> implements InviteTokenDAO {

    @Override
    public InviteToken getByHash(String hash) {
        try {
            return (InviteToken) entityManager.createQuery("SELECT i FROM InviteToken i WHERE i.hash  = :hash").setParameter("hash", hash)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
