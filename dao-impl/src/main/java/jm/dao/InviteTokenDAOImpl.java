package jm.dao;

import jm.api.dao.InviteTokenDAO;
import jm.model.InviteToken;
import jm.model.Message;
import jm.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class InviteTokenDAOImpl extends AbstractDao<InviteToken> implements InviteTokenDAO {

    @Override
    public InviteToken getByHash(String hash) {
        try {
            return (InviteToken) entityManager.createQuery("from InviteToken where hash  = :hash").setParameter("hash", hash).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
