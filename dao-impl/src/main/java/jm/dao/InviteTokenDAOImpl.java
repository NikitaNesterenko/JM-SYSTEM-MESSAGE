package jm.dao;

import io.swagger.v3.oas.integration.api.ObjectMapperProcessor;
import jm.api.dao.InviteTokenDAO;
import jm.model.InviteToken;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class InviteTokenDAOImpl extends AbstractDao<InviteToken> implements InviteTokenDAO {

    @Override
    public Optional<InviteToken> getByHash(String hash) {
        try {
            return Optional.ofNullable((InviteToken) entityManager.createQuery("from InviteToken where hash  = :hash").setParameter("hash", hash)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
