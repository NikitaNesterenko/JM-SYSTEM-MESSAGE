package jm.dao;

import jm.api.dao.InviteTokenDAO;
import jm.model.InviteToken;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class InviteTokenDAOImpl extends AbstractDao<InviteToken> implements InviteTokenDAO {

    @Override
    public Optional<InviteToken> getByHash(String hash) {
            return Optional.ofNullable((InviteToken) entityManager.createQuery("FROM InviteToken WHERE hash  = :hash")
                    .setParameter("hash", hash)
                    .getSingleResult());
    }
}
