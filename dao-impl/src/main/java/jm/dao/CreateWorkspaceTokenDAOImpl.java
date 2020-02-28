package jm.dao;

import jm.api.dao.CreateWorkspaceTokenDAO;
import jm.model.CreateWorkspaceToken;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Repository
@Transactional
public class CreateWorkspaceTokenDAOImpl extends AbstractDao<CreateWorkspaceToken> implements CreateWorkspaceTokenDAO {

    @Override
    public CreateWorkspaceToken getCreateWorkspaceTokenByOwnerEmail(String email) {
        try {
            return (CreateWorkspaceToken) entityManager.createNativeQuery("select * from create_workspace_token where user_email= :email", CreateWorkspaceToken.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public CreateWorkspaceToken getCreateWorkspaceTokenByCode(int code) {
        try {
            return (CreateWorkspaceToken) entityManager.createNativeQuery("select * from create_workspace_token where code= :code", CreateWorkspaceToken.class)
                    .setParameter("code", code)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
