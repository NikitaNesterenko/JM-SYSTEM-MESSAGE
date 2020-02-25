package jm.dao;

import jm.api.dao.CreateWorkspaceTokenDAO;
import jm.model.CreateWorkspaceToken;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class CreateWorkspaceTokenDAOImpl extends AbstractDao<CreateWorkspaceToken> implements CreateWorkspaceTokenDAO {

    @Override
    public Optional<CreateWorkspaceToken> getCreateWorkspaceTokenByOwnerEmail(String email) {
        try {
            return Optional.ofNullable((CreateWorkspaceToken) entityManager.createNativeQuery("select * from createWorkspaceToken where ownerEmail=?", CreateWorkspaceToken.class)
                    .setParameter(1, email)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CreateWorkspaceToken> getCreateWorkspaceTokenByCode(int code) {
        try {
            return Optional.ofNullable((CreateWorkspaceToken) entityManager.createNativeQuery("select * from createWorkspaceToken where code=?", CreateWorkspaceToken.class)
                    .setParameter(1, code)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
