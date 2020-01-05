package jm.dao;

import jm.api.dao.CreateWorkspaceTokenDAO;
import jm.model.CreateWorkspaceToken;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class CreateWorkspaceTokenDAOImpl extends AbstractDao<CreateWorkspaceToken> implements CreateWorkspaceTokenDAO {

    @Override
    public Optional<CreateWorkspaceToken> getCreateWorkspaceTokenByOwnerEmail(String email) {
            return Optional.ofNullable((CreateWorkspaceToken) entityManager.createNativeQuery("SELECT * FROM createWorkspaceToken WHERE ownerEmail=?", CreateWorkspaceToken.class)
                    .setParameter(1, email)
                    .getSingleResult());
    }

    @Override
    public Optional<CreateWorkspaceToken> getCreateWorkspaceTokenByCode(int code) {
            return Optional.ofNullable((CreateWorkspaceToken) entityManager.createNativeQuery("SELECT * FROM createWorkspaceToken WHERE code=?", CreateWorkspaceToken.class)
                    .setParameter(1, code)
                    .getSingleResult());
    }
}
