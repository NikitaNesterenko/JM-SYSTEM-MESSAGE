package jm.dao;

import jm.api.dao.InviteTokenDAO;
import jm.model.InviteToken;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public class InviteTokenDAOImpl extends AbstractDao<InviteToken> implements InviteTokenDAO {

    @Override
    public InviteToken getByHash(String hash) {
        if (twoParametersMethodToSearchEntity("hash", hash)) {
            return (InviteToken) entityManager.createQuery("from InviteToken where hash  = :hash")
                    .setParameter("hash", hash)
                    .getSingleResult();
        }
        return null;
    }
}
